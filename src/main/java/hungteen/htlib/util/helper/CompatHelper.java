package hungteen.htlib.util.helper;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.fml.ModList;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-11 20:10
 **/
public class CompatHelper {

    public static boolean isModLoaded(String modId){
        return ModList.get().isLoaded(modId);
    }

    public static FeatureFlagSet allFeatures(){
        return FeatureFlags.REGISTRY.allFlags();
    }
}
