package hungteen.htlib.common.codec.parse.handler;

import hungteen.htlib.common.codec.parse.CodecUnwrapper;
import hungteen.htlib.common.codec.parse.DispatchCodecInspector;
import hungteen.htlib.common.codec.parse.ParseContext;
import hungteen.htlib.common.codec.parse.SchemaType;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.util.ReflectionUtil;

import java.util.Map;

/**
 * 多态（dispatch）形态的 handler：解析 {@code KeyDispatchCodec}（DFU 6.0.8）。
 *
 * <p>识别：{@link CodecUnwrapper#isDispatchedMap}（{@code com.mojang.serialization.codecs.KeyDispatchCodec}），
 * 即 {@code Codec.dispatch(...)} / {@code dispatchMap(...)} 的产物。</p>
 *
 * <p>解析思路（类型值枚举与反查见 {@link DispatchCodecInspector}）：</p>
 * <ol>
 *   <li>产出 {@link SchemaType#UNION} 节点，{@code variantKey} 记为 typeKey；</li>
 *   <li>解析 keyCodec 得到"类型值"的 Schema（枚举或注册表引用）作为 key；</li>
 *   <li><b>按 type 拆分</b>：用 {@link DispatchCodecInspector#typeToCodec} 枚举所有类型值 → 分支 codec，
 *       逐个解析为变体，变体名记为类型值名字。</li>
 * </ol>
 *
 * <p>枚举不到类型值（keyCodec 既非枚举也无可定位注册表）时，退化为"只有分派键、无变体"的 UNION。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 12:20
 **/
public final class DispatchCodecHandler implements CodecSchemaHandler {

    /** 命中条件：KeyDispatchCodec。 */
    @Override
    public boolean supports(Object codec) {
        return CodecUnwrapper.isDispatchedMap(codec);
    }

    /** 解析 dispatch codec：UNION + 按 type 拆分变体。 */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        DataSchema schema = new DataSchema(SchemaType.UNION);

        schema.variantKey(DispatchCodecInspector.typeKey(codec));

        // 类型值的 codec（枚举 / 注册表引用）→ key Schema。
        Object keyCodec = ReflectionUtil.getField(codec, "keyCodec");

        if (keyCodec != null) {

            DataSchema key = context.resolve(keyCodec);

            if (key != null) {
                schema.key(key);
            }
        }

        // 按 type 拆分：枚举类型值 → 分支 codec → 解析为变体。
        for (Map.Entry<String, Object> entry : DispatchCodecInspector.typeToCodec(codec).entrySet()) {

            DataSchema variant = context.resolve(entry.getValue());

            if (variant == null) {
                continue;
            }

            DataSchema copy = variant.copy();

            copy.variantName(entry.getKey());

            schema.variants().add(copy);
        }

        return schema;
    }
}
