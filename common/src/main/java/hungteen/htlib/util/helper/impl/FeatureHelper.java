package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/30 9:03
 */
public interface FeatureHelper {

     HTVanillaRegistryHelper<Feature<?>> HELPER = () -> BuiltInRegistries.FEATURE;

     HTResourceHelper<ConfiguredFeature<?, ?>> CONFIGURED_HELPER = () -> Registries.CONFIGURED_FEATURE;

     HTResourceHelper<PlacedFeature> PLACED_HELPER = () -> Registries.PLACED_FEATURE;

    static HTVanillaRegistryHelper<Feature<?>> get(){
        return HELPER;
    }

    static HTResourceHelper<ConfiguredFeature<?, ?>> config(){
        return CONFIGURED_HELPER;
    }

    static HTResourceHelper<PlacedFeature> placed(){
        return PLACED_HELPER;
    }

}
