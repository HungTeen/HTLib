package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-09 13:12
 **/
public abstract class RegistryHelper<T> {

    public abstract IForgeRegistry<T> getForgeRegistry();

    /**
     * Event Register.
     */
    public void register(RegisterEvent event, ResourceLocation location, Supplier<T> supplier){
        event.register(getForgeRegistry().getRegistryKey(), location, supplier);
    }

    /**
     * Get predicate registry objects.
     */
    public List<T> getFilterObjects(Predicate<T> predicate) {
        return getForgeRegistry().getValues().stream()
                .filter(predicate)
                .sorted(Comparator.comparing((object) -> Objects.requireNonNullElseGet(getForgeRegistry().getKey(object), () -> StringHelper.EMPTY_LOCATION)))
                .collect(Collectors.toList());
    }

    /**
     * Get all registered objects with keys.
     */
    public Collection<Pair<ResourceKey<T>, T>> getObjectWithKeys() {
        return getForgeRegistry().getEntries().stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Get key of specific object.
     */
    public ResourceLocation getResourceLocation(T object) {
        return getForgeRegistry().getKey(object);
    }

}
