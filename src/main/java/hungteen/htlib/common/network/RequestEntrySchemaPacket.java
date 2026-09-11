package hungteen.htlib.common.network;

import com.google.gson.JsonObject;
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
        this.registryName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
    }

    public static void onMessage(RequestEntrySchemaPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            // 供 DispatchCodec 枚举解析数据驱动注册表。
            SchemaRegistryAccess.set(sender.server.registryAccess());
            // 无论成败都回包，避免客户端监听者永久挂起（空串表示失败）。
            String schemaJson = CodecEditorManager.getSchemaJson(message.registryName)
                .map(JsonObject::toString).orElse("");
            NetworkHandler.sendToClient(sender,
                new EntrySchemaResponsePacket(message.registryName, schemaJson));
        });
        ctx.get().setPacketHandled(true);
    }
}
