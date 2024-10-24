package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
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
public interface LevelHelper {

     HTResourceHelper<DimensionType> DIMENSION_TYPE_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<DimensionType>> resourceKey() {
            return Registries.DIMENSION_TYPE;
        }
    };

     HTResourceHelper<LevelStem> LEVEL_STEM_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<LevelStem>> resourceKey() {
            return Registries.LEVEL_STEM;
        }
    };

     HTResourceHelper<Level> LEVEL_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<Level>> resourceKey() {
            return Registries.DIMENSION;
        }
    };

     HTResourceHelper<NoiseGeneratorSettings> NOISE_GEN_HELPER = new HTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<NoiseGeneratorSettings>> resourceKey() {
            return Registries.NOISE_SETTINGS;
        }
    };

    /* Common Methods */

    static HTResourceHelper<Level> get(){
        return LEVEL_HELPER;
    }

    static HTResourceHelper<DimensionType> type(){
        return DIMENSION_TYPE_HELPER;
    }

    static HTResourceHelper<LevelStem> stem(){
        return LEVEL_STEM_HELPER;
    }

    static HTResourceHelper<NoiseGeneratorSettings> noise(){
        return NOISE_GEN_HELPER;
    }
}
