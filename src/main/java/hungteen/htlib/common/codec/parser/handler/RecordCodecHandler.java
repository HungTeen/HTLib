package hungteen.htlib.common.codec.parser.handler;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.common.codec.parser.*;
import hungteen.htlib.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 记录（对象）形态的 handler：解析 RecordCodecBuilder 构建出的 MapCodec。
 *
 * <p>识别：{@link CodecUnwrapper#isRecordMapCodec}（{@code MapCodec} 且封闭类为
 * {@code RecordCodecBuilder}，即其构建产物的匿名类）。</p>
 *
 * <p>解析思路：记录结构里真正保存字段的是一棵"字段 MapCodec 树"，
 * 入口在 {@code val$builder.encoder}。用 BFS 遍历这棵树：</p>
 * <ul>
 *   <li>遇到字段级 MapCodec（{@link CodecUnwrapper#isFieldCodec}）→ 交给 {@link FieldParser} 解析成字段；</li>
 *   <li><b>不深入任何 MapCodec 内部</b>：字段的值类型统一由 FieldParser → 解析器责任链解析，
 *       避免把嵌套 Record 的内部字段误并到当前记录（外层先命中，交给子解析）。</li>
 * </ul>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:23
 **/
public final class RecordCodecHandler implements CodecSchemaHandler {

    /** 命中条件：RecordCodecBuilder 构建产物（记录 MapCodec）。 */
    @Override
    public boolean supports(Object codec) {
        return CodecUnwrapper.isRecordMapCodec(codec);
    }

    /**
     * 解析记录：遍历 encoder 中的字段 MapCodec 树，逐字段生成 FieldSchema。
     */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        DataSchema schema = new DataSchema(SchemaType.RECORD);

        // 记录构建器持有 encoder / decoder 树，字段 MapCodec 从 encoder 侧收集。
        Object builder = ReflectionUtil.getField(codec, "val$builder");

        if (builder == null) {
            return schema;
        }

        Object encoder = ReflectionUtil.getField(builder, "encoder");

        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        Deque<Object> stack = new ArrayDeque<>();

        if (encoder != null) {
            stack.push(encoder);
        }

        while (!stack.isEmpty()) {

            Object current = stack.pop();

            if (current == null || !visited.add(current)) {
                continue;
            }

            if (current instanceof MapCodec<?> mapCodec) {

                if (CodecUnwrapper.isFieldCodec(current)) {

                    FieldSchema field = FieldParser.parse(mapCodec, context);

                    if (field != null) {
                        schema.fields().add(field);
                    }
                }

                /*
                 * 不深入任何 MapCodec 内部：
                 * 字段类型统一由 FieldParser → resolver 链式解析，
                 * 避免把嵌套 Record 的字段误并到当前记录。
                 */
                continue;
            }

            // 非 MapCodec（可能是 lambda / 包装对象）：继续深入其字段。
            for (Field field : ReflectionUtil.fields(current.getClass())) {

                try {

                    Object value = field.get(current);

                    if (value != null && !CodecUnwrapper.isTerminal(value)) {
                        stack.push(value);
                    }

                } catch (Throwable ignored) {
                }
            }
        }

        return schema;
    }
}
