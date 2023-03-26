package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:23
 */
public class PaintVariantHelper extends RegistryHelper<PaintingVariant> {

    private static final PaintVariantHelper HELPER = new PaintVariantHelper();

    /* Common Methods */

    public static PaintVariantHelper get() {
        return HELPER;
    }

    @Override
    public Either<IForgeRegistry<PaintingVariant>, Registry<PaintingVariant>> getRegistry() {
        return Either.left(ForgeRegistries.PAINTING_VARIANTS);
    }
}
