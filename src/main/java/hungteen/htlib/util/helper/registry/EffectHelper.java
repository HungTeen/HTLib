package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

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
    public Either<IForgeRegistry<MobEffect>, Registry<MobEffect>> getRegistry() {
        return Either.left(ForgeRegistries.MOB_EFFECTS);
    }

}
