package hungteen.htlib.common.network;

import hungteen.htlib.common.codec.parse.CodecEditorManager;
import hungteen.htlib.common.codec.parse.SchemaRegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 客户端 → 服务端：请求某个注册表的 schema。
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 14:06
 **/
public class RequestSchemaPacket {

    private final String registryName;

    public RequestSchemaPacket(String registryName) {
        this.registryName = registryName;
    }

    public RequestSchemaPacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
    }

    public static class Handler {
        public static void onMessage(RequestSchemaPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender != null) {
                    // 供 DispatchCodec 枚举解析数据驱动注册表。
                    SchemaRegistryAccess.set(sender.server.registryAccess());
                }
                CodecEditorManager.getSchemaJson(message.registryName).ifPresent(schema -> {
                    NetworkHandler.sendToClient(
                            ctx.get().getSender(),
                            new SchemaResponsePacket(message.registryName, schema.toString())
                    );
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
