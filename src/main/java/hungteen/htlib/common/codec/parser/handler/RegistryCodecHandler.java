package hungteen.htlib.common.codec.parser.handler;

import hungteen.htlib.common.codec.parser.*;
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
 * 字段里，无法用 instanceof 识别，于是通过对象图扫描 {@link CodecUnwrapper#registryKey} 判定——
 * 扫到 ResourceKey 即视为注册表引用；但基础结构 codec（record/list/map/primitive）先交还责任链
 * 的其他 handler，避免误判（见 {@link #supports} 的顺序逻辑）。</p>
 *
 * <p>注册表键与是否允许标签被封装进 {@link RegistrySchema}，挂在产出 Schema 上。</p>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
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
         * Registry 实例被捕获在 lambda 字段里。通过对象图扫描识别；
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

        SchemaType type = typeOf(codec);

        DataSchema schema = new DataSchema(type);

        ResourceKey<?> key = CodecUnwrapper.registryKey(codec);

        if (key != null) {

            schema.registry(new RegistrySchema((ResourceKey<? extends Registry<?>>)key, type == SchemaType.HOLDER_SET));
        }

        return schema;
    }

    /**
     * 按 codec 形态映射 SchemaType；非直接命中的兜底为 HOLDER。
     */
    private static SchemaType typeOf(Object codec) {

        if (codec instanceof HolderSetCodec<?>) {
            return SchemaType.HOLDER_SET;
        }

        if (codec instanceof RegistryFileCodec<?>) {
            return SchemaType.HOLDER;
        }

        if (codec instanceof RegistryFixedCodec<?>) {
            return SchemaType.REGISTRY;
        }

        return SchemaType.HOLDER;
    }
}
