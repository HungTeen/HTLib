package hungteen.htlib.util.helper;

import hungteen.htlib.HTLib;
import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-09 13:12
 **/
public abstract class RegistryHelper<T> {

    /**
     * 每个分组的注册项。
     */
    private final HashMap<ResourceLocation, GroupRegistration<T>> groups = new HashMap<>();

    public GroupRegistration<T> group(ResourceLocation location){
        return groups.putIfAbsent(location, new GroupRegistration<>());
    }

    /**
     * {@link HTLib#postRegister(RegisterEvent)}
     */
    public void register(RegisterEvent event){
        groups.forEach((key, value) -> {
            value.getRegistrations().forEach(entry -> register(event, entry.getKey(), entry.getValue()));
            value.dump();
        });
    }

    /**
     * Get all objects from the group.
     */
    public List<T> getGroupEntries(ResourceLocation group){
        List<T> list = Arrays.asList();
        getRegistration(group).ifPresent(l -> {
            l.getEntries().forEach(res -> {
                getObject(res).ifPresent(list::add);
            });
        });
        return list;
    }

    public TagKey<T> tag(String name) {
        return TagKey.create(getForgeRegistry().getRegistryKey(), new ResourceLocation(name));
    }

    public TagKey<T> tag(ResourceLocation location) {
        return TagKey.create(getForgeRegistry().getRegistryKey(), location);
    }

    public TagKey<T> htTag(String name){
        return tag(HTLib.prefix(name));
    }

    public TagKey<T> forgeTag(String name){
        return tag(HTLib.forgePrefix(name));
    }

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
     * Get registered objects by key.
     */
    public Optional<T> getObject(ResourceLocation location) {
        return Optional.ofNullable(getForgeRegistry().getValue(location));
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

    private Optional<GroupRegistration<T>> getRegistration(ResourceLocation group){
        return Optional.ofNullable(groups.getOrDefault(group, null));
    }

    /**
     * 灵感来自XStudio/PVZReborn，实现方式不同。
     */
    public static class GroupRegistration<T> {

        private final HashMap<ResourceLocation, Supplier<T>> registrations = new HashMap<>();
        private final List<ResourceLocation> locations = new ArrayList<>();

        public void add(ResourceLocation location, Supplier<T> supplier) {
            if(registrations.containsKey(location)){
                HTLib.getLogger().warn("Duplicate registration for " + location + " !");
            } else {
                registrations.put(location, supplier);
            }
        }

        /**
         * Register finished, so only need to keep the group relationship.
         */
        public void dump(){
            locations.addAll(registrations.keySet());
            registrations.clear();
        }

        public List<ResourceLocation> getEntries() {
            return locations;
        }

        public Collection<Map.Entry<ResourceLocation, Supplier<T>>> getRegistrations() {
            return Collections.unmodifiableCollection(registrations.entrySet());
        }

    }

}
