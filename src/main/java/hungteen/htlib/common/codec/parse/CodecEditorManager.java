package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
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

/** Server-side discovery, original-Codec validation and confined datapack writes. */
public final class CodecEditorManager {
    private CodecEditorManager() {
    }

    public static List<ResourceLocation> getRegistryNames() {
        return CodecHelper.getDatapackCodecs().stream().map(RegistryDataLoader.RegistryData::key)
            .map(ResourceKey::location).toList();
    }

    public static Optional<JsonObject> getSchemaJson(String registryName) {
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        return name == null ? Optional.empty() : CodecHelper.getCodec(name).map(codec -> {
            DataSchema schema = CodecSchemaParser.parse(codec);
            return SchemaPrinter.toJson(schema);
        });
    }

    /** Holder references require the full server RegistryAccess, not the client's partial registries. */
    public static Optional<String> validate(MinecraftServer server, String registryName, String json) {
        if (!EditorPacketLimits.fitsJson(json)) {
            return Optional.of("JSON 超过 30000 个 UTF-8 字节的限制");
        }
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        var codec = name == null ? Optional.<com.mojang.serialization.Codec<?>>empty() : CodecHelper.getCodec(name);
        if (codec.isEmpty()) return Optional.of("未知的注册表类型：" + registryName);
        try {
            CodecJsonValidator.parseAndValidate(codec.get(), RegistryOps.create(JsonOps.INSTANCE, server.registryAccess()), json);
            return Optional.empty();
        } catch (RuntimeException ex) {
            return Optional.of(EditorPacketLimits.resultMessage(ex.getMessage()));
        }
    }

    /** Accept identifiers instead of arbitrary paths; validation always precedes disk writes. */
    public static DataResult<Path> saveJson(MinecraftServer server, String registryName, String packName,
                                             String resourceId, String json) {
        Optional<String> error = validate(server, registryName, json);
        if (error.isPresent()) return DataResult.error(error::get);
        ResourceLocation registry = ResourceLocation.tryParse(registryName);
        ResourceLocation resource = ResourceLocation.tryParse(resourceId);
        if (registry == null || resource == null) return DataResult.error(() -> "注册表或资源 ID 不合法");
        try {
            return DataResult.success(DatapackFileStorage.save(server.getWorldPath(LevelResource.DATAPACK_DIR), packName,
                registry.getNamespace(), registry.getPath(), resource.getNamespace(), resource.getPath(), json));
        } catch (IllegalArgumentException ex) {
            return DataResult.error(() -> EditorPacketLimits.resultMessage(ex.getMessage()));
        } catch (IOException | RuntimeException ex) {
            LogUtils.getLogger().warn("Could not save datapack {} resource {}", packName, resourceId, ex);
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
