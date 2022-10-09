package hungteen.htlib.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 10:29
 **/
public class HTScreen extends Screen {

    protected int leftPos;
    protected int topPos;
    protected int imageWidth = 176;
    protected int imageHeight = 166;

    public HTScreen() {
        super(new TextComponent(""));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }
}
