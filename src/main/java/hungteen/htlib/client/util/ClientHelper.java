package hungteen.htlib.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import hungteen.htlib.client.ClientProxy;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/3 16:18
 */
public class ClientHelper {

    public static boolean isMouseInput(KeyMapping key) {
        return key.getKey().getType() == InputConstants.Type.MOUSE;
    }

    public static boolean isKeyInput(KeyMapping key) {
        return key.getKey().getType() == InputConstants.Type.KEYSYM;
    }

    /**
     * Why not add them together?
     */
    public static void addWoodType(WoodType woodType){
        Sheets.addWoodType(woodType);
        Sheets.HANGING_SIGN_MATERIALS.put(woodType, Sheets.createHangingSignMaterial(woodType));
    }

    public static Minecraft mc(){
        return ClientProxy.MC;
    }

    public static Font font(){
        return mc().font;
    }

}
