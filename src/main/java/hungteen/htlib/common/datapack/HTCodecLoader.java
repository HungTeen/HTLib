package hungteen.htlib.common.datapack;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-25 21:02
 **/
public class HTCodecLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();

    public HTCodecLoader() {
        super(GSON, "hungteen");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller filler) {
        Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
        HTRegistryManager.getRegistryNames(false).forEach(res -> {
            map.putAll(prepare(manager, filler, res));
        });
        return map;
    }

    /**
     * Copy super to fit multi directories.
     */
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager manager, ProfilerFiller filler, String directory) {
        Map<ResourceLocation, JsonElement> map = Maps.newHashMap();

        for(Map.Entry<ResourceLocation, Resource> entry : manager.listResources(directory, (location) -> {
            return location.getPath().endsWith(".json"); // 获取所有有效的数据json。
        }).entrySet()) {
            final ResourceLocation location = entry.getKey();
            final String path = location.getPath();
            final ResourceLocation registry = new ResourceLocation(location.getNamespace(), path.substring(0, path.length() - PATH_SUFFIX_LENGTH));
            try {
                Reader reader = entry.getValue().openAsReader();
                try {
                    JsonElement jsonelement = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                    if (jsonelement != null) {
                        JsonElement jsonelement1 = map.put(registry, jsonelement);
                        if (jsonelement1 != null) {
                            throw new IllegalStateException("Duplicate data file ignored with ID " + registry);
                        }
                    } else {
                        HTLib.getLogger().error("Couldn't load data file {} from {} as it'path null or empty", registry, location);
                    }
                } catch (Throwable throwable1) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable throwable) {
                            throwable1.addSuppressed(throwable);
                        }
                    }
                    throw throwable1;
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                HTLib.getLogger().error("Couldn't parse data file {} from {}", registry, location, exception);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elementMap, ResourceManager manager, ProfilerFiller filler) {
        HTRegistryManager.getRegistryNames(false).forEach(res -> {
            final Optional<HTCodecRegistry<?>> registryOpt = HTRegistryManager.get(res);
            if(registryOpt.isPresent()){
                registryOpt.get().clearOutRegistries(); // 先清除再加入新的。
                elementMap.entrySet().stream().filter(entry -> entry.getKey().getPath().startsWith(res)).forEach(entry -> {
                    registryOpt.ifPresent(registry -> registry.getCodec().parse(JsonOps.INSTANCE, entry.getValue())
                            .resultOrPartial(msg -> HTLib.getLogger().error(msg))
                            .ifPresent(obj -> {
                                registry.outerRegister(entry.getKey(), obj);
                            }));
                });
                HTLib.getLogger().info("HTCodecRegistry {} registered {} entries in data pack", res, registryOpt.get().getOuterCount());
            } else{
                HTLib.getLogger().error("Missing HTCodecRegistry for {}", res);
            }
        });
    }

    @Override
    public String getName() {
        return "HT Codec Registry Loader";
    }
}
