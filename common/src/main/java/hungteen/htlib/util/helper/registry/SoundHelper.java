package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-09 14:08
 **/
public class SoundHelper extends RegistryHelper<SoundEvent> {

    private static final SoundHelper HELPER = new SoundHelper();

    /* Common Methods */

    public static SoundHelper get(){
        return HELPER;
    }

    @Override
    public Either<IForgeRegistry<SoundEvent>, Registry<SoundEvent>> getRegistry() {
        return Either.left(ForgeRegistries.SOUND_EVENTS);
    }
}
