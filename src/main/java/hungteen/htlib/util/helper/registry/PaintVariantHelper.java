package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.decoration.PaintingVariant;

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
    public Registry<PaintingVariant> getRegistry() {
        return Registry.PAINTING_VARIANT;
    }
}
