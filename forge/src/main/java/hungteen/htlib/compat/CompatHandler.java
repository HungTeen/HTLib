package hungteen.htlib.compat;

import net.minecraftforge.fml.ModList;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 22:32
 **/
public class CompatHandler {

    public static final String KUBEJS = "kubejs";

    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static boolean isKubeJSLoaded() {
        return isModLoaded(KUBEJS);
    }
}
