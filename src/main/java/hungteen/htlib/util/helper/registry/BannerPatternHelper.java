package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.IForgeRegistry;

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
    public Either<IForgeRegistry<BannerPattern>, Registry<BannerPattern>> getRegistry() {
        return Either.right(BuiltInRegistries.BANNER_PATTERN);
    }
}
