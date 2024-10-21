package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

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
    public Either<IForgeRegistry<Fluid>, Registry<Fluid>> getRegistry() {
        return Either.left(ForgeRegistries.FLUIDS);
    }
}
