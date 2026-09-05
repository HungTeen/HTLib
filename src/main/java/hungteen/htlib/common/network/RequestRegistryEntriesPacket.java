package hungteen.htlib.common.network;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 客户端 → 服务端：请求枚举某个注册表的全部条目。
 *
 * <p>数据包注册表默认不同步到客户端，Holder 字段的补全下拉需要服务端代为枚举。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:30
 **/
public class RequestRegistryEntriesPacket {

    private final String registryName;

    public RequestRegistryEntriesPacket(String registryName) {
        this.registryName = registryName;
    }

    public RequestRegistryEntriesPacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
    }

    public static void onMessage(RequestRegistryEntriesPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            NetworkHandler.sendToClient(sender, new RegistryEntriesResponsePacket(
                message.registryName, collectEntries(sender.server, message.registryName)));
        });
        ctx.get().setPacketHandled(true);
    }

    /** 服务端枚举注册表条目（数据包注册表也在关卡 RegistryAccess 里）。 */
    private static List<String> collectEntries(MinecraftServer server, String registryName) {
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        if (name == null) {
            return List.of();
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        Optional<? extends Registry<?>> registryOpt = server.registryAccess()
            .registry((ResourceKey) ResourceKey.createRegistryKey(name));
        if (registryOpt.isEmpty()) {
            return List.of();
        }
        return registryOpt.get().keySet().stream().sorted().map(ResourceLocation::toString).toList();
    }
}
