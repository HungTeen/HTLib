package hungteen.htlib.common.codec.parser.handler;

import com.mojang.serialization.codecs.SimpleMapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import hungteen.htlib.common.codec.parser.*;

import java.util.Map;

/**
 * 映射形态的 handler：解析两种 Map codec。
 *
 * <p>命中条件：</p>
 * <ul>
 *   <li>{@link UnboundedMapCodec} —— 键值类型任意的 Map（键通常为 String / ResourceLocation）；</li>
 *   <li>{@link SimpleMapCodec} —— 键值类型固定的 Map。</li>
 * </ul>
 *
 * <p>产出 {@link SchemaType#MAP} 节点：键 Schema / 值 Schema 分别走解析器责任链。
 * 多态（dispatch）Map 由 {@link DispatchCodecHandler} 处理，不在此列。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:24
 **/
public final class MapCodecHandler implements CodecSchemaHandler {

    /** 命中条件：两种 Map codec 之一。 */
    @Override
    public boolean supports(Object codec) {
        return codec instanceof UnboundedMapCodec<?, ?>
            || codec instanceof SimpleMapCodec<?, ?>;
    }

    /** 解析映射：键 Schema + 值 Schema。 */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        DataSchema schema = new DataSchema(SchemaType.MAP);

        schema.javaType(Map.class);

        DataSchema key = context.resolve(CodecUnwrapper.keyCodec(codec));

        if (key != null) {
            schema.key(key);
        }

        DataSchema element = context.resolve(CodecUnwrapper.elementCodec(codec));

        if (element != null) {
            schema.element(element);
        }

        return schema;
    }
}
