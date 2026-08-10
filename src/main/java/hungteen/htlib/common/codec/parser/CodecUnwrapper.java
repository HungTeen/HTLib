package hungteen.htlib.common.codec.parser;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import hungteen.htlib.util.ReflectionUtil;
import hungteen.htlib.util.helper.CodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Supplier;

/**
 * DFU / MC Codec 对象图的结构反射工具集，为解析器提供统一的能力。
 *
 * <p>由于 DFU 的 codec 大量使用私有字段、匿名类与 lambda 捕获，无法靠类型安全地访问，
 * 这里以"实测结构"为基础，用反射 + 公开 API 混合的方式读取内部结构。所有方法都是
 * 尽力而为：读不到就返回 null / 默认值，绝不抛异常。</p>
 *
 * <p>基于 26.1 (DFU 9.0.19) 的实测结构：</p>
 * <ul>
 *   <li>根：{@code MapCodec$MapCodecCodec}（public record，字段 {@code codec}）→ 内部 MapCodec；</li>
 *   <li>记录：{@code RecordCodecBuilder$2}（MapCodec 匿名类，字段 {@code val$builder}，
 *       用 {@code getEnclosingClass() == RecordCodecBuilder.class} 识别）；</li>
 *   <li>可选字段：{@code OptionalFieldCodec}（字段 {@code name}/{@code elementCodec}）；</li>
 *   <li>必填字段：{@code MapCodec$2}（字段 {@code val$encoder}/{@code val$decoder}/{@code val$name}）；</li>
 *   <li>递归包装：{@code Codec$RecursiveCodec} / {@code MapCodec$RecursiveMapCodec}（包私有/私有类，
 *       只能按类名识别，见 {@link #isRecursive}）；</li>
 *   <li>列表：{@code ListCodec}（公开方法 {@code elementCodec()/minSize()/maxSize()}）；</li>
 *   <li>基础类型：{@link PrimitiveCodec} 实例（用 {@code Codec.INT} 等恒等判断）；</li>
 *   <li>注册表：{@code RegistryFileCodec / RegistryFixedCodec / HolderSetCodec / Holder}（均可 instanceof）。</li>
 * </ul>
 *
 * <p>能力划分（按类内分区注释）：包装解包 / 记录与字段 / 元素 codec / 列表 / 范围 /
 * 类型推断 / 注册表 / 反射工具。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:22
 **/
public final class CodecUnwrapper {

    private static final DynamicOps<JsonElement> OPS = JsonOps.INSTANCE;

    /**
     * 递归包装 codec（DFU 9.0.19）。{@code Codec.RecursiveCodec} 为包私有、
     * {@code MapCodec.RecursiveMapCodec} 为私有嵌套类，跨包无法 instanceof，只能按类名识别。
     */
    private static final String RECURSIVE_CODEC = "com.mojang.serialization.Codec$RecursiveCodec";

    private static final String RECURSIVE_MAP_CODEC = "com.mojang.serialization.MapCodec$RecursiveMapCodec";

    private CodecUnwrapper() {
    }

    // -------------------------------------------------
    // 包装解包
    // -------------------------------------------------

    /**
     * 剥掉包装：MapCodecCodec、Codec.RecursiveCodec、MapCodec.RecursiveMapCodec，
     * 循环穿透直到拿到真实结构 codec。
     */
    public static Object unwrap(Object codec) {

        if (codec == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Object current = codec;

        while (current != null && seen.add(current)) {

            Object next = unwrapOnce(current);

            if (next == null || next == current) {
                return current;
            }

            current = next;
        }

        return current;
    }

    private static Object unwrapOnce(Object codec) {

        if (codec instanceof MapCodec.MapCodecCodec<?> mc) {

            MapCodec<?> inner = mc.codec();

            return inner != null ? inner : codec;
        }

        if (isRecursive(codec)) {

            Object supplier = ReflectionUtil.getField(codec, "wrapped");

            if (supplier instanceof Supplier<?> sup) {

                try {

                    Object inner = sup.get();

                    return inner != null && inner != codec ? inner : codec;

                } catch (Throwable ignored) {
                    return codec;
                }
            }

            return codec;
        }

        return codec;
    }

    private static boolean isRecursive(Object codec) {

        String name = codec.getClass().getName();

        return name.equals(RECURSIVE_CODEC) || name.equals(RECURSIVE_MAP_CODEC);
    }

    // -------------------------------------------------
    // 记录 / 字段
    // -------------------------------------------------

    /** 是否为记录 MapCodec（RecordCodecBuilder 构建产物，匿名类 -> 用封闭类识别）。 */
    public static boolean isRecordMapCodec(Object codec) {

        return codec instanceof MapCodec<?> && codec.getClass().getEnclosingClass() == RecordCodecBuilder.class;
    }

    /**
     * 取记录的权威字段名（去重、保持顺序）。
     */
    public static List<String> fieldKeys(Object codec) {

        List<String> keys = new ArrayList<>();

        try {

            if (codec instanceof MapCodec<?> mc) {

                Iterator<JsonElement> it = mc.keys(OPS).iterator();

                while (it.hasNext()) {

                    Optional<String> name = OPS.getStringValue(it.next()).result();

                    if (name.isPresent() && !keys.contains(name.get())) {
                        keys.add(name.get());
                    }
                }
            }

        } catch (Throwable ignored) {
        }

        return keys;
    }

    /**
     * 取字段名：优先 {@code name} 字段 / {@code FieldDecoder#name} / {@code val$name} Supplier（需形如
     * Field[..]/OptionalFieldCodec[..]）；orElse/mapResult 等包装（this$0）逐层穿透。
     */
    public static String fieldName(Object codec) {
        return fieldName(codec, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private static String fieldName(Object codec, Set<Object> seen) {

        if (codec == null || !seen.add(codec)) {
            return null;
        }

        String direct = fieldNameDirect(codec);

        if (direct != null) {
            return direct;
        }

        Object self = ReflectionUtil.getField(codec, "this$0");

        if (self != null && self != codec) {
            return fieldName(self, seen);
        }

        return null;
    }

    /**
     * 直接从对象本身取字段名，不穿透 this$0 包装：
     * 依次尝试 {@code name} 字段、{@code val$decoder.name}（FieldDecoder）、
     * {@code val$name} Supplier（需解析出的文本形如 {@code Field[..]/OptionalFieldCodec[..]}）。
     */
    private static String fieldNameDirect(Object codec) {

        if (codec == null) {
            return null;
        }

        Object name = ReflectionUtil.getField(codec, "name");

        if (name instanceof String string) {
            return string;
        }

        Object decoder = ReflectionUtil.getField(codec, "val$decoder");

        Object decodedName = decoder == null ? null : ReflectionUtil.getField(decoder, "name");

        if (decodedName instanceof String string) {
            return string;
        }

        Object supplier = ReflectionUtil.getField(codec, "val$name");

        String resolved = resolveSupplier(supplier);

        if (resolved != null && (resolved.startsWith("Field[") || resolved.startsWith("OptionalFieldCodec["))) {

            return extractName(resolved);
        }

        return null;
    }

    /**
     * 从 {@code Field[name:..] / OptionalFieldCodec[name:..]} 这类 toString 文本中提取字段名。
     */
    private static String extractName(String text) {

        int bracket = text.indexOf('[');

        int colon = text.indexOf(':', bracket);

        if (bracket >= 0 && colon > bracket) {

            return text.substring(bracket + 1, colon).trim();
        }

        return null;
    }

    /**
     * 是否为可选字段：OptionalFieldCodec 本体或其 xmap 包装 （toString 以 {@code OptionalFieldCodec[} 开头）。
     */
    public static boolean isOptionalField(Object codec) {

        if (codec == null) {
            return false;
        }

        if (codec instanceof OptionalFieldCodec<?>) {
            return true;
        }

        String text = codec.toString();

        return text != null && text.startsWith("OptionalFieldCodec[");
    }

    /**
     * 是否为字段 MapCodec（必填 Field[..] 或可选 OptionalFieldCodec[..]）。
     */
    public static boolean isFieldCodec(Object codec) {

        if (codec == null) {
            return false;
        }

        if (isOptionalField(codec)) {
            return true;
        }

        String text = codec.toString();

        return text != null && text.startsWith("Field[");
    }

    /**
     * 取字段的单一 key：名字确定则返回名字；只有 1 个 key 才返回该 key，否则 null。
     */
    public static String fieldKey(Object codec) {

        String name = fieldName(codec);

        if (name != null) {
            return name;
        }

        List<String> keys = fieldKeys(codec);

        return keys.size() == 1 ? keys.get(0) : null;
    }

    /**
     * BFS 收集对象图中所有的 MapCodec（不含根），用于解析记录的字段。
     */
    public static List<Object> collectFieldMapCodecs(Object root) {

        List<Object> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current instanceof MapCodec<?> && current != root) {
                result.add(current);
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return result;
    }

    // -------------------------------------------------
    // 元素 codec
    // -------------------------------------------------

    /**
     * 取包装 codec 内部的元素 codec： 方法 {@code elementCodec()} → 字段 {@code elementCodec} → {@code val$decoder} → BFS 扫描。
     */
    public static Object elementCodec(Object codec) {

        if (codec == null) {
            return null;
        }

        Object method = invokeNoArg(codec, "elementCodec");

        if (method != null) {
            return method;
        }

        Object field = ReflectionUtil.getField(codec, "elementCodec");

        if (field != null) {
            return field;
        }

        Object decoder = ReflectionUtil.getField(codec, "val$decoder");

        if (decoder != null) {

            Object decoded = ReflectionUtil.getField(decoder, "elementCodec");

            if (decoded != null) {
                return decoded;
            }
        }

        return scanCodec(codec);
    }

    /** BFS 找对象图中第一个 Codec 类型字段值。 */
    public static Object scanCodec(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    field.setAccessible(true);

                    Object value = field.get(current);

                    if (value instanceof Codec<?>) {
                        return value;
                    }

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    /**
     * 找对象图中第一个可解析的“结构型”基础 codec（用于穿透 xmap / flatXmap / validate
     * 等变换包装后重新分发）。枚举不在此列，避免 BFS 误判。
     */
    public static Object scanBaseCodec(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current != root && isBaseCodec(current)) {
                return current;
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    /** 是否为可被 handler 链直接解析的基础结构 codec。 */
    public static boolean isBaseCodec(Object codec) {

        if (codec == null) {
            return false;
        }

        if (codec instanceof ListCodec<?>) {
            return true;
        }

        if (codec instanceof UnboundedMapCodec<?, ?>
            || codec instanceof SimpleMapCodec<?, ?>
            || isDispatchedMap(codec)) {
            return true;
        }

        if (isRecordMapCodec(codec)) {
            return true;
        }

        if (codec instanceof PrimitiveCodec<?> && primitiveType(codec) != SchemaType.UNKNOWN) {
            return true;
        }

        return codec instanceof HolderSetCodec<?>
            || codec instanceof RegistryFileCodec<?>
            || codec instanceof RegistryFixedCodec<?>
            || codec instanceof Holder<?>;
    }

    /** 是否为 DispatchedMapCodec（键确定、值随键分发的 Map）。 */
    public static boolean isDispatchedMap(Object codec) {

        return codec instanceof KeyDispatchCodec<?,?>;
    }

    /** BFS 找对象图中第一个基本类型 codec（PrimitiveCodec 实例）。 */
    public static Object findPrimitive(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    field.setAccessible(true);

                    Object value = field.get(current);

                    if (value instanceof PrimitiveCodec<?>) {
                        return value;
                    }

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    // -------------------------------------------------
    // 可选 / 默认值
    // -------------------------------------------------

    /** 是否为可选字段（OptionalFieldCodec）。 */
    public static boolean isOptional(Object codec) {
        return codec instanceof OptionalFieldCodec<?>;
    }

    /**
     * 用空 Map 解码探测默认值：成功说明缺失时取默认值（可选），失败说明必填。
     */
    public static ValueInfo decodeEmptyDefault(Object codec) {

        try {

            if (codec instanceof MapCodec<?> mc) {

                DataResult<?> result = mc.decode(OPS, CodecHelper.emptyMapLike());

                Optional<?> present = result.result();

                if (present.isPresent()) {
                    return new ValueInfo.Static(present.get());
                }
            }

        } catch (Throwable ignored) {
        }

        return new ValueInfo.Absent();
    }

    // -------------------------------------------------
    // 列表
    // -------------------------------------------------

    /** 取 ListCodec 的元素 codec。 */
    public static Object listElement(Object codec) {

        if (codec == null) {
            return null;
        }

        Object method = invokeNoArg(codec, "elementCodec");

        if (method != null) {
            return method;
        }

        return ReflectionUtil.getField(codec, "elementCodec");
    }

    /** 取 ListCodec 的 [minSize, maxSize]，读不到用全开默认值。 */
    public static int[] listSize(Object codec) {

        Integer minSize = null;

        Integer maxSize = null;

        Object min = invokeNoArg(codec, "minSize");

        if (min instanceof Number number) {
            minSize = number.intValue();
        }

        Object max = invokeNoArg(codec, "maxSize");

        if (max instanceof Number number) {
            maxSize = number.intValue();
        }

        if (minSize == null) {

            Object field = ReflectionUtil.getField(codec, "minSize");

            if (field instanceof Number number) {
                minSize = number.intValue();
            }
        }

        if (maxSize == null) {

            Object field = ReflectionUtil.getField(codec, "maxSize");

            if (field instanceof Number number) {
                maxSize = number.intValue();
            }
        }

        return new int[] {minSize == null ? 0 : minSize, maxSize == null ? Integer.MAX_VALUE : maxSize};
    }

    // -------------------------------------------------
    // 范围
    // -------------------------------------------------

    /**
     * 探测数值范围：先找 min/max 命名字段，再找数值型 lambda 捕获（checkRange 闭包）。
     */
    public static double[] findRange(Object elem) {

        if (elem == null) {
            return null;
        }

        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        return findRange(elem, visited, 0);
    }

    private static double[] findRange(Object obj, Set<Object> visited, int depth) {

        if (obj == null || depth > 4 || !visited.add(obj)) {
            return null;
        }

        if (isDfuObject(obj.getClass())) {

            Double min = null;

            Double max = null;

            for (Field field : ReflectionUtil.fields(obj.getClass())) {

                String name = field.getName();

                if (!name.startsWith("min") && !name.startsWith("max")) {
                    continue;
                }

                if (name.equals("minSize") || name.equals("maxSize")) {
                    continue;
                }

                try {

                    field.setAccessible(true);

                    if (field.get(obj) instanceof Number number) {

                        if (name.startsWith("min")) {
                            min = number.doubleValue();
                        } else {
                            max = number.doubleValue();
                        }
                    }

                } catch (Throwable ignored) {
                }
            }

            if (min != null && max != null && min <= max) {
                return new double[] {min, max};
            }
        }

        if (isLambda(obj)) {

            Double first = null;

            Double second = null;

            for (Field field : ReflectionUtil.fields(obj.getClass())) {

                try {

                    field.setAccessible(true);

                    if (field.get(obj) instanceof Number number) {

                        if (first == null) {
                            first = number.doubleValue();
                        } else if (second == null) {
                            second = number.doubleValue();
                        }
                    }

                } catch (Throwable ignored) {
                }
            }

            if (first != null && second != null) {
                return new double[] {Math.min(first, second), Math.max(first, second)};
            }
        }

        for (Field field : ReflectionUtil.fields(obj.getClass())) {

            try {

                field.setAccessible(true);

                double[] range = findRange(field.get(obj), visited, depth + 1);

                if (range != null) {
                    return range;
                }

            } catch (Throwable ignored) {
            }
        }

        return null;
    }

    // -------------------------------------------------
    // 类型推断
    // -------------------------------------------------

    /** 取包装 codec 中的 Map 键 codec（UnboundedMapCodec / SimpleMapCodec）。 */
    public static Object keyCodec(Object codec) {

        if (codec == null) {
            return null;
        }

        Object method = invokeNoArg(codec, "keyCodec");

        if (method != null) {
            return method;
        }

        return ReflectionUtil.getField(codec, "keyCodec");
    }

    /** 基本类型 codec → SchemaType。 */
    public static SchemaType primitiveType(Object codec) {

        if (codec == null) {
            return SchemaType.UNKNOWN;
        }

        if (codec == Codec.BOOL)
            return SchemaType.BOOLEAN;
        if (codec == Codec.BYTE)
            return SchemaType.BYTE;
        if (codec == Codec.SHORT)
            return SchemaType.SHORT;
        if (codec == Codec.INT)
            return SchemaType.INT;
        if (codec == Codec.LONG)
            return SchemaType.LONG;
        if (codec == Codec.FLOAT)
            return SchemaType.FLOAT;
        if (codec == Codec.DOUBLE)
            return SchemaType.DOUBLE;
        if (codec == Codec.STRING)
            return SchemaType.STRING;
        if (codec == Codec.BYTE_BUFFER)
            return SchemaType.UNKNOWN;
        if (codec == Codec.INT_STREAM || codec == Codec.LONG_STREAM)
            return SchemaType.UNKNOWN;

        return SchemaType.UNKNOWN;
    }

    /**
     * 推断值类型 Class：基本类型 → 包装类；列表 → List；可选 → Optional； 枚举 → 枚举类；其余 null。
     */
    public static Class<?> valueClass(Object codec) {

        if (codec == null) {
            return null;
        }

        if (codec == Codec.INT)
            return Integer.class;
        if (codec == Codec.LONG)
            return Long.class;
        if (codec == Codec.FLOAT)
            return Float.class;
        if (codec == Codec.DOUBLE)
            return Double.class;
        if (codec == Codec.STRING)
            return String.class;
        if (codec == Codec.BOOL)
            return Boolean.class;
        if (codec == Codec.BYTE)
            return Byte.class;
        if (codec == Codec.SHORT)
            return Short.class;
        if (codec == Codec.BYTE_BUFFER)
            return ByteBuffer.class;

        if (codec instanceof ListCodec<?>) {
            return List.class;
        }

        if (codec instanceof OptionalFieldCodec<?>) {
            return Optional.class;
        }

        Class<?> enumClass = enumClassOf(codec);

        return enumClass;
    }

    /** BFS 找枚举类（枚举数组的组件类型或 Class 类型字段）。 */
    public static Class<?> enumClassOf(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current instanceof Class<?> clazz && clazz.isEnum()) {
                return clazz;
            }

            if (current.getClass().isArray() && current.getClass().getComponentType().isEnum()) {

                return current.getClass().getComponentType();
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    /** 枚举值列表（枚举数组 → 常量遍历）。 */
    public static List<EnumValueSchema> enumValues(Object root) {

        List<EnumValueSchema> result = new ArrayList<>();

        Object array = findEnumArray(root);

        if (array instanceof Object[] values) {

            for (Object value : values) {

                result.add(enumSchema(value));
            }

            if (!result.isEmpty()) {
                return result;
            }
        }

        Class<?> enumClass = enumClassOf(root);

        if (enumClass != null) {

            for (Object constant : enumClass.getEnumConstants()) {

                result.add(enumSchema(constant));
            }
        }

        return result;
    }

    // -------------------------------------------------
    // 注册表
    // -------------------------------------------------

    /**
     * BFS 找对象图中的第一个 {@link Registry} 实例（用于枚举注册表条目）。
     *
     * <p>registryKey 只用 key，这里返回实例本身，供 DispatchCodecHandler 枚举 dispatch 的类型值。</p>
     */
    public static Registry<?> findRegistry(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current instanceof Registry<?> registry) {
                return registry;
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    /** 取 codec 中的 registryKey：先找 {@code registryKey} 命名字段，再找捕获的 Registry 实例调 {@code key()}。 */
    public static ResourceKey<?> registryKey(Object root) {
        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current instanceof Registry<?> registry) {

                try {

                    ResourceKey<? extends Registry<?>> key = registry.key();

                    if (key != null) {
                        return key;
                    }

                } catch (Throwable ignored) {
                }
            }

            Object key = ReflectionUtil.getField(current, "registryKey");

            if (key instanceof ResourceKey<?> resourceKey) {
                return resourceKey;
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    // -------------------------------------------------
    // 反射 / 工具
    // -------------------------------------------------

    /**
     * 按全限定类名精确匹配对象类型。
     *
     * @param object    待判断对象
     * @param className 全限定类名
     * @return 对象非空且类名完全一致时返回 true
     */
    public static boolean isClass(Object object, String className) {

        return object != null && object.getClass().getName().equals(className);
    }

    /**
     * 是否为 DFU 包下的对象（{@code com.mojang.serialization} / {@code com.mojang.datafixers}）。
     *
     * <p>包名从 {@link Codec} 与 {@link DataFixUtils} 两个已知类派生，避免硬编码。
     * 用于范围探测等场景：只对 DFU 对象做结构性反射，降低误判面。</p>
     */
    public static boolean isDfuObject(Class<?> clazz) {

        String name = clazz.getName();

        return name.startsWith(Codec.class.getPackageName()) || name.startsWith(DataFixUtils.class.getPackageName());
    }

    /**
     * 是否为 lambda 捕获对象。
     *
     * <p>lambda 编译产物类名形如 {@code Foo$$Lambda$1/0x...}（JVM 约定）。
     * 用于从 lambda 捕获字段中提取数值（如 checkRange 闭包捕获的 min/max）。</p>
     */
    public static boolean isLambda(Object object) {

        return object != null && object.getClass().getName().contains("$$Lambda");
    }

    /**
     * 是否为终端叶子对象（无需继续反射深入）：
     * 字符串 / 数字 / 布尔 / 字符 / 枚举实例。
     *
     * <p>注意：Class 与数组不在此列（枚举探测依赖它们）。</p>
     */
    public static boolean isTerminal(Object object) {

        return object == null || object instanceof String || object instanceof Number || object instanceof Boolean || object instanceof Character || object instanceof Enum<?>;
    }

    /**
     * 若对象是 {@link Supplier}，求值并转成字符串；否则返回 null。
     *
     * <p>用于解析 {@code val$name} 等 Supplier 字段（如字段名 Supplier），
     * 求值失败（抛异常）时安静返回 null。</p>
     */
    public static String resolveSupplier(Object supplier) {

        if (supplier instanceof Supplier<?> sup) {

            try {

                Object value = sup.get();

                return value == null ? null : String.valueOf(value);

            } catch (Throwable ignored) {
            }
        }

        return null;
    }

    /**
     * 沿继承树反射调用无参方法（含私有方法）。
     *
     * <p>用于读取 DFU 公开/私有方法如 {@code elementCodec()}、{@code minSize()}。
     * 子类没有就往父类找；调用失败（方法缺失 / 抛异常）安静返回 null。</p>
     *
     * @param object     目标对象
     * @param methodName 方法名
     * @return 方法返回值；失败返回 null
     */
    public static Object invokeNoArg(Object object, String methodName) {

        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();

        while (clazz != null && clazz != Object.class) {

            try {

                java.lang.reflect.Method method = clazz.getDeclaredMethod(methodName);

                method.setAccessible(true);

                return method.invoke(object);

            } catch (NoSuchMethodException ignored) {

                // 当前类没有该方法，去父类找。
                clazz = clazz.getSuperclass();

            } catch (Throwable ignored) {

                // 方法存在但调用失败（如抛异常），放弃。
                return null;
            }
        }

        return null;
    }

    /** BFS 在对象图中找第一个枚举数组（组件类型为 enum）。 */
    private static Object findEnumArray(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current.getClass().isArray() && current.getClass().getComponentType().isEnum()) {

                return current;
            }

            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    /**
     * 把单个枚举常量转成 {@link EnumValueSchema}：
     * Java 常量名取 {@code name()}，序列化名优先取 {@code getSerializedName()}（没有则退回常量名）。
     */
    private static EnumValueSchema enumSchema(Object value) {

        String name = value instanceof Enum<?> anEnum ? anEnum.name() : String.valueOf(value);

        Object serialized = invokeNoArg(value, "getSerializedName");

        return new EnumValueSchema(name, serialized instanceof String string ? string : name);
    }
}
