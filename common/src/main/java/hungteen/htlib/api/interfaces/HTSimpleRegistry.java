package hungteen.htlib.api.interfaces;

import hungteen.htlib.api.registry.SimpleEntry;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:32
 */
public interface HTSimpleRegistry<T extends SimpleEntry> extends HTCommonRegistry<T> {

    /**
     * Single register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param type The entry to be registered.
     */
    default <I extends T> I register(I type){
        return register(type.getLocation(), type);
    }

    /**
     * Multiple register. <br>
     * Note: invoke before register event, 建议在注册事件发生前注册。
     * @param types registry list.
     */
    default <I extends T> void register(List<I> types){
        types.forEach(this::register);
    }

}
