package hungteen.htlib.common.network.packet;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/8/25 16:23
 */
public record SyncDatapackPacket(ResourceLocation dataType, ResourceLocation location, CompoundTag data) implements PlayToClientPacket {

    public static final CustomPacketPayload.Type<SyncDatapackPacket> TYPE = new CustomPacketPayload.Type<>(HTLibHelper.prefix("sync_datapack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDatapackPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            SyncDatapackPacket::dataType,
            ResourceLocation.STREAM_CODEC,
            SyncDatapackPacket::location,
            ByteBufCodecs.TRUSTED_COMPOUND_TAG,
            SyncDatapackPacket::data,
            SyncDatapackPacket::new
    );

    @Override
    public void process(ClientPacketContext context) {
        HTRegistryManager.getCodecRegistry(dataType()).ifPresent(registry -> {
            registry.getSyncCodec().flatMap(codec -> CodecHelper.parse(codec, data())
                    .resultOrPartial(msg -> HTLibAPI.logger().error("HTLib sync datapack : {}", msg))).ifPresent(value -> {
                registry.syncRegister(location(), value);
            });
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
