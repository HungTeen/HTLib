package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.widget.codec.EntrySchemaCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：返回某个数据包注册表条目结构的 schema JSON。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:50
 **/
public class EntrySchemaResponsePacket {

    private final String registryName;
    private final String schemaJson;

    public EntrySchemaResponsePacket(String registryName, String schemaJson) {
        this.registryName = registryName;
        this.schemaJson = schemaJson;
    }

    public EntrySchemaResponsePacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
        this.schemaJson = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
        buffer.writeUtf(schemaJson);
    }

    public static void onMessage(EntrySchemaResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> EntrySchemaCache.update(message.registryName, message.schemaJson));
        ctx.get().setPacketHandled(true);
    }
}
