package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-09 14:08
 **/
public class SoundHelper extends RegistryHelper<SoundEvent> {

    private static final SoundHelper HELPER = new SoundHelper();

    /**
     * Get predicate registry objects.
     */
    public static List<SoundEvent> getFilterSoundEvents(Predicate<SoundEvent> predicate) {
        return HELPER.getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<SoundEvent>, SoundEvent>> getSoundEventWithKeys() {
        return HELPER.getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(SoundEvent object) {
        return HELPER.getResourceLocation(object);
    }

    @Override
    public IForgeRegistry<SoundEvent> getForgeRegistry() {
        return ForgeRegistries.SOUND_EVENTS;
    }
}
