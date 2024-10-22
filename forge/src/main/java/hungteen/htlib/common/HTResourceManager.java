package hungteen.htlib.common;

import com.google.gson.*;
import hungteen.htlib.HTLibForgeInitializer;
import hungteen.htlib.util.helper.VanillaHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.ModList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

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

        ModList.get().getMods().stream().map(info -> ModList.get().getModContainerById(info.getModId()))
                .filter(Optional::isPresent).map(Optional::get).forEach(mod -> {
                    if (mod.getModId().equals(VanillaHelper.get().getModID())) return; // skip minecraft.
                    findFiles(Collections.singletonList(mod.getModInfo().getOwningFile().getFile().getSecureJar().getRootPath()), String.format("%s/%s/%s", PACK_TYPE.getDirectory(), mod.getModId(), LOCATION), Files::exists, (path, file) -> {
                        return collectJson(mod.getModId(), path, file, (fileStr, bookId) -> {
                            final String assetPath = fileStr.substring(fileStr.indexOf(PACK_TYPE.getDirectory() + "/"));
                            try (InputStream stream = Files.newInputStream(mod.getModInfo().getOwningFile().getFile().findResource(assetPath))) {
                                loadModel(bookId, stream, false);
                            } catch (Exception e) {
                                HTLibForgeInitializer.getLogger().error("Failed to load book {} defined by mod {}, skipping",
                                        bookId, mod.getModId(), e);
                            }
                        });
                    }, true, 2);
                });

//        foundJsons.forEach((pair, file) -> {
//            ModContainer mod = pair.getFirst();
//            ResourceLocation res = pair.getSecond();
//
//            try (InputStream stream = Files.newInputStream(mod.getModInfo().getOwningFile().getFile().findResource(file))) {
//                loadModel(res, stream, false);
//            } catch (Exception e) {
//                HTLib.getLogger().error("Failed to load book {} defined by mod {}, skipping",
//                        res, mod.getModId(), e);
//            }
//        });

        ModList.get().getModContainerById(HTLibForgeInitializer.id()).ifPresent(self -> {
            findFiles(Collections.singletonList(loadDir.toPath()), "", Files::exists, (path, file) -> {
                return collectJson(VanillaHelper.get().getModID(), path, file, (assetPath, bookId) -> {
                    try (FileInputStream stream = new FileInputStream(file.toFile())) {
                        loadModel(bookId, stream, false);
                    } catch (Exception e) {
                        HTLibForgeInitializer.getLogger().error("Failed to load book {} defined by external, skipping",
                                bookId, e);
                    }
                });
            }, true, 2);
//            File[] subdirs = loadDir.listFiles(File::isDirectory);
//            if (subdirs == null) {
//                HTLib.getLogger().warn("Failed to list external books in {}, not loading external books",
//                        loadDir.getAbsolutePath());
//                return;
//            }
//
//            for (File dir : subdirs) {
//                ResourceLocation res;
//                try {
//                    res = new ResourceLocation(PatchouliAPI.MOD_ID, dir.getName());
//                } catch (ResourceLocationException ex) {
//                    HTLib.getLogger().error("Invalid external book folder name {}, skipping", dir.getName(), ex);
//                    continue;
//                }
//
//                File bookJson = new File(dir, "book.json");
//                try (FileInputStream stream = new FileInputStream(bookJson)) {
//                    loadModel(self, res, stream, true);
//                } catch (Exception e) {
//                    PatchouliAPI.LOGGER.error("Failed to load external book json from {}, skipping", bookJson, e);
//                }
//            }
        });
    }

    private static boolean collectJson(String modId, Path path, Path file, BiConsumer<String, ResourceLocation> consumer){
        if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".json")) {
            String fileStr = file.toString().replaceAll("\\\\", "/"); // 规范化路径。
            String relPath = fileStr.substring(fileStr.indexOf(LOCATION) + LOCATION.length() + 1); // 去掉前缀。
            String bookName = relPath.substring(0, relPath.indexOf(".")); // 去掉后缀。

            if (bookName.contains("/")) {
                HTLibForgeInitializer.getLogger().warn("Ignored book.json @ {}", file);
                return true;
            }

            ResourceLocation bookId = new ResourceLocation(modId, bookName);

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
                models.add(new ResourceLocation(jsonElement.getAsString()));
            }
        });
    }

    public static void findFiles(List<Path> paths, String base, Predicate<Path> rootFilter,
                                 BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles) {
        findFiles(paths, base, rootFilter, processor, visitAllFiles, Integer.MAX_VALUE);
    }

    public static void findFiles(List<Path> paths, String base, Predicate<Path> rootFilter,
                                 BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth) {
        try {
            for (var root : paths) {
                walk(root.resolve(base), rootFilter, processor, visitAllFiles, maxDepth);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor,
                             boolean visitAllFiles, int maxDepth) throws IOException {
        if (root == null || !Files.exists(root) || !rootFilter.test(root)) {
            return;
        }

        if (processor != null) {
            try (var stream = Files.walk(root, maxDepth)) {
                Iterator<Path> itr = stream.iterator();

                while (itr.hasNext()) {
                    boolean keepGoing = processor.apply(root, itr.next());

                    if (!visitAllFiles && !keepGoing) {
                        return;
                    }
                }
            }
        }
    }

}
