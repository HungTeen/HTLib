package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

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
    public Registry<SoundEvent> getRegistry() {
        return Registry.SOUND_EVENT;
    }
}
