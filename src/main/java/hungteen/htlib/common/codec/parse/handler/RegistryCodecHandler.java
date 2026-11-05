package hungteen.htlib.common.codec.parse.handler;

import hungteen.htlib.common.codec.parse.CodecUnwrapper;
import hungteen.htlib.common.codec.parse.ParseContext;
import hungteen.htlib.common.codec.parse.SchemaType;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.common.codec.parse.schema.RegistrySchema;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

/**
 * 注册表引用形态的 handler：解析 HOLDER / HOLDER_SET / REGISTRY 三类 Schema。
 *
 * <p>直接命中四种 public 形态：</p>
 * <ul>
 *   <li>{@link HolderSetCodec} → {@link SchemaType#HOLDER_SET}（条目集合，可含标签）；</li>
 *   <li>{@link RegistryFileCodec} → {@link SchemaType#HOLDER}（单个条目引用）；</li>
 *   <li>{@link RegistryFixedCodec} → {@link SchemaType#REGISTRY}（整个注册表）；</li>
 *   <li>{@link Holder}（含 Direct / Reference / Reference.Type）→ 按类型再分派。</li>
 * </ul>
 *
 * <p>未直接命中的包装链（如 holderByNameCodec / byNameCodec）：Registry 实例被捕获在 lambda
 * 字段里，无法用 instanceof 识别，于是沿透明包装扫描 {@link CodecUnwrapper#registryKey}。
 * 遇到基础结构即停止，不能把内部字段的注册表引用当作整个 record/list/map 的形态。</p>
 *
 * <p>注册表键与是否允许标签被封装进 {@link RegistrySchema}，挂在产出 Schema 上。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:24
 **/
@SuppressWarnings("unchecked")
public final class RegistryCodecHandler implements CodecSchemaHandler {

    /**
     * 命中条件：四种可 instanceof 的 public 形态，或对象图扫描能定位到注册表键、
     * 且不是基础结构 codec。
     */
    @Override
    public boolean supports(Object codec) {

        if (codec instanceof HolderSetCodec<?>
            || codec instanceof RegistryFileCodec<?>
            || codec instanceof RegistryFixedCodec<?>
            || codec instanceof Holder<?>) {
            return true;
        }

        /*
         * holderByNameCodec / byNameCodec 等包装链不是 RegistryFileCodec 形态，
         * Registry 实例被捕获在 lambda 字段里。通过受限包装扫描识别；
         * 但基础结构 codec（record/list/map/primitive）交给各自的 handler，避免误判。
         */
        if (CodecUnwrapper.isBaseCodec(codec)) {
            return false;
        }

        return CodecUnwrapper.registryKey(codec) != null;
    }

    /**
     * 产出注册表引用 Schema：附带注册表键；是 HOLDER_SET 时允许标签引用。
     */
    @Override
    public DataSchema parse(Object codec, ParseContext context) {

        CodecUnwrapper.RegistryReference reference = CodecUnwrapper.registryReference(codec);
        SchemaType type = reference == null ? SchemaType.HOLDER : reference.type();

        DataSchema schema = new DataSchema(type);

        ResourceKey<?> key = reference == null ? null : reference.key();

        if (key != null) {

            schema.registry(new RegistrySchema((ResourceKey<? extends Registry<?>>)key, type == SchemaType.HOLDER_SET));
        }

        return schema;
    }

}
