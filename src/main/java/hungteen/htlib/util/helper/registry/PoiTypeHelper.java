package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:21
 */
public class PoiTypeHelper extends RegistryHelper<PoiType> {

    private static final PoiTypeHelper HELPER = new PoiTypeHelper();

    /* Common Methods */

    public static PoiTypeHelper get() {
        return HELPER;
    }

    @Override
    public Registry<PoiType> getRegistry() {
        return Registry.POINT_OF_INTEREST_TYPE;
    }
}
