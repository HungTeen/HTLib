package hungteen.htlib.api.registry;

import hungteen.htlib.api.HTLibAPI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 最先开始的注册项，不与原版的注册系统打交道，可以看做类似于动态扩展的枚举类。
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:32
 */
public interface HTSimpleRegistry<T extends SimpleEntry> extends HTCommonRegistry<T> {

    /**
     * Do nothing, just make the specific class being loaded.
     */
    default void initialize(){
        HTLibAPI.logger().debug("Initialize simple registry: {}", getRegistryName());
    }

    /**
     * Single initialize. <br>
     * Note: invoke before initialize event, 建议在注册事件发生前注册。
     * @param type The entry to be registered.
     */
    <I extends T> I register(@NotNull I type);

    /**
     * Multiple initialize. <br>
     * Note: invoke before initialize event, 建议在注册事件发生前注册。
     * @param types registry list.
     */
    default <I extends T> void register(List<I> types){
        types.forEach(this::register);
    }

}
