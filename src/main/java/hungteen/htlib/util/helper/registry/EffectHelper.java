package hungteen.htlib.util.helper.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:08
 **/
public class EffectHelper extends RegistryHelper<MobEffect> {

    private static final EffectHelper HELPER = new EffectHelper();

    /**
     * Create an invisible effect instance.
     */
    public static MobEffectInstance effect(MobEffect effect, int time, int level){
        return effect(effect, time, level, false, false);
    }

    /**
     * Create a viewable effect instance.
     */
    public static MobEffectInstance viewEffect(MobEffect effect, int time, int level){
        return effect(effect, time, level, false, true);
    }

    /**
     * Create an effect instance.
     */
    public static MobEffectInstance effect(MobEffect effect, int time, int level, boolean ambient, boolean display){
        return new MobEffectInstance(effect, time, level, ambient, display);
    }

    /* Common Methods */

    public static EffectHelper get(){
        return HELPER;
    }

    @Override
    public Registry<MobEffect> getRegistry() {
        return Registry.MOB_EFFECT;
    }

}
