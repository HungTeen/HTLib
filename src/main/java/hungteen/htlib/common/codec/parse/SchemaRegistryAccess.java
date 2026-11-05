package hungteen.htlib.common.codec.parse;

import net.minecraft.core.RegistryAccess;

import java.util.function.Supplier;

/**
 * schema 生成期间的服务端 {@link RegistryAccess} 持有器。
 *
 * <p>vanilla 数据驱动注册表（如 {@code worldgen/configured_feature}）的 dispatch
 * keyCodec 只持有 {@code ResourceKey}，需要 RegistryAccess 才能解析出实际注册表并枚举所有 type。 由 schema 请求入口（服务端
 * {@code RequestEntrySchemaPacket}）与命令入口在生成期间设置，结束后恢复。</p>
 */
public final class SchemaRegistryAccess {

    private static final ThreadLocal<RegistryAccess> ACCESS = new ThreadLocal<>();

    private SchemaRegistryAccess() {
    }

    /**
     * 设置当前服务端的 RegistryAccess（每次 schema 请求前刷新）。
     */
    public static void set(RegistryAccess registryAccess) {
        if (registryAccess == null) {
            ACCESS.remove();
        } else {
            ACCESS.set(registryAccess);
        }
    }

    /**
     * 当前 RegistryAccess；未设置时为 null。
     */
    public static RegistryAccess get() {
        return ACCESS.get();
    }

    /** Scope registry lookup to one parse, restoring any outer context even when parsing fails. */
    public static <T> T withAccess(RegistryAccess registryAccess, Supplier<T> action) {
        RegistryAccess previous = get();
        set(registryAccess);
        try {
            return action.get();
        } finally {
            set(previous);
        }
    }
}
