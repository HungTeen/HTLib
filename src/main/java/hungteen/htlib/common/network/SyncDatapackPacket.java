package hungteen.htlib.common.network;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.CodecHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/8/25 16:23
 */
public class SyncDatapackPacket {

    private final String type;
    private final ResourceLocation location;
    private final CompoundTag data;

    public SyncDatapackPacket(ResourceLocation type, ResourceLocation location, CompoundTag data) {
        this.type = type.toString();
        this.location = location;
        this.data = data;
    }

    public SyncDatapackPacket(FriendlyByteBuf buffer) {
        this.type = buffer.readUtf();
        this.location = new ResourceLocation(buffer.readUtf());
        this.data = buffer.readNbt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.type);
        buffer.writeUtf(this.location.toString());
        buffer.writeNbt(this.data);
    }

    public static class Handler {
        public static void onMessage(SyncDatapackPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                HTRegistryManager.get(new ResourceLocation(message.type)).ifPresent(registry -> {
                    registry.getSyncCodec().flatMap(codec -> CodecHelper.parse(codec, message.data)
                            .resultOrPartial(msg -> HTLib.getLogger().warn("HTLib sync datapack : " + msg))).ifPresent(value -> {
                        registry.syncRegister(message.location, value);
                    });

                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
