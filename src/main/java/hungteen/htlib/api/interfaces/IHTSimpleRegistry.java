package hungteen.htlib.api.interfaces;

import java.util.List;

/**
 * "先于原版注册"的简单注册表接口（{@link hungteen.htlib.common.registry.HTSimpleRegistry}
 * 的 API 定义）。 <br>
 *
 * <p>条目实现 {@link ISimpleEntry} 后自带注册名，可直接用对象本身注册
 * （{@link #register(ISimpleEntry)}），或一次注册整个列表
 * （{@link #register(List)}），非常适合批量注册轻量"类型/选项"对象。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 14:32
 */
public interface IHTSimpleRegistry<T extends ISimpleEntry> extends IHTCommonRegistry<T> {

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
