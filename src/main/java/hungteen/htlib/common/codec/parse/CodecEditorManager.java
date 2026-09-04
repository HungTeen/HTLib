package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.util.helper.CodecHelper;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Codec 编辑器的服务端逻辑：注册表列表 / schema 生成 / 保存文件。
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 14:00
 **/
public final class CodecEditorManager {

    private CodecEditorManager() {
    }

    /** 所有可编辑的数据包注册表名字。 */
    public static List<ResourceLocation> getRegistryNames() {
        return CodecHelper.getDatapackCodecs().stream()
                .map(RegistryDataLoader.RegistryData::key)
                .map(ResourceKey::location)
                .toList();
    }

    /** 解析某注册表 codec 为 schema JSON。 */
    public static Optional<JsonObject> getSchemaJson(String registryName) {
        return CodecHelper.getCodec(parseName(registryName))
                .map(codec -> {
                    DataSchema schema = CodecSchemaParser.parse(codec);
                    return SchemaPrinter.toJson(schema);
                });
    }

    /**
     * 保存 JSON 到指定路径。
     *
     * <p>路径规则：</p>
     * <ul>
     *   <li>{@code datapack:<pack>/<rel>} —— 保存到存档 datapack 目录
     *       {@code saves/<world>/datapacks/<pack>/<rel>}（相对 world 解析）；</li>
     *   <li>其余相对路径 —— 以服务器根目录为基准；</li>
     *   <li>绝对路径 —— 原样使用。</li>
     * </ul>
     *
     * @param server      服务器实例（用于定位世界 datapack 目录）
     * @param targetPath  目标路径
     * @param json        要写入的 JSON 文本
     */
    public static Optional<Path> saveJson(net.minecraft.server.MinecraftServer server, String targetPath, String json) {
        // 校验 JSON 合法。
        try {
            JsonParser.parseString(json);
        } catch (Exception e) {
            return Optional.empty();
        }
        try {
            Path file = resolvePath(server, targetPath);
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(file, json, StandardCharsets.UTF_8);
            return Optional.of(file);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static Path resolvePath(net.minecraft.server.MinecraftServer server, String targetPath) {
        if (targetPath.startsWith("datapack:")) {
            String rel = targetPath.substring("datapack:".length());
            Path worldDatapacks = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.DATAPACK_DIR);
            return worldDatapacks.resolve(rel);
        }
        Path p = Paths.get(targetPath);
        if (p.isAbsolute()) {
            return p;
        }
        return server.getServerDirectory().toPath().resolve(targetPath);
    }

    private static ResourceLocation parseName(String name) {
        return new ResourceLocation(name);
    }
}
