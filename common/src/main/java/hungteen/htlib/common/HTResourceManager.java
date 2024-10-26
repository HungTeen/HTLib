package hungteen.htlib.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.platform.HTLibPlatformAPI;
import hungteen.htlib.util.helper.FileHelper;
import hungteen.htlib.util.helper.impl.VanillaHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2023/12/9 22:59
 **/
public class HTResourceManager {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    private static final String LOCATION = "extra_models";
    private static final Set<ResourceLocation> models = new HashSet<>();
    private static final PackType PACK_TYPE = PackType.CLIENT_RESOURCES;
    private static File loadDir;

    private static void setup() {
        loadDir = new File(LOCATION);
        if (!loadDir.exists()) {
            loadDir.mkdir();
        } else if (!loadDir.isDirectory()) {
            throw new RuntimeException(loadDir.getAbsolutePath() + " is a file, not a folder, aborting. Please delete this file or move it elsewhere if it has important contents.");
        }
    }

    public static Collection<ResourceLocation> getExtraModels() {
        return models.stream().toList();
    }

    public static void init() {
        if (loadDir == null) {
            setup();
        }

        // load assets from mods.
        HTLibPlatformAPI.get().getModInfoList().stream().map(info -> HTLibPlatformAPI.get().getModContainer(info.getModId()))
                .filter(Optional::isPresent).map(Optional::get).forEach(mod -> {
                    if (mod.getModId().equals(VanillaHelper.get().getModID())) return; // skip minecraft.
                    FileHelper.findFiles(mod.getRootPaths(), String.format("%s/%s/%s", PACK_TYPE.getDirectory(), mod.getModId(), LOCATION), Files::exists, (path, file) -> {
                        return collectJson(mod.getModId(), path, file, (fileStr, bookId) -> {
                            final String assetPath = fileStr.substring(fileStr.indexOf(PACK_TYPE.getDirectory() + "/"));
                            try (InputStream stream = Files.newInputStream(mod.getPath(assetPath))) {
                                loadModel(bookId, stream, false);
                            } catch (Exception e) {
                                HTLibAPI.logger().error("Failed to load book {} defined by mod {}, skipping",
                                        bookId, mod.getModId(), e);
                            }
                        });
                    }, true, 2);
                });

        // load global assets.
        HTLibPlatformAPI.get().getModContainer(HTLibAPI.id()).ifPresent(self -> {
            FileHelper.findFiles(Collections.singletonList(loadDir.toPath()), "", Files::exists, (path, file) -> {
                return collectJson(VanillaHelper.get().getModID(), path, file, (assetPath, bookId) -> {
                    try (FileInputStream stream = new FileInputStream(file.toFile())) {
                        loadModel(bookId, stream, false);
                    } catch (Exception e) {
                        HTLibAPI.logger().error("Failed to load book {} defined by external, skipping",
                                bookId, e);
                    }
                });
            }, true, 2);
        });
    }

    private static boolean collectJson(String modId, Path path, Path file, BiConsumer<String, ResourceLocation> consumer){
        if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".json")) {
            String fileStr = file.toString().replaceAll("\\\\", "/"); // 规范化路径。
            String relPath = fileStr.substring(fileStr.indexOf(LOCATION) + LOCATION.length() + 1); // 去掉前缀。
            String bookName = relPath.substring(0, relPath.indexOf(".")); // 去掉后缀。

            if (bookName.contains("/")) {
                HTLibAPI.logger().warn("Ignored book.json @ {}", file);
                return true;
            }

            ResourceLocation bookId = ResourceLocation.fromNamespaceAndPath(modId, bookName);

            consumer.accept(fileStr, bookId);
        }

        return true;
    }

    public static void loadModel(ResourceLocation res, InputStream stream, boolean external) {
        Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        var tree = GSON.fromJson(reader, JsonObject.class);
        final JsonArray modelArray = GsonHelper.getAsJsonArray(tree, "models", new JsonArray());
        modelArray.forEach(jsonElement -> {
            if (jsonElement.isJsonPrimitive()) {
                models.add(ResourceLocation.parse(jsonElement.getAsString()));
            }
        });
    }

}
