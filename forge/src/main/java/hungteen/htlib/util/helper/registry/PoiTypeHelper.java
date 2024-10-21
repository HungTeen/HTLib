package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

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
    public Either<IForgeRegistry<PoiType>, Registry<PoiType>> getRegistry() {
        return Either.left(ForgeRegistries.POI_TYPES);
    }
}
