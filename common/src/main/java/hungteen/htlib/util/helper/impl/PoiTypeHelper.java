package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:21
 */
public interface PoiTypeHelper extends HTVanillaRegistryHelper<PoiType> {

    HTVanillaRegistryHelper<PoiType> HELPER = () -> BuiltInRegistries.POINT_OF_INTEREST_TYPE;

    /* Common Methods */

    static HTVanillaRegistryHelper<PoiType> get() {
        return HELPER;
    }

}
