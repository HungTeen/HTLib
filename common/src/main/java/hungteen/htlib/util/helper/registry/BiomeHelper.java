package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

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
    public Either<IForgeRegistry<Biome>, Registry<Biome>> getRegistry() {
        return Either.left(ForgeRegistries.BIOMES);
    }
}
