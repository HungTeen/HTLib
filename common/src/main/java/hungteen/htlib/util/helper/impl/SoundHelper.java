package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-09 14:08
 **/
public interface SoundHelper extends HTVanillaRegistryHelper<SoundEvent> {

    HTVanillaRegistryHelper<SoundEvent> HELPER = () -> BuiltInRegistries.SOUND_EVENT;

    /* Common Methods */

    static HTVanillaRegistryHelper<SoundEvent> get(){
        return HELPER;
    }

}
