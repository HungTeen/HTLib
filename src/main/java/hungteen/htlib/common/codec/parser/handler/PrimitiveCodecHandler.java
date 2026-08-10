package hungteen.htlib.common.codec.parser.handler;

import hungteen.htlib.common.codec.parser.*;

/**
 * 基本类型形态的 handler：解析 {@link com.mojang.serialization.codecs.PrimitiveCodec}。
 *
 * <p>PrimitiveCodec 是责任链的兜底：int / string / bool / byte / short / long /
 * float / double 等全部由 {@code Codec.INT} 这类单例实现。产出对应 {@link SchemaType}
 * 节点，并附带推断出的 Java 包装类型。</p>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:24
 **/
public final class PrimitiveCodecHandler implements CodecSchemaHandler {

    /** 命中条件：PrimitiveCodec 实例。 */
    @Override
    public boolean supports(Object codec) {
        return codec instanceof com.mojang.serialization.codecs.PrimitiveCodec<?>;
    }

    /** 解析基本类型：SchemaType（恒等判断）+ Java 值类型。 */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        DataSchema schema = new DataSchema(CodecUnwrapper.primitiveType(codec));

        schema.javaType(CodecUnwrapper.valueClass(codec));

        return schema;
    }
}
