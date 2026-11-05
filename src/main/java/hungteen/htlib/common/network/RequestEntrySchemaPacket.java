package hungteen.htlib.common.network;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import hungteen.htlib.common.codec.parse.CodecEditorManager;
import hungteen.htlib.common.codec.parse.SchemaRegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：请求某个数据包注册表"条目结构"的 schema（不是 Holder 包装，而是条目本身的字段结构），
 * 供 Holder 字段的展开模式（内联定义）构建子表单。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:50
 **/
public class RequestEntrySchemaPacket {

    private final String registryName;

    public RequestEntrySchemaPacket(String registryName) {
        this.registryName = registryName;
    }

    public RequestEntrySchemaPacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName, EditorPacketLimits.MAX_ID_LENGTH);
    }

    public static void onMessage(RequestEntrySchemaPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            if (!sender.hasPermissions(2)) {
                NetworkHandler.sendToClient(sender, EntrySchemaResponsePacket.failure(message.registryName, "需要权限等级 2 才能编辑数据包"));
                return;
            }
            try {
                String json = SchemaRegistryAccess.withAccess(sender.server.registryAccess(),
                    () -> CodecEditorManager.getCompactSchemaJson(message.registryName).map(JsonObject::toString).orElse(""));
                EntrySchemaResponsePacket reply = json.isEmpty()
                    ? EntrySchemaResponsePacket.failure(message.registryName, "未知的数据包注册表")
                    : !EditorPacketLimits.fitsSchema(json)
                        ? EntrySchemaResponsePacket.failure(message.registryName, "Schema 超出网络大小限制")
                        : new EntrySchemaResponsePacket(message.registryName, json);
                NetworkHandler.sendToClient(sender, reply);
            } catch (RuntimeException ex) {
                LogUtils.getLogger().warn("Could not generate editor schema for {}", message.registryName, ex);
                NetworkHandler.sendToClient(sender, EntrySchemaResponsePacket.failure(message.registryName, ex.getMessage()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
