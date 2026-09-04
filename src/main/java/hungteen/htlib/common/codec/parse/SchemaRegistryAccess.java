package hungteen.htlib.common.codec.parse;

import net.minecraft.core.RegistryAccess;

/**
 * schema 生成期间的服务端 {@link RegistryAccess} 持有器。
 *
 * <p>vanilla 数据驱动注册表（如 {@code worldgen/configured_feature}）的 dispatch
 * keyCodec 只持有 {@code ResourceKey}，需要 RegistryAccess 才能解析出实际注册表并枚举所有 type。 由 schema 请求入口（服务端
 * {@code RequestSchemaPacket}）在生成前设置。</p>
 */
public final class SchemaRegistryAccess {

    private static RegistryAccess access;

    /**
     * 设置当前服务端的 RegistryAccess（每次 schema 请求前刷新）。
     */
    public static void set(RegistryAccess registryAccess) {
        access = registryAccess;
    }

    /**
     * 当前 RegistryAccess；未设置时为 null。
     */
    public static RegistryAccess get() {
        return access;
    }
}
