package hungteen.htlib.common.network;

import hungteen.htlib.common.codec.parse.CodecEditorManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** Client requests identify a pack and resource; only the server derives their file path. */
public class SaveDataPacket {
    private final UUID requestId;
    private final String registryName;
    private final String datapackName;
    private final String resourceId;
    private final String json;

    public SaveDataPacket(UUID requestId, String registryName, String datapackName, String resourceId, String json) {
        this.requestId = requestId;
        this.registryName = registryName;
        this.datapackName = datapackName;
        this.resourceId = resourceId;
        this.json = json;
    }

    public SaveDataPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH), buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH),
            buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH), buffer.readUtf(EditorPacketLimits.MAX_JSON_LENGTH));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(requestId);
        buffer.writeUtf(registryName, EditorPacketLimits.MAX_ID_LENGTH);
        buffer.writeUtf(datapackName, EditorPacketLimits.MAX_ID_LENGTH);
        buffer.writeUtf(resourceId, EditorPacketLimits.MAX_ID_LENGTH);
        buffer.writeUtf(json, EditorPacketLimits.MAX_JSON_LENGTH);
    }

    public static class Handler {
        public static void onMessage(SaveDataPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;
                if (!sender.hasPermissions(2)) {
                    reply(sender, message.requestId, false, "需要权限等级 2 才能编辑数据包");
                    return;
                }
                var saved = CodecEditorManager.saveJson(sender.server, message.registryName, message.datapackName,
                    message.resourceId, message.json);
                saved.result().ifPresentOrElse(
                    path -> reply(sender, message.requestId, true, CodecEditorManager.displayPath(sender.server, path)),
                    () -> reply(sender, message.requestId, false, saved.error().map(err -> err.message()).orElse("写入失败")));
            });
            ctx.get().setPacketHandled(true);
        }

        private static void reply(ServerPlayer sender, UUID requestId, boolean success, String message) {
            String detail = EditorPacketLimits.resultMessage(message);
            NetworkHandler.sendToClient(sender, new EditorResultPacket(EditorResultPacket.ACTION_SAVE, requestId, success, detail));
            sender.sendSystemMessage(Component.translatable(success ? "htlib.screen.saved" : "htlib.screen.save_failed", detail));
        }
    }
}
