package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BannerPattern;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 19:51
 */
public class BannerPatternHelper extends RegistryHelper<BannerPattern> {

    private static final BannerPatternHelper HELPER = new BannerPatternHelper();

    /* Common Methods */

    public static BannerPatternHelper get() {
        return HELPER;
    }

    @Override
    public Registry<BannerPattern> getRegistry() {
        return Registry.BANNER_PATTERN;
    }
}
