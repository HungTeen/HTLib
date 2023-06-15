package hungteen.htlib.common;

import hungteen.htlib.HTLib;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 09:19
 **/
public class HTSounds {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HTLib.MOD_ID);

    public static final RegistryObject<SoundEvent> PREPARE = register("prepare");
    public static final RegistryObject<SoundEvent> HUGE_WAVE = register("huge_wave");
    public static final RegistryObject<SoundEvent> FINAL_WAVE = register("final_wave");
    public static final RegistryObject<SoundEvent> VICTORY = register("victory");
    public static final RegistryObject<SoundEvent> LOSS = register("loss");
    public static final RegistryObject<SoundEvent> REWARD = register("reward");
    public static final RegistryObject<SoundEvent> FINAL_VICTORY = register("final_victory");

    public static void register(IEventBus event){
        SOUNDS.register(event);
    }

    private static RegistryObject<SoundEvent> register(String name){
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(HTLibHelper.prefix(name)));
    }

    private static RegistryObject<SoundEvent> register(String name, float range){
        return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(HTLibHelper.prefix(name), range));
    }
}
