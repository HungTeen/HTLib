package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.widget.codec.RegistryEntriesCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 服务端 → 客户端：返回某个注册表的全部条目（排序后的资源定位字符串）。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:30
 **/
public class RegistryEntriesResponsePacket {

    private final String registryName;
    private final List<String> entries;

    public RegistryEntriesResponsePacket(String registryName, List<String> entries) {
        this.registryName = registryName;
        this.entries = entries;
    }

    public RegistryEntriesResponsePacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
        int size = buffer.readVarInt();
        this.entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.entries.add(buffer.readUtf());
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
        buffer.writeVarInt(entries.size());
        for (String entry : entries) {
            buffer.writeUtf(entry);
        }
    }

    public static void onMessage(RegistryEntriesResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RegistryEntriesCache.update(message.registryName, message.entries));
        ctx.get().setPacketHandled(true);
    }
}
