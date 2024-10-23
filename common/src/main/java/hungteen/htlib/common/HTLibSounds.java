package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 09:19
 **/
public interface HTLibSounds {

    HTVanillaRegistry<SoundEvent> SOUNDS = HTRegistryManager.vanilla(Registries.SOUND_EVENT, HTLibAPI.id());

    Supplier<SoundEvent> PREPARE = register("prepare");
    Supplier<SoundEvent> HUGE_WAVE = register("huge_wave");
    Supplier<SoundEvent> FINAL_WAVE = register("final_wave");
    Supplier<SoundEvent> VICTORY = register("victory");
    Supplier<SoundEvent> LOSS = register("loss");
    Supplier<SoundEvent> REWARD = register("reward");
    Supplier<SoundEvent> FINAL_VICTORY = register("final_victory");

    private static Supplier<SoundEvent> register(String name){
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(HTLibHelper.prefix(name)));
    }

    private static Supplier<SoundEvent> register(String name, float range){
        return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(HTLibHelper.prefix(name), range));
    }

    static HTVanillaRegistry<SoundEvent> registry(){
        return SOUNDS;
    }
}
