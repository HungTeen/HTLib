package hungteen.htlib.common.codec.parser;

/**
 * 枚举形态的 handler：解析持有枚举类引用的 codec。
 *
 * <p>枚举 codec（如 {@code Codec.enumCodec(X.class)} / 1.21 前的 NamedEnumCodec）通常把
 * 枚举数组或 {@code Class} 字段捕获在对象图里，无法直接 instanceof 到公共类，
 * 所以用 {@link CodecUnwrapper#enumClassOf} 在对象图里 BFS 定位枚举类。</p>
 *
 * <p>产出 {@link SchemaType#ENUM} 节点：附带全部枚举值（Java 名 + 序列化名），
 * 供编辑器提供下拉选项。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:24
 **/
public final class EnumCodecHandler implements CodecSchemaHandler {

    /** 命中条件：对象图里能定位到枚举类。 */
    @Override
    public boolean supports(Object codec) {
        return codec != null && CodecUnwrapper.enumClassOf(codec) != null;
    }

    /** 解析枚举：类型 + 全部枚举值。 */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        Class<?> enumClass = CodecUnwrapper.enumClassOf(codec);

        DataSchema schema = new DataSchema(SchemaType.ENUM);

        schema.javaType(enumClass);

        schema.enumValues().addAll(CodecUnwrapper.enumValues(codec));

        return schema;
    }
}
