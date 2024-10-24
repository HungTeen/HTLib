package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:26
 */
public interface FluidHelper extends HTVanillaRegistryHelper<Fluid> {

     FluidHelper HELPER = () -> BuiltInRegistries.FLUID;

    /* Common Methods */

     static HTVanillaRegistryHelper<Fluid> get() {
        return HELPER;
    }

}
