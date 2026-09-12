package hungteen.htlib.common.network;

import com.mojang.serialization.DataResult;
import hungteen.htlib.common.codec.parse.CodecEditorManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 客户端 → 服务端：保存编辑结果到指定路径。
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 14:07
 **/
public class SaveDataPacket {

    private final String registryName;
    private final String targetPath;
    private final String json;

    public SaveDataPacket(String registryName, String targetPath, String json) {
        this.registryName = registryName;
        this.targetPath = targetPath;
        this.json = json;
    }

    public SaveDataPacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
        this.targetPath = buffer.readUtf();
        this.json = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
        buffer.writeUtf(targetPath);
        buffer.writeUtf(json);
    }

    public static class Handler {
        public static void onMessage(SaveDataPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var sender = ctx.get().getSender();
                if (sender == null) {
                    return;
                }
                // 先校验：Holder 引用需要服务端完整的 RegistryAccess 才能解析，不通过则不落盘，只回传原因
                Optional<String> error = CodecEditorManager.validate(sender.server, message.registryName, message.json);
                if (error.isPresent()) {
                    reply(sender, false, error.get());
                    return;
                }
                DataResult<Path> saved = CodecEditorManager.saveJson(sender.server, message.targetPath, message.json);
                saved.result().ifPresentOrElse(
                        path -> reply(sender, true, path.toString()),
                        () -> reply(sender, false, saved.error().map(err -> err.message()).orElse("写入失败"))
                );
            });
            ctx.get().setPacketHandled(true);
        }

        /** 结果同时写状态栏（响应包）与聊天栏（界面已关闭时仍能看到路径 / 原因）。 */
        private static void reply(ServerPlayer sender, boolean success, String message) {
            NetworkHandler.sendToClient(sender, new EditorResultPacket(EditorResultPacket.ACTION_SAVE, success, message));
            sender.sendSystemMessage(Component.literal(success ? "已保存 → " + message : "保存失败：" + message));
        }
    }
}
