package hungteen.htlib.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import hungteen.htlib.common.codec.parse.*;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.OpenCodecPacket;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2026/8/10 18:31
 **/
public class IDECommand {

    private static final SuggestionProvider<CommandSourceStack> ALL_DATAPACK_CODECS =
        SuggestionProviders.register(HTLibHelper.prefix("all_datapack_codecs"), (commandContext, builder) -> {
            return SharedSuggestionProvider.suggestResource(CodecEditorManager.getRegistryNames().stream(), builder);
        });

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> builder =
            Commands.literal("ide").requires((ctx) -> ctx.hasPermission(2));
        builder.then(Commands.literal("schema").then(Commands.literal("types").then(
                Commands.argument("registry", ResourceLocationArgument.id()).suggests(ALL_DATAPACK_CODECS)
                    .executes(ctx -> listSchemaTypes(ctx.getSource(), ResourceLocationArgument.getId(ctx, "registry")))))
            .then(Commands.argument("registry", ResourceLocationArgument.id()).suggests(ALL_DATAPACK_CODECS)
                .executes(ctx -> printSchema(ctx.getSource(), ResourceLocationArgument.getId(ctx, "registry"), false))
                .then(Commands.literal("types")
                    .executes(ctx -> listSchemaTypes(ctx.getSource(), ResourceLocationArgument.getId(ctx, "registry"))))
                .then(Commands.literal("json").executes(
                    ctx -> printSchema(ctx.getSource(), ResourceLocationArgument.getId(ctx, "registry"), true))).then(
                    Commands.literal("type").then(Commands.argument("type", StringArgumentType.greedyString()).executes(
                        ctx -> printSchemaType(ctx.getSource(), ResourceLocationArgument.getId(ctx, "registry"),
                            StringArgumentType.getString(ctx, "type")))))));
        builder.then(Commands.literal("editor").executes(ctx -> openEditor(ctx.getSource())));
//        builder.then(Commands.literal("view").executes(ctx -> openViewer(ctx.getSource(), null)).then(
//            Commands.argument("registry", ResourceLocationArgument.id()).suggests(ALL_DATAPACK_CODECS)
//                .executes(ctx -> openViewer(ctx.getSource(), ResourceLocationArgument.getId(ctx, "registry")))));
        dispatcher.register(builder);
    }

    /**
     * 输出某个数据包注册表的 codec 数据格式。
     *
     * <p>聊天框分页输出文本格式（每页若干行，避免单行过长塞不下）；
     * {@code writeJson} 为 true 时把完整 JSON 写入服务器目录 {@code schema_output/<registry>.json}。</p>
     */
    public static int printSchema(CommandSourceStack source, ResourceLocation registryName, boolean writeJson) {
        Optional<? extends Codec<?>> codecOpt = CodecHelper.getCodec(registryName);
        if (codecOpt.isEmpty()) {
            source.sendFailure(Component.literal("未找到数据包注册表 " + registryName + " 对应的 codec"));
            return 0;
        }
        DataSchema schema = CodecSchemaParser.parse(codecOpt.get());
        String text = SchemaPrinter.print(schema);
        sendChunks(source, "=== " + registryName + " 格式 ===", text);

        if (writeJson) {
            writeSchemaJson(source, registryName, schema);
        }
        return 1;
    }

    /**
     * 列出某个 dispatch 注册表的全部类型值（type）。
     *
     * <p>枚举自 keyCodec：枚举常量或注册表条目（见 {@link DispatchCodecInspector#typeToCodec}）。</p>
     */
    public static int listSchemaTypes(CommandSourceStack source, ResourceLocation registryName) {
        Optional<? extends Codec<?>> codecOpt = CodecHelper.getCodec(registryName);
        if (codecOpt.isEmpty()) {
            source.sendFailure(Component.literal("未找到数据包注册表 " + registryName + " 对应的 codec"));
            return 0;
        }
        Object dispatch = CodecUnwrapper.unwrap(codecOpt.get());
        if (!DispatchCodecInspector.isDispatch(dispatch)) {
            source.sendFailure(Component.literal(registryName + " 不是 dispatch codec，没有类型列表"));
            return 0;
        }
        java.util.Map<String, Object> typeToCodec = DispatchCodecInspector.typeToCodec(dispatch);
        if (typeToCodec.isEmpty()) {
            source.sendSuccess(
                () -> Component.literal("无法枚举 " + registryName + " 的类型值（keyCodec 既非枚举也定位不到注册表）"),
                false);
            return 1;
        }
        StringBuilder sb = new StringBuilder(
            "分派键=" + DispatchCodecInspector.typeKey(dispatch) + "，共 " + typeToCodec.size() + " 个类型:");
        for (String type : typeToCodec.keySet()) {
            sb.append("\n- ").append(type);
        }
        sendChunks(source, "=== " + registryName + " 类型列表 ===", sb.toString());
        return 1;
    }

    /**
     * 按 type 反查 dispatch codec 的分支，只输出该分支的格式。
     */
    public static int printSchemaType(CommandSourceStack source, ResourceLocation registryName, String type) {
        Optional<? extends Codec<?>> codecOpt = CodecHelper.getCodec(registryName);
        if (codecOpt.isEmpty()) {
            source.sendFailure(Component.literal("未找到数据包注册表 " + registryName + " 对应的 codec"));
            return 0;
        }
        Object dispatch = CodecUnwrapper.unwrap(codecOpt.get());
        if (!DispatchCodecInspector.isDispatch(dispatch)) {
            source.sendFailure(Component.literal(registryName + " 不是 dispatch codec"));
            return 0;
        }
        Optional<Object> branch = DispatchCodecInspector.codecForType(dispatch, type);
        if (branch.isEmpty() || !(branch.get() instanceof Codec<?> codec)) {
            source.sendFailure(Component.literal(
                "未找到类型 " + type + "（可用 /htlib schema types " + registryName + " 查看全部类型）"));
            return 0;
        }
        DataSchema schema = CodecSchemaParser.parse(codec);
        sendChunks(source, "=== " + registryName + " 类型 " + type + " 格式 ===", SchemaPrinter.print(schema));
        return 1;
    }

    /**
     * 打开 Codec 编辑器界面。
     *
     * <p>服务端收集全部数据包注册表名，通过 {@link OpenCodecPacket}
     * 发给客户端打开界面。</p>
     */
    public static int openEditor(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<ResourceLocation> names = CodecEditorManager.getRegistryNames();
        NetworkHandler.sendToClient(player, new OpenCodecPacket(names, "", OpenCodecPacket.Mode.EDITOR));
        source.sendSuccess(() -> Component.literal("已打开 Codec 编辑器"), true);
        return 1;
    }

    /**
     * 打开 Codec 字段查看器。
     *
     * <p>{@code registry} 为空时只打开查看器列表；指定时预选该注册表并直接请求 schema。</p>
     */
    public static int openViewer(CommandSourceStack source, ResourceLocation registry) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<ResourceLocation> names = CodecEditorManager.getRegistryNames();
        String preselect = registry != null ? registry.toString() : "";
        NetworkHandler.sendToClient(player, new OpenCodecPacket(names, preselect, OpenCodecPacket.Mode.VIEWER));
        source.sendSuccess(() -> Component.literal("已打开 Codec 字段查看器"), true);
        return 1;
    }

    /** 把完整 schema JSON 写入服务器目录 schema_output/<registry>.json。 */
    private static void writeSchemaJson(CommandSourceStack source, ResourceLocation registryName, DataSchema schema) {
        try {
            Path dir = source.getServer().getServerDirectory().toPath().resolve("schema_output");
            Files.createDirectories(dir);
            Path file = dir.resolve(registryName.getNamespace() + "_" + registryName.getPath() + ".json");
            Files.writeString(file, SchemaPrinter.toJson(schema).toString(), StandardCharsets.UTF_8);
            source.sendSuccess(() -> Component.literal("完整 JSON 已写入 " + file), true);
        } catch (IOException e) {
            source.sendFailure(Component.literal("写入 schema JSON 失败: " + e.getMessage()));
        }
    }

    /**
     * 分页发送长文本到聊天框：按换行切成若干条消息，每页最多 {@link #CHUNK_LINES} 行， 单行超长自动截断，避免"一行太长塞不下"。
     */
    private static final int CHUNK_LINES = 16;
    private static final int MAX_LINE_LENGTH = 80;

    private static void sendChunks(CommandSourceStack source, String title, String text) {
        source.sendSuccess(() -> Component.literal(title), false);
        List<String> lines = text.lines().toList();
        for (int i = 0; i < lines.size(); i += CHUNK_LINES) {
            int end = Math.min(i + CHUNK_LINES, lines.size());
            StringBuilder page = new StringBuilder();
            for (int j = i; j < end; j++) {
                String line = lines.get(j);
                page.append(truncateLine(line)).append('\n');
            }
            source.sendSuccess(() -> Component.literal(page.toString()), false);
        }
    }

    private static String truncateLine(String line) {
        if (line != null && line.length() > MAX_LINE_LENGTH) {
            return line.substring(0, MAX_LINE_LENGTH) + "…";
        }
        return line;
    }
}
