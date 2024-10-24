package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:24
 */
public interface BiomeHelper extends HTResourceHelper<Biome> {

    HTResourceHelper<Biome> HELPER = () -> Registries.BIOME;

    /* Common Methods */

    static HTResourceHelper<Biome> get() {
        return HELPER;
    }


}
