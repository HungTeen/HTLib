package hungteen.htlib.common.codec.parser.handler;

import com.mojang.serialization.codecs.ListCodec;
import hungteen.htlib.common.codec.parser.*;

import java.util.List;

/**
 * 列表形态的 handler：解析 {@link ListCodec}。
 *
 * <p>产出 {@link SchemaType#LIST} 节点：元素 Schema 走解析器责任链；
 * 若 codec 显式声明了长度范围（minSize/maxSize），附加 {@link SchemaConstraint.Size} 约束。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:24
 **/
public final class ListCodecHandler implements CodecSchemaHandler {

    /** 命中条件：ListCodec 实例。 */
    @Override
    public boolean supports(Object codec) {
        return codec instanceof ListCodec<?>;
    }

    /** 解析列表：元素 Schema + 长度约束。 */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        DataSchema schema = new DataSchema(SchemaType.LIST);

        schema.javaType(List.class);

        int[] size = CodecUnwrapper.listSize(codec);

        // 只有显式声明长度范围（非全开默认）时才加约束。
        if (size[0] > 0 || size[1] != Integer.MAX_VALUE) {
            schema.constraints().add(new SchemaConstraint.Size(size[0], size[1]));
        }

        DataSchema element = context.resolve(CodecUnwrapper.listElement(codec));

        if (element != null) {
            schema.element(element);
        }

        return schema;
    }
}
