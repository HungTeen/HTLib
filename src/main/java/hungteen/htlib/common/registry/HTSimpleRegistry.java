package hungteen.htlib.common.registry;

import hungteen.htlib.HTLib;
import hungteen.htlib.util.interfaces.ISimpleRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/25 9:21
 *
 * 主要是用于先于常规注册的一些东西，先注册这些可以更方便的一个循环来注册常规注册。<br>
 * 建议在mod构造函数中注册。
 */
public final class HTSimpleRegistry<T extends ISimpleRegistry> {

    private final HashMap<ResourceLocation, T> registryMap = new HashMap<>();
    private final ResourceLocation registryName;
    private final boolean hasOrder;

    public HTSimpleRegistry(ResourceLocation registryName){
        this(registryName, false);
    }

    public HTSimpleRegistry(ResourceLocation registryName, boolean hasOrder){
        this.registryName = registryName;
        this.hasOrder = hasOrder;
    }

    public void register(T type) {
        if(registryMap.containsKey(type.getLocation())){
            HTLib.getLogger().warn("HTSimpleRegistry {} already registered {}", this.getRegistryName(), type.getLocation());
        }
        registryMap.put(type.getLocation(), type);
    }

    public List<T> getAll() {
        if(hasOrder){
            return registryMap.values().stream().sorted(Comparator.comparingInt(ISimpleRegistry::getSortPriority)).toList();
        }
        return registryMap.values().stream().toList();
    }

    public Optional<T> getByName(String type) {
        return getByLocation(new ResourceLocation(type));
    }

    public Optional<T> getByLocation(ResourceLocation type) {
        return Optional.ofNullable(registryMap.getOrDefault(type, null));
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
