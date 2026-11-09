package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.common.network.EditorPacketLimits;
import hungteen.htlib.util.helper.CodecHelper;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Path;
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
        return getSchema(registryName).map(SchemaPrinter::toJson);
    }

    /** 网络传输使用共享定义，避免重复展开同一结构。 */
    public static Optional<JsonObject> getCompactSchemaJson(String registryName) {
        return getSchema(registryName).map(SchemaPrinter::toCompactJson);
    }

    private static Optional<DataSchema> getSchema(String registryName) {
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        return name == null ? Optional.empty() : CodecHelper.getCodec(name).map(CodecSchemaParser::parse);
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
        if (!EditorPacketLimits.fitsJson(json)) {
            return Optional.of("JSON 超过 30000 个 UTF-8 字节的限制");
        }
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        Optional<? extends Codec<?>> codecOpt = name == null ? Optional.empty() : CodecHelper.getCodec(name);
        if (codecOpt.isEmpty()) {
            return Optional.of("未知的注册表类型：" + registryName);
        }
        try {
            CodecJsonValidator.parseAndValidate(codecOpt.get(), RegistryOps.create(JsonOps.INSTANCE, server.registryAccess()), json);
            return Optional.empty();
        } catch (RuntimeException ex) {
            return Optional.of(EditorPacketLimits.resultMessage(ex.getMessage()));
        }
    }

    /**
     * 校验并保存 JSON 到存档的数据包目录，路径由数据包名、注册表名和资源 ID 推导。
     *
     * @param server       服务器实例（用于定位世界 datapack 目录）
     * @param registryName 数据包注册表名
     * @param packName     数据包目录名
     * @param resourceId   条目资源 ID
     * @param json         要写入的 JSON 文本
     * @return 成功时携带写入的文件路径，失败时携带错误原因
     */
    public static DataResult<Path> saveJson(MinecraftServer server, String registryName, String packName,
                                            String resourceId, String json) {
        Optional<String> error = validate(server, registryName, json);
        if (error.isPresent()) {
            return DataResult.error(error::get);
        }
        ResourceLocation registry = ResourceLocation.tryParse(registryName);
        ResourceLocation resource = ResourceLocation.tryParse(resourceId);
        if (registry == null || resource == null) {
            return DataResult.error(() -> "注册表或资源 ID 不合法");
        }
        try {
            return DataResult.success(DatapackFileStorage.save(server.getWorldPath(LevelResource.DATAPACK_DIR), packName,
                registry.getNamespace(), registry.getPath(), resource.getNamespace(), resource.getPath(), json));
        } catch (IllegalArgumentException ex) {
            return DataResult.error(() -> EditorPacketLimits.resultMessage(ex.getMessage()));
        } catch (IOException | RuntimeException ex) {
            HTLib.getLogger().warn("Could not save datapack {} resource {}", packName, resourceId, ex);
            return DataResult.error(() -> "写入文件失败：" + EditorPacketLimits.resultMessage(ex.getMessage()));
        }
    }

    public static String displayPath(MinecraftServer server, Path file) {
        try {
            return "datapacks/" + server.getWorldPath(LevelResource.DATAPACK_DIR).toRealPath()
                .relativize(file).toString().replace('\\', '/');
        } catch (IOException ex) {
            return file.toString();
        }
    }
}
