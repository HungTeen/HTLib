package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.Platform;
import hungteen.htlib.api.util.helper.HTModIDHelper;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 15:04
 */
public interface VanillaHelper {

     HTModIDHelper HELPER = Platform.MINECRAFT::getNamespace;

    static HTModIDHelper get(){
        return HELPER;
    }

    static FeatureFlagSet allFeatures(){
        return FeatureFlags.REGISTRY.allFlags();
    }
}
