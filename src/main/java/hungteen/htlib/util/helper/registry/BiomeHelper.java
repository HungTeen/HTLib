package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.biome.Biome;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:24
 */
public class BiomeHelper extends RegistryHelper<Biome> {

    private static final BiomeHelper HELPER = new BiomeHelper();

    /* Common Methods */

    public static BiomeHelper get() {
        return HELPER;
    }

    @Override
    public Registry<Biome> getRegistry() {
        return BuiltinRegistries.BIOME;
    }
}
