package hungteen.htlib.util.helper.registry;

import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:53
 */
public class LevelHelper {

    private static final IHTResourceHelper<DimensionType> DIMENSION_TYPE_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<DimensionType>> resourceKey() {
            return Registries.DIMENSION_TYPE;
        }
    };

    private static final IHTResourceHelper<LevelStem> LEVEL_STEM_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<LevelStem>> resourceKey() {
            return Registries.LEVEL_STEM;
        }
    };

    private static final IHTResourceHelper<Level> LEVEL_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<Level>> resourceKey() {
            return Registries.DIMENSION;
        }
    };

    private static final IHTResourceHelper<NoiseGeneratorSettings> NOISE_GEN_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<NoiseGeneratorSettings>> resourceKey() {
            return Registries.NOISE_SETTINGS;
        }
    };

    /* Common Methods */

    public static IHTResourceHelper<Level> get(){
        return LEVEL_HELPER;
    }

    public static IHTResourceHelper<DimensionType> type(){
        return DIMENSION_TYPE_HELPER;
    }

    public static IHTResourceHelper<LevelStem> stem(){
        return LEVEL_STEM_HELPER;
    }

    public static IHTResourceHelper<NoiseGeneratorSettings> noise(){
        return NOISE_GEN_HELPER;
    }
}
