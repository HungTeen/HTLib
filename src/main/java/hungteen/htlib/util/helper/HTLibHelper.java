package hungteen.htlib.util.helper;

import hungteen.htlib.HTLib;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 14:56
 */
public class HTLibHelper {

    private static final IModIDHelper HELPER = () -> HTLib.MOD_ID;

    public static IModIDHelper get(){
        return HELPER;
    }

    public static ResourceLocation prefix(String path){
        return get().prefix(path);
    }

}
