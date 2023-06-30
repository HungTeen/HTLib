package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/30 9:03
 */
public class FeatureHelper {

    private static final RegistryHelper<Feature<?>> HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<Feature<?>>, Registry<Feature<?>>> getRegistry() {
            return Either.left(ForgeRegistries.FEATURES);
        }
    };

    private static final IHTResourceHelper<ConfiguredFeature<?, ?>> CONFIGURED_HELPER = () -> Registries.CONFIGURED_FEATURE;

    private static final IHTResourceHelper<PlacedFeature> PLACED_HELPER = () -> Registries.PLACED_FEATURE;

    public static RegistryHelper<Feature<?>> get(){
        return HELPER;
    }

    public static IHTResourceHelper<ConfiguredFeature<?, ?>> config(){
        return CONFIGURED_HELPER;
    }

    public static IHTResourceHelper<PlacedFeature> placed(){
        return PLACED_HELPER;
    }

}
