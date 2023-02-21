package hungteen.htlib.data;

import com.mojang.serialization.Encoder;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.impl.placement.HTPlaceComponents;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-30 10:24
 **/
public class HTTestGen extends HTCodecGen{

    public HTTestGen(DataGenerator generator) {
        super(generator, HTLib.MOD_ID);
    }

    @Override
    public void run(CachedOutput cache) {
        HTPlaceComponents.registerStuffs();
        HTSpawnComponents.registerStuffs();
        HTWaveComponents.registerStuffs();
        HTResultComponents.registerStuffs();
        HTRaidComponents.registerStuffs();
        register(cache, HTRaidComponents.RAIDS, HTRaidComponents.getCodec());
    }

    protected <E, T extends HTRegistryHolder<E>> void register(CachedOutput cache, HTCodecRegistry<E> registry, Encoder<E> encoder) {
        registry.getEntries().forEach(entry -> {
            register(createPath(path, registry.getRegistryName(), entry.getKey()), cache, encoder, entry.getValue());
        });
    }

    @Override
    public String getName() {
        return this.modId + " test gen";
    }
}
