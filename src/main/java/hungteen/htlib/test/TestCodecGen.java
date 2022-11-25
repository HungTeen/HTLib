package hungteen.htlib.test;

import hungteen.htlib.HTLib;
import hungteen.htlib.data.HTCodecGen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import java.util.stream.StreamSupport;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 10:54
 */
public class TestCodecGen extends HTCodecGen {

    public TestCodecGen(DataGenerator generator) {
        super(generator, HTLib.MOD_ID);
    }

    @Override
    public void run(CachedOutput cache) {
//        WritableRegistry<SpawnPlacement> registry = new MappedRegistry<>(HTPlacements.PLACEMENTS.getRegistryKey(), Lifecycle.experimental(), null);
//
//        register(cache, HTPlacements.PLACEMENTS.getRegistryKey(), registry, HTPlacements.getCodec());

        StreamSupport.stream(RegistryAccess.knownRegistries().spliterator(), false)
                .filter(r -> access().ownedRegistry(r.key()).isPresent())
                .forEach(data -> registerCap(cache, data));
    }

    @Override
    public String getName() {
        return this.modId + " test gen";
    }
}
