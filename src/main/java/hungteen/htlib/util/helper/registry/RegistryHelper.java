package hungteen.htlib.util.helper.registry;

import hungteen.htlib.HTLib;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.RegisterEvent;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 所有注册帮助类支持分组注册、便捷标签获取、简单注册流查询等方法。 <br>
 * All registry type support following methods : <br>
 * 1. Group registration methods. <br>
 * 2. Easy tag get methods. <br>
 * 3. Simple registration stream query methods. <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-09 13:12
 **/
public abstract class RegistryHelper<T> {

    private final Map<ResourceLocation, GroupRegistration<T>> groups = Collections.synchronizedMap(new HashMap<>()); // 每个分组的注册项。

    /**
     * 决定注册帮助类的类型。
     * @return ForgeRegistry.
     */
    public abstract Registry<T> getRegistry();

    /* 分组注册相关 */

    /**
     * 创建新的分组。
     * @param location group name.
     * @return group registration.
     */
    public GroupRegistration<T> group(ResourceLocation location){
        return this.groups.putIfAbsent(location, new GroupRegistration<>());
    }

    /**
     * Used in group register, 用于分组注册。
     * {@link HTLib#postRegister(RegisterEvent)}
     */
    public void register(RegisterEvent event){
        this.groups.forEach((key, value) -> {
            value.getRegistrations().forEach(entry -> register(event, entry.getKey(), entry.getValue()));
            value.dump();
        });
    }

    /**
     * Get all objects from the group.
     */
    public List<T> getGroupObjects(ResourceLocation group){
        List<T> list = Arrays.asList();
        getRegistration(group).ifPresent(l -> {
            l.getEntries().forEach(res -> {
                get(res).ifPresent(list::add);
            });
        });
        return list;
    }

    /**
     * 根据group获取分组注册类。
     */
    private Optional<GroupRegistration<T>> getRegistration(ResourceLocation group){
        return Optional.ofNullable(groups.getOrDefault(group, null));
    }

    /* 标签相关 */

    /**
     * 根据name创建tag。
     */
    public TagKey<T> tag(String name) {
        return tag(new ResourceLocation(name));
    }

    /**
     * 根据location创建tag。
     */
    public TagKey<T> tag(ResourceLocation location) {
        return TagKey.create(getRegistry().key(), location);
    }

    /**
     * 根据name创建ht的tag。
     */
    public TagKey<T> htTag(String name){
        return tag(StringHelper.prefix(name));
    }

    /**
     * 根据name创建forge的tag。
     */
    public TagKey<T> forgeTag(String name){
        return tag(StringHelper.forgePrefix(name));
    }

    /* Common Methods */

    /**
     * 注册类的注册名。
     */
    public ResourceKey<? extends Registry<T>> resourceKey(){
        return getRegistry().key();
    }

    /**
     * Used by {@link RegisterEvent}.
     */
    public void register(RegisterEvent event, ResourceLocation location, Supplier<T> supplier){
        event.register(getRegistry().key(), location, supplier);
    }

    /**
     * easy check.
     */
    public boolean matchEvent(RegisterEvent event){
        return resourceKey().equals(event.getRegistryKey());
    }

    /**
     * Get predicate registry objects.
     */
    public List<T> getFilterEntries(Predicate<T> predicate) {
        return getRegistry().stream()
                .filter(predicate)
                .sorted(Comparator.comparing((object) -> Objects.requireNonNullElseGet(getRegistry().getKey(object), () -> StringHelper.EMPTY_LOCATION)))
                .collect(Collectors.toList());
    }

    /**
     * Get registered objects by key.
     */
    public Optional<T> get(ResourceLocation location) {
        return getRegistry().getOptional(location);
    }

    /**
     * Get all registered objects with keys.
     */
    public Collection<Map.Entry<ResourceKey<T>, T>> getWithKeys() {
        return getRegistry().entrySet().stream().toList();
    }

    /**
     * Get key of specific object.
     */
    public ResourceLocation getKey(T object) {
        return getRegistry().getKey(object);
    }

    /**
     * 灵感来自XStudio/PVZReborn，实现方式不同。
     */
    public static class GroupRegistration<T> {

        private final HashMap<ResourceLocation, Supplier<T>> registrations = new HashMap<>();
        private final List<ResourceLocation> locations = new ArrayList<>();

        /**
         * 添加新的项到分组中。
         * @param location key.
         * @param supplier registration.
         * @return builder.
         */
        public GroupRegistration<T> add(ResourceLocation location, Supplier<T> supplier) {
            if(registrations.containsKey(location)){
                HTLib.getLogger().warn("Duplicate registration for " + location + " !");
            } else {
                registrations.put(location, supplier);
            }
            return this;
        }

        /**
         * 注册完成，清理冗余数据。 <br>
         * Register finished, so only need to keep the group relationship.
         */
        public void dump(){
            locations.addAll(registrations.keySet());
            registrations.clear();
        }

        /**
         * 获取分组类的所有注册名。
         * @return all registry key in group.
         */
        public List<ResourceLocation> getEntries() {
            return locations;
        }

        /**
         * 获取分组类的所有注册项。
         * @return all registrations in group.
         */
        public Collection<Map.Entry<ResourceLocation, Supplier<T>>> getRegistrations() {
            return Collections.unmodifiableCollection(registrations.entrySet());
        }

    }

}
