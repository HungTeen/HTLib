package hungteen.htlib.common.codec.parser.handler;

import com.mojang.serialization.codecs.SimpleMapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import hungteen.htlib.common.codec.parser.*;

import java.util.Map;

/**
 * 映射形态的 handler：解析三种 Map codec。
 *
 * <p>命中条件：</p>
 * <ul>
 *   <li>{@link UnboundedMapCodec} —— 键值类型任意的 Map（键通常为 String / ResourceLocation）；</li>
 *   <li>{@link SimpleMapCodec} —— 键值类型固定的 Map；</li>
 *   <li>{@link DispatchedMapCodec}（{@link CodecUnwrapper#isDispatchedMap}）——
 *       按键分派的多态 Map（键确定、值随键变化，即 {@link SchemaType#UNION}）。</li>
 * </ul>
 *
 * <p>产出 {@link SchemaType#MAP} 节点：键 Schema / 值 Schema 分别走解析器责任链；
 * DispatchedMapCodec 值形态随键变化，不解析单一值 Schema。</p>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:24
 **/
public final class MapCodecHandler implements CodecSchemaHandler {

    /** 命中条件：三种 Map codec 之一。 */
    @Override
    public boolean supports(Object codec) {
        return codec instanceof UnboundedMapCodec<?, ?>
            || codec instanceof SimpleMapCodec<?, ?>
            || CodecUnwrapper.isDispatchedMap(codec);
    }

    /** 解析映射：键 Schema +（非分派时）值 Schema。 */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        DataSchema schema = new DataSchema(SchemaType.MAP);

        schema.javaType(Map.class);

        DataSchema key = context.resolve(CodecUnwrapper.keyCodec(codec));

        if (key != null) {
            schema.key(key);
        }

        // DispatchedMapCodec 的值结构随键分派，无法确定单一元素 Schema。
        if (!CodecUnwrapper.isDispatchedMap(codec)) {

            DataSchema element = context.resolve(CodecUnwrapper.elementCodec(codec));

            if (element != null) {
                schema.element(element);
            }
        }

        return schema;
    }
}
