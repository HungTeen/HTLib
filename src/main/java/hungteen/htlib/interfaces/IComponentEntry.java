package hungteen.htlib.interfaces;

import net.minecraft.network.chat.Component;

/**
 * 很多模组自定义的注册项是需要保存或显示的，故需要名字来区分显示。
 */
public interface IComponentEntry {

    /**
     * 获取注册名。
     */
    String getName();

    /**
     * 获取文本显示。
     */
    Component getComponent();
}
