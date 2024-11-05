package hungteen.htlib.common.network.packet;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 09:31
 **/
public class DummyEntityInitPacket implements PlayToClientPacket {

    public static final Type<DummyEntityInitPacket> TYPE = new Type<>(HTLibHelper.prefix("dummy_entity_init"));
    private final List<Pair<ResourceLocation, CompoundTag>> entities;

    public static final Codec<DummyEntityInitPacket> CODEC = RecordCodecBuilder.<DummyEntityInitPacket>mapCodec(instance -> instance.group(
            Codec.mapPair(
                    ResourceLocation.CODEC.fieldOf("type"),
                    CompoundTag.CODEC.fieldOf("nbt")
            ).codec().listOf().fieldOf("entities").forGetter(DummyEntityInitPacket::getEntities)
    ).apply(instance, DummyEntityInitPacket::new)).codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, DummyEntityInitPacket> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public DummyEntityInitPacket(Collection<DummyEntity> dummyEntities) {
        this.entities = dummyEntities.stream().map(entity -> Pair.of(entity.getEntityType().getLocation(), entity.save(new CompoundTag()))).toList();
    }

    public DummyEntityInitPacket(List<Pair<ResourceLocation, CompoundTag>> entities) {
        this.entities = entities;
    }

    public List<Pair<ResourceLocation, CompoundTag>> getEntities() {
        return entities;
    }

    @Override
    public void process(ClientPacketContext context) {
        HTLibProxy.get().clearDummyEntities();
        getEntities().forEach(pair -> {
            HTLibDummyEntities.getEntityType(pair.getFirst()).ifPresent(type -> {
                DummyEntity dummyEntity = type.create(context.player().level(), pair.getSecond());
                HTLibProxy.get().addDummyEntity(dummyEntity);
            });
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
