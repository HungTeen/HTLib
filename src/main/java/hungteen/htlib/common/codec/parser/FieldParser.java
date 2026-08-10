package hungteen.htlib.common.codec.parser;

import com.mojang.serialization.MapCodec;
import hungteen.htlib.common.codec.parser.handler.RecordCodecHandler;

import java.util.Optional;

/**
 * 字段级 MapCodec → {@link FieldSchema} 的解析器（被 {@link RecordCodecHandler} 调用）。
 *
 * <p>字段结构在 26.1 (DFU 9.0.19) 有两种实现形态：必填的 {@code MapCodec$2}
 * （字段 {@code val$encoder/val$decoder/val$name}）与可选的 {@code OptionalFieldCodec}。
 * 解析流程：</p>
 * <ol>
 *   <li>{@link CodecUnwrapper#fieldName} 取权威字段名（穿透 this$0 包装链）；</li>
 *   <li>{@link CodecUnwrapper#isOptionalField} 判定可选性；</li>
 *   <li>解析元素 codec → 经 {@link ParseContext#resolve} 走解析器责任链得到元素 Schema；</li>
 *   <li>数值字段尽力探测范围（{@link CodecUnwrapper#findRange}）并附加 Range 约束；</li>
 *   <li>可选字段用 {@link SchemaType#OPTIONAL} 包一层；</li>
 *   <li>{@link CodecUnwrapper#decodeEmptyDefault} 探测缺失默认值。</li>
 * </ol>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:24
 **/
public final class FieldParser {

    private FieldParser() {
    }

    /**
     * 解析一个字段级 MapCodec。
     *
     * @param codec   字段级 MapCodec（必填 Field 或可选 OptionalFieldCodec）
     * @param context 共享解析上下文
     * @return 字段 Schema；字段名缺失/为空时返回 null（调用方忽略该字段）
     */
    public static FieldSchema parse(MapCodec<?> codec, ParseContext context) {

        String name = CodecUnwrapper.fieldName(codec);

        if (name == null || name.isEmpty()) {
            return null;
        }

        boolean optional = CodecUnwrapper.isOptionalField(codec);

        String path = context.path().isEmpty() ? name : context.path() + "." + name;

        context.push(name);

        Object elementCodec;

        DataSchema schema;

        try {

            elementCodec = CodecUnwrapper.elementCodec(codec);

            schema = context.resolve(elementCodec);

        } finally {

            context.pop();
        }

        if (schema == null) {
            schema = new DataSchema(SchemaType.UNKNOWN);
        }

        // 数值字段：尽力探测 min/max 并附加约束（探测结果不可用时自动跳过）。
        if (isNumeric(schema.type())) {

            double[] range = CodecUnwrapper.findRange(elementCodec);

            if (range != null) {

                schema = schema.copy();

                schema.constraints().add(new SchemaConstraint.Range(range[0], range[1]));
            }
        }

        // 可选字段：用 OPTIONAL 包装元素 Schema。
        if (optional) {

            DataSchema wrapped = new DataSchema(SchemaType.OPTIONAL);

            wrapped.element(schema);

            wrapped.javaType(Optional.class);

            schema = wrapped;
        }

        FieldSchema field = new FieldSchema(name, schema, !optional, path);

        ValueInfo info = CodecUnwrapper.decodeEmptyDefault(codec);

        // 只有真正存在默认值（且不是空的 Optional）时才记录。
        if (info instanceof ValueInfo.Static staticInfo
            && !(staticInfo.value() instanceof Optional<?> empty && empty.isEmpty())) {

            field.defaultValue(info);
        }

        return field;
    }

    /** 仅数值形态才做范围探测。 */
    private static boolean isNumeric(SchemaType type) {

        return type == SchemaType.INT
            || type == SchemaType.LONG
            || type == SchemaType.FLOAT
            || type == SchemaType.DOUBLE;
    }
}
