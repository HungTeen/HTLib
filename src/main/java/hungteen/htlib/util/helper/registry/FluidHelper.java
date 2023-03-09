package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:26
 */
public class FluidHelper extends RegistryHelper<Fluid> {

    private static final FluidHelper HELPER = new FluidHelper();

    /* Common Methods */

    public static FluidHelper get() {
        return HELPER;
    }

    @Override
    public Registry<Fluid> getRegistry() {
        return Registry.FLUID;
    }
}
