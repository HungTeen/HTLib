package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.screen.codec.CodecScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：返回某注册表的 schema JSON。
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 14:06
 **/
public class SchemaResponsePacket {

    private final String registryName;
    private final String schemaJson;

    public SchemaResponsePacket(String registryName, String schemaJson) {
        this.registryName = registryName;
        this.schemaJson = schemaJson;
    }

    public SchemaResponsePacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
        this.schemaJson = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
        buffer.writeUtf(schemaJson);
    }

    public static class Handler {
        public static void onMessage(SchemaResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> CodecScreen.onSchema(message.registryName, message.schemaJson));
            ctx.get().setPacketHandled(true);
        }
    }
}
