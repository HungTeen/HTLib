package hungteen.htlib.common.codec.parse.schema;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * 注册表引用信息：描述某个 codec 对应哪个注册表。
 *
 * <p>由 {@link hungteen.htlib.common.codec.parse.handler.RegistryCodecHandler} 解析 HOLDER / HOLDER_SET / REGISTRY 形态的 codec 时填充，
 * 供编辑器校验引用是否指向合法注册表条目。</p>
 *
 * <ul>
 *   <li>{@link #registry}：目标注册表的 ResourceKey（如 {@code Regries.ENCHANTMENT}）；</li>
 *   <li>{@link #allowTag}：是否允许标签（#tag）引用（HOLDER_SET 允许，单个 HOLDER 视 codec 而定）。</li>
 * </ul>
 *
 * @param registry
 *     目标注册表的键。
 * @param allowTag
 *     是否允许用标签（#name）形式引用条目。
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:20
 */
public record RegistrySchema(ResourceKey<? extends Registry<?>> registry, boolean allowTag) {

    /**
     * 目标注册表的键。
     */
    @Override
    public ResourceKey<? extends Registry<?>> registry() {
        return registry;
    }

    /**
     * 是否允许标签引用。
     */
    @Override
    public boolean allowTag() {
        return allowTag;
    }
}
