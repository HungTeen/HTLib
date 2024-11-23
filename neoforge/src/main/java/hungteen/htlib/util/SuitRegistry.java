package hungteen.htlib.util;

import hungteen.htlib.api.registry.SimpleEntry;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2024/11/23 16:54
 **/
public interface SuitRegistry extends SimpleEntry {

    /**
     * 填充创造模式物品栏。
     */
    void fillTabs(BuildCreativeModeTabContentsEvent event);

    /**
     * @param event 注册事件。
     */
    void register(RegisterEvent event);

    /**
     * 注册结束后，清空辅助数据。
     */
    void clear();
}
