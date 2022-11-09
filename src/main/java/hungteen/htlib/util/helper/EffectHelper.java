package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

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

    /**
     * Get predicate registry objects.
     */
    public static List<MobEffect> getFilterEffects(Predicate<MobEffect> predicate) {
        return HELPER.getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<MobEffect>, MobEffect>> getEffectWithKeys() {
        return HELPER.getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(MobEffect object) {
        return HELPER.getResourceLocation(object);
    }

    @Override
    public IForgeRegistry<MobEffect> getForgeRegistry() {
        return ForgeRegistries.MOB_EFFECTS;
    }

}
