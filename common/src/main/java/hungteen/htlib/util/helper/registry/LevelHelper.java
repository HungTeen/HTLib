package hungteen.htlib.util.helper.registry;

import hungteen.htlib.api.interfaces.HTResourceHelper;
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

    private static final HTResourceHelper<DimensionType> DIMENSION_TYPE_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<DimensionType>> resourceKey() {
            return Registries.DIMENSION_TYPE;
        }
    };

    private static final HTResourceHelper<LevelStem> LEVEL_STEM_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<LevelStem>> resourceKey() {
            return Registries.LEVEL_STEM;
        }
    };

    private static final HTResourceHelper<Level> LEVEL_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<Level>> resourceKey() {
            return Registries.DIMENSION;
        }
    };

    private static final HTResourceHelper<NoiseGeneratorSettings> NOISE_GEN_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<NoiseGeneratorSettings>> resourceKey() {
            return Registries.NOISE_SETTINGS;
        }
    };

    /* Common Methods */

    public static HTResourceHelper<Level> get(){
        return LEVEL_HELPER;
    }

    public static HTResourceHelper<DimensionType> type(){
        return DIMENSION_TYPE_HELPER;
    }

    public static HTResourceHelper<LevelStem> stem(){
        return LEVEL_STEM_HELPER;
    }

    public static HTResourceHelper<NoiseGeneratorSettings> noise(){
        return NOISE_GEN_HELPER;
    }
}
