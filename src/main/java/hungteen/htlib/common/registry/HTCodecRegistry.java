package hungteen.htlib.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.network.DataPackPacket;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.util.helper.FileHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/24 16:25
 * <p>
 * It seems that forge can not support {entityType -> value} kind codec registry.
 */
public final class HTCodecRegistry<V> implements IHTCodecRegistry<V> {

    private static final Logger LOGGER = LogUtils.getLogger();
    /**
     * Register by code.
     */
    private final BiMap<ResourceLocation, V> innerMap = HashBiMap.create();
    /**
     * Register by data pack.
     */
    private final BiMap<ResourceLocation, V> outerMap = HashBiMap.create();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final String registryName;
    private final Class<V> registryClass;
    private final Supplier<Codec<V>> supplier;
    private final boolean isGlobal;
    private File rootDir;

    HTCodecRegistry(Class<V> registryClass, String registryName, Supplier<Codec<V>> supplier, boolean isGlobal) {
        this.registryClass = registryClass;
        this.registryName = registryName;
        this.supplier = supplier;
        this.isGlobal = isGlobal;
    }

    /**
     * 代码注册。
     */
    public HTRegistryHolder<V> innerRegister(ResourceLocation name, V value) {
        if (containKey(name)) {
            HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
        }
        this.innerMap.put(name, value);
        return new HTRegistryHolder<>(name, value);
    }

    /**
     * 外部数据包注册。
     */
    public void outerRegister(ResourceLocation name, Object value) {
        if (containKey(name)) {
            HTLib.getLogger().warn("HTCodecRegistry {} already registered {}", this.getRegistryName(), name);
        } else if (!this.getRegistryClass().isInstance(value)) {
            HTLib.getLogger().warn("HTCodecRegistry {} can not cast {} to correct entityType", this.getRegistryName(), name);
        }
        this.outerMap.put(name, this.getRegistryClass().cast(value));
    }

    public void init(){
        if(this.rootDir == null){
            this.rootDir = new File(this.registryName);
        }
        if(! this.rootDir.exists()){
            this.rootDir.mkdirs();
        } else if(! this.rootDir.isDirectory()){
            throw new RuntimeException(this.rootDir.getAbsolutePath() + " is a file, not a folder.");
        }
        // Clear first.
        this.clearOutRegistries();
        final List<File> files = FileHelper.getAllFiles(this.rootDir, f -> f.getName().endsWith(".json"));
        files.forEach(file -> {
            try (FileInputStream stream = new FileInputStream(file)) {
                Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                getCodec().parse(JsonOps.INSTANCE, element)
                        .resultOrPartial(msg -> HTLib.getLogger().error(msg + " [HTCodecRegistry] - " + this.registryName))
                        .ifPresent(l -> {
                            this.outerRegister(HTLib.prefix(file.getName().substring(0, file.getName().length() - 4)), l);
                        });
            } catch (Exception e) {
                HTLib.getLogger().error("Failed to load json from {}, skipping", file, e);
            }
        });
        HTLib.getLogger().info("HTCodecRegistry {} registered {} entries in data pack", this.registryName, this.getOuterCount());
    }

    public boolean containKey(ResourceLocation name) {
        return innerMap.containsKey(name) || outerMap.containsKey(name);
    }

    public boolean containValue(V value) {
        return innerMap.inverse().containsKey(value) || outerMap.inverse().containsKey(value);
    }

    @Override
    public List<V> getValues() {
        return Stream.concat(this.innerMap.values().stream(), this.outerMap.values().stream()).toList();
    }

    @Override
    public List<ResourceLocation> getIds() {
        return Stream.concat(this.innerMap.keySet().stream(), this.outerMap.keySet().stream()).toList();
    }

    @Override
    public List<Map.Entry<ResourceLocation, V>> getEntries() {
        return Stream.concat(this.innerMap.entrySet().stream(), this.outerMap.entrySet().stream()).toList();
    }

    public void syncToClient(ServerPlayer player) {
        this.outerMap.forEach((res, data) -> {
            this.getCodec()
                    .encodeStart(JsonOps.INSTANCE, data)
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(json -> {
                        NetworkHandler.sendToClient(player, new DataPackPacket(this.getRegistryName(), res, json));
                    });
        });
    }

    public void clearOutRegistries() {
        this.outerMap.clear();
    }

    public int getOuterCount() {
        return this.outerMap.size();
    }

    @Override
    public Optional<V> getValue(ResourceLocation type) {
        return Optional.ofNullable(this.innerMap.containsKey(type) ? this.innerMap.get(type) : this.outerMap.getOrDefault(type, null));
    }

    @Override
    public Optional<ResourceLocation> getKey(V value) {
        return Optional.ofNullable(this.containValue(value) ? this.innerMap.inverse().getOrDefault(value, this.outerMap.inverse().getOrDefault(value, null)) : null);
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Nonnull
    @Override
    public Codec<V> getCodec() {
        return this.supplier.get();
    }

    public Class<V> getRegistryClass() {
        return registryClass;
    }

    /**
     * 全局代表着独立于存档之外的数据包。
     * Determines whether it use data pack or not.
     */
    public boolean isGlobal() {
        return isGlobal;
    }
}
