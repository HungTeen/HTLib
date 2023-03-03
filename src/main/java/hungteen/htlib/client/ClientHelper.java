package hungteen.htlib.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

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

}
