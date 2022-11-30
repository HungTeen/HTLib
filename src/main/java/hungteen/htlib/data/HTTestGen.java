package hungteen.htlib.data;

import com.mojang.serialization.Encoder;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
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
//        register(cache, HTRaidComponents.RAIDS, HTRaidComponents.getCodec());
    }

    protected <E, T extends HTRegistryHolder<E>> void register(CachedOutput cache, HTCodecRegistry<E> registry, Encoder<E> encoder) {
        registry.getAllWithLocation().forEach(entry -> {
            register(createPath(path, registry.getRegistryName(), entry.getKey()), cache, encoder, entry.getValue());
        });
    }

    @Override
    public String getName() {
        return this.modId + " test gen";
    }
}
