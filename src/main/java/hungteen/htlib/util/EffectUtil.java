package hungteen.htlib.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:08
 **/
public class EffectUtil {

    public static MobEffectInstance effect(MobEffect effect, int time, int lvl){
        return new MobEffectInstance(effect, time, lvl, false, false);
    }

    public static MobEffectInstance viewEffect(MobEffect effect, int time, int lvl){
        return new MobEffectInstance(effect, time, lvl, true, true);
    }

}
