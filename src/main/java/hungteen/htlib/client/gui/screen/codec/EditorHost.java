package hungteen.htlib.client.gui.screen.codec;

import hungteen.htlib.client.gui.widget.codec.TypeSelector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

/**
 * 表单控件的宿主接口，由 Codec 界面实现。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public interface EditorHost {

    Screen screen();

    Font font();

    int screenWidth();

    /**
     * 表单区顶部工具条高度，行 y 小于该值时不显示。
     */
    int topOffset();

    void addWidget(AbstractWidget widget);

    void removeWidget(AbstractWidget widget);

    void registerSelector(TypeSelector selector);

    void unregisterSelector(TypeSelector selector);

    /**
     * 结构变化后请求重新布局（只重摆控件，不重建）。
     */
    void relayout();
}
