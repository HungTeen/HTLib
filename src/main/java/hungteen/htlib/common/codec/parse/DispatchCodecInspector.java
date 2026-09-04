package hungteen.htlib.common.codec.parse;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.api.interfaces.IHTCommonRegistry;
import hungteen.htlib.api.interfaces.IHTRegistry;
import hungteen.htlib.util.ReflectionUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;

/**
 * Dispatch codec（{@code KeyDispatchCodec}）的"类型值 → 分支 codec"反射工具。
 *
 * <p>dispatch 的产物结构（DFU 6.0.8）：</p>
 * <ul>
 *   <li>{@code typeKey}：JSON 里区分分支的键名（默认 "type"）；</li>
 *   <li>{@code keyCodec}：类型值的 codec（枚举或注册表引用）；</li>
 *   <li>{@code decoder}：{@code Function<K, DataResult<Decoder<V>>>} —— <b>给定类型值返回该分支的 codec</b>。</li>
 * </ul>
 *
 * <p>因此"提前列举类型值并反查对应 codec"是可行的：</p>
 * <ol>
 *   <li>枚举类型值来源 —— 枚举常量（{@link CodecUnwrapper#enumClassOf}）优先，
 *       其次注册表条目（{@link CodecUnwrapper#findRegistry}）；</li>
 *   <li>对每个类型值调用 {@code decoder}，得到该分支的 codec 对象。</li>
 * </ol>
 *
 * <p>用途：{@link hungteen.htlib.common.codec.parse.handler.DispatchCodecHandler} 按 type 拆分 schema；命令按 type 反查分支 codec、
 * 列出所有可用类型等。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 12:30
 **/
public final class DispatchCodecInspector {

    private DispatchCodecInspector() {
    }

    /** 是否为 dispatch codec（KeyDispatchCodec）。 */
    public static boolean isDispatch(Object codec) {
        return CodecUnwrapper.isDispatchedMap(codec);
    }

    /** 取分派键名（typeKey），读不到用默认 "type"。 */
    public static String typeKey(Object codec) {

        Object typeKey = ReflectionUtil.getField(codec, "typeKey");

        return typeKey instanceof String s && !s.isEmpty() ? s : "type";
    }

    /**
     * 枚举所有"类型值 → 分支 codec"映射（保持枚举/注册表顺序）。
     *
     * <p>类型值名字：枚举用序列化名（{@code getSerializedName()}），注册表用键的 location。</p>
     */
    public static Map<String, Object> typeToCodec(Object codec) {

        Map<String, Object> map = new LinkedHashMap<>();

        if (!isDispatch(codec)) {
            return map;
        }

        Object keyCodec = ReflectionUtil.getField(codec, "keyCodec");

        Object decoder = ReflectionUtil.getField(codec, "decoder");

        if (!(decoder instanceof Function<?, ?> function)) {
            return map;
        }

        Object probe = keyCodec != null ? keyCodec : codec;

        // 1) 枚举常量。
        Class<?> enumClass = CodecUnwrapper.enumClassOf(probe);

        if (enumClass != null && enumClass.isEnum()) {

            for (Object constant : enumClass.getEnumConstants()) {

                Object branch = applyDecoder(function, constant);

                if (branch != null) {
                    map.put(serializedName(constant), branch);
                }
            }

            if (!map.isEmpty()) {
                return map;
            }
        }

        // 2) 注册表条目。
        Registry<?> registry = resolveRegistry(probe);

        if (registry != null) {

            for (var entry : registry.entrySet()) {

                Object branch = applyDecoder(function, entry.getValue());

                if (branch != null) {
                    map.put(entry.getKey().location().toString(), branch);
                }
            }

            if (!map.isEmpty()) {
                return map;
            }
        }

        // 3) HTLib 自定义注册表（byNameCodec 捕获注册表实例；含 HTCommonRegistry 与 HTCodecRegistry）。
        IHTRegistry<?> htRegistry = findHtRegistry(probe);

        if (htRegistry != null) {

            fillFromHtRegistry(map, function, htRegistry);

            if (!map.isEmpty()) {
                return map;
            }
        }

        return map;
    }

    /**
     * 从 HTLib 注册表枚举"类型名 → 分支 codec"。
     *
     * <p>{@link IHTCommonRegistry} 直接取 {@code getEntries()}；
     * {@link IHTCodecRegistry}（{@code HTCodecRegistry}，数据包注册表）取缓存键/值。
     * 名字统一用 {@link ResourceLocation}。</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void fillFromHtRegistry(Map<String, Object> map, Function<?, ?> function, IHTRegistry<?> registry) {

        if (registry instanceof IHTCommonRegistry<?> common) {

            for (Map.Entry<ResourceLocation, ?> entry : common.getEntries()) {

                Object branch = applyDecoder(function, entry.getValue());

                if (branch != null) {
                    map.put(entry.getKey().toString(), branch);
                }
            }

        } else if (registry instanceof IHTCodecRegistry<?> codecRegistry) {

            Set<ResourceKey<?>> keys = (Set) codecRegistry.getClientKeys();

            if (keys.isEmpty()) {

                // 服务端缓存键兜底。
                for (ResourceLocation key : (List<ResourceLocation>) codecRegistry.getCachedKeys()) {
                    keys.add(ResourceKey.create(registry.getRegistryKey(), key));
                }
            }

            for (ResourceKey<?> key : keys) {

                Object value = ((IHTCodecRegistry) codecRegistry).getClientOptValue(key).orElse(null);

                if (value == null) {
                    continue;
                }

                Object branch = applyDecoder(function, value);

                if (branch != null) {
                    map.put(key.location().toString(), branch);
                }
            }
        }
    }

    /**
     * 解析 dispatch keyCodec 对应的实际注册表。
     *
     * <p>优先级：</p>
     * <ol>
     *   <li>对象图内直接找 {@link Registry} 实例（HTLib byNameCodec 会捕获注册表）；</li>
     *   <li>按 {@link ResourceKey} 解析 —— 内置注册表走 {@link BuiltInRegistries}，
     *       数据驱动注册表走服务端 {@link SchemaRegistryAccess} 的 RegistryAccess。</li>
     * </ol>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Registry<?> resolveRegistry(Object probe) {

        Registry<?> direct = CodecUnwrapper.findRegistry(probe);

        if (direct != null) {
            return direct;
        }

        ResourceKey<?> key = CodecUnwrapper.registryKey(probe);

        if (key != null) {

            Registry<?> builtin = BuiltInRegistries.REGISTRY.get(key.location());

            if (builtin != null) {
                return builtin;
            }

            RegistryAccess access = SchemaRegistryAccess.get();

            if (access != null) {

                Optional<? extends Registry<?>> opt = access.registry((ResourceKey) key);

                if (opt.isPresent()) {
                    return opt.get();
                }
            }
        }

        return null;
    }

    /** BFS 找对象图中的第一个 HTLib 注册表实例（{@link IHTRegistry}，含 Common / Codec 两种）。 */
    private static IHTRegistry<?> findHtRegistry(Object root) {

        if (root == null) {
            return null;
        }

        Set<Object> seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

        java.util.Deque<Object> stack = new java.util.ArrayDeque<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !seen.add(current)) {
                continue;
            }

            if (current instanceof IHTRegistry<?> htRegistry) {
                return htRegistry;
            }

            for (java.lang.reflect.Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !CodecUnwrapper.isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return null;
    }

    /** 列出所有可用的类型值名字。 */
    public static List<String> listTypes(Object codec) {
        return List.copyOf(typeToCodec(codec).keySet());
    }

    /** 根据类型值名字反查对应分支 codec；不存在返回 empty。 */
    public static Optional<Object> codecForType(Object codec, String type) {

        if (type == null) {
            return Optional.empty();
        }

        for (Map.Entry<String, Object> entry : typeToCodec(codec).entrySet()) {

            if (entry.getKey().equalsIgnoreCase(type)) {
                return Optional.ofNullable(entry.getValue());
            }
        }

        return Optional.empty();
    }

    /**
     * 调用 decoder 函数拿到某类型值对应的 codec。
     *
     * <p>decoder 返回形态多样，这里统一收编：</p>
     * <ul>
     *   <li>{@code DataResult<? extends Decoder<?>>} —— {@code Codec.dispatch} 的标准形态；</li>
     *   <li>{@code DataResult<? extends MapCodec<?>>} —— MapCodec 分支的 dispatch；</li>
     *   <li>直接返回 {@code Decoder} / {@code MapCodec}（个别实现不包 DataResult）。</li>
     * </ul>
     * <p>解包后返回 codec 对象（可被责任链解析）；失败返回 null。</p>
     */
    @SuppressWarnings("unchecked")
    private static Object applyDecoder(Function<?, ?> decoder, Object typeValue) {

        try {

            Object applied = ((Function<Object, ?>) decoder).apply(typeValue);

            if (applied instanceof DataResult<?> result) {

                Optional<?> optional = result.result();

                if (optional.isPresent()) {

                    Object value = optional.get();

                    if (value instanceof Decoder<?> || value instanceof com.mojang.serialization.MapCodec<?>) {
                        return value;
                    }
                }

            } else if (applied instanceof Decoder<?> || applied instanceof com.mojang.serialization.MapCodec<?>) {

                return applied;
            }

        } catch (Throwable ignored) {
        }

        return null;
    }

    /** 枚举常量的序列化名（getSerializedName 优先，失败退回 name()）。 */
    private static String serializedName(Object constant) {

        if (constant instanceof Enum<?> e) {

            try {

                java.lang.reflect.Method method = constant.getClass().getMethod("getSerializedName");

                Object value = method.invoke(constant);

                if (value != null) {
                    return String.valueOf(value);
                }

            } catch (Throwable ignored) {
            }

            return e.name();
        }

        return String.valueOf(constant);
    }
}
