package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:08
 **/
public interface EffectHelper extends HTVanillaRegistryHelper<MobEffect> {

    HTVanillaRegistryHelper<MobEffect> HELPER = () -> BuiltInRegistries.MOB_EFFECT;

    /**
     * Create an invisible effect instance.
     */
    static MobEffectInstance effect(Holder<MobEffect> effect, int time, int level){
        return effect(effect, time, level, false, false);
    }

    /**
     * Create a viewable effect instance.
     */
    static MobEffectInstance viewEffect(Holder<MobEffect> effect, int time, int level){
        return effect(effect, time, level, false, true);
    }

    /**
     * Create an effect instance.
     */
    static MobEffectInstance effect(Holder<MobEffect> effect, int time, int level, boolean ambient, boolean display){
        return new MobEffectInstance(effect, time, level, ambient, display);
    }

    /* Common Methods */

    static HTVanillaRegistryHelper<MobEffect> get(){
        return HELPER;
    }


}
