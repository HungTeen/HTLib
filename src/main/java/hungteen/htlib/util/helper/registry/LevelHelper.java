package hungteen.htlib.util.helper.registry;

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

    private static final ResourceHelper<DimensionType> DIMENSION_TYPE_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<DimensionType>> resourceKey() {
            return Registries.DIMENSION_TYPE;
        }
    };

    private static final ResourceHelper<LevelStem> LEVEL_STEM_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<LevelStem>> resourceKey() {
            return Registries.LEVEL_STEM;
        }
    };

    private static final ResourceHelper<Level> LEVEL_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<Level>> resourceKey() {
            return Registries.DIMENSION;
        }
    };

    private static final ResourceHelper<NoiseGeneratorSettings> NOISE_GEN_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<NoiseGeneratorSettings>> resourceKey() {
            return Registries.NOISE_SETTINGS;
        }
    };

    /* Common Methods */

    public static ResourceHelper<Level> get(){
        return LEVEL_HELPER;
    }

    public static ResourceHelper<DimensionType> type(){
        return DIMENSION_TYPE_HELPER;
    }

    public static ResourceHelper<LevelStem> stem(){
        return LEVEL_STEM_HELPER;
    }

    public static ResourceHelper<NoiseGeneratorSettings> noise(){
        return NOISE_GEN_HELPER;
    }
}
