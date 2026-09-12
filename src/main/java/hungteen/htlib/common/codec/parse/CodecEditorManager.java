package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.util.helper.CodecHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

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
     * 校验 JSON 是否满足某注册表的 codec。
     *
     * <p>Holder 引用（{@link net.minecraft.resources.RegistryFileCodec}）只有在 {@link RegistryOps} 下
     * 才会按注册名解析成条目，而客户端 RegistryAccess 只含内置注册表与提供 networkCodec 的数据包注册表，
     * 故校验统一在服务端用完整的 RegistryAccess 完成。</p>
     *
     * @param server       服务端实例（提供完整 RegistryAccess）
     * @param registryName 数据包注册表名
     * @param json         待校验的 JSON 文本
     * @return 通过时 empty，不通过时返回错误原因
     */
    public static Optional<String> validate(MinecraftServer server, String registryName, String json) {
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        Optional<? extends Codec<?>> codecOpt = name == null ? Optional.empty() : CodecHelper.getCodec(name);
        if (codecOpt.isEmpty()) {
            return Optional.of("未知的注册表类型：" + registryName);
        }
        try {
            return validate(codecOpt.get(), server.registryAccess(), JsonParser.parseString(json));
        } catch (Exception e) {
            return Optional.of("JSON 解析失败：" + e.getMessage());
        }
    }

    /** 用给定 RegistryAccess 构造 RegistryOps 解析；解析不出完整结果即校验不通过（忽视可容忍的 partial 警告）。 */
    private static Optional<String> validate(Codec<?> codec, RegistryAccess access, JsonElement element) {
        DataResult<?> result = codec.parse(RegistryOps.create(JsonOps.INSTANCE, access), element);
        if (result.result().isPresent()) {
            return Optional.empty();
        }
        return Optional.of(result.error().map(err -> err.message()).orElse("解析失败"));
    }

    /**
     * 保存 JSON 到指定路径。
     *
     * <p>调用方应先用 {@link #validate} 确认内容合法。</p>
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
     * @return 成功时携带写入的文件路径，失败时携带错误原因
     */
    public static DataResult<Path> saveJson(MinecraftServer server, String targetPath, String json) {
        try {
            Path file = resolvePath(server, targetPath);
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(file, json, StandardCharsets.UTF_8);
            return DataResult.success(file);
        } catch (IOException e) {
            return DataResult.error(() -> "写入文件失败：" + e.getMessage());
        }
    }

    private static Path resolvePath(MinecraftServer server, String targetPath) {
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
