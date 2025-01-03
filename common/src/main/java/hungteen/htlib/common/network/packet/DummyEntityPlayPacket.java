package hungteen.htlib.common.network.packet;

import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 09:31
 **/
public class DummyEntityPlayPacket implements PlayToClientPacket {

    public static final CustomPacketPayload.Type<DummyEntityPlayPacket> TYPE = new CustomPacketPayload.Type<>(HTLibHelper.prefix("dummy_entity_play"));
    private final Operation operation;
    private int entityID;
    private ResourceLocation entityType;
    private CompoundTag entityNBT;

    public static final StreamCodec<RegistryFriendlyByteBuf, DummyEntityPlayPacket> STREAM_CODEC = StreamCodec.of(
            DummyEntityPlayPacket::encode,
            DummyEntityPlayPacket::new
    );

    public DummyEntityPlayPacket() {
        this.operation = Operation.CLEAR;
    }

    public DummyEntityPlayPacket(DummyEntity dummyEntity, CompoundTag nbt) {
        this(Operation.UPDATE, dummyEntity);
        this.entityNBT = nbt;
    }

    public DummyEntityPlayPacket(Operation operation, DummyEntity dummyEntity) {
        this.operation = operation;
        this.entityID = dummyEntity.getEntityID();
        if (this.operation == Operation.CREATE) {
            this.entityType = dummyEntity.getEntityType().getLocation();
            this.entityNBT = dummyEntity.save(new CompoundTag());
        }
    }

    public DummyEntityPlayPacket(FriendlyByteBuf buffer) {
        this.operation = Operation.values()[buffer.readInt()];
        if (this.operation != Operation.CLEAR) {
            this.entityID = buffer.readInt();
            if (this.operation == Operation.CREATE) {
                this.entityType = ResourceLocation.parse(buffer.readUtf());
                this.entityNBT = buffer.readNbt();
            } else if (this.operation == Operation.UPDATE) {
                this.entityNBT = buffer.readNbt();
            }
        }
    }

    public static void encode(FriendlyByteBuf buffer, DummyEntityPlayPacket packet) {
        buffer.writeInt(packet.operation.ordinal());
        if (packet.operation != Operation.CLEAR) {
            buffer.writeInt(packet.entityID);
            if (packet.operation == Operation.CREATE) {
                buffer.writeUtf(packet.entityType.toString());
                buffer.writeNbt(packet.entityNBT);
            } else if (packet.operation == Operation.UPDATE) {
                buffer.writeNbt(packet.entityNBT);
            }
        }
    }

    @Override
    public void process(ClientPacketContext context) {
        switch (operation){
            case CREATE -> {
                HTLibDummyEntities.getEntityType(entityType).ifPresent(type -> {
                    DummyEntity dummyEntity = type.create(context.player().level(), entityNBT);
                    HTLibProxy.get().addDummyEntity(dummyEntity);
                });
            }
            case REMOVE -> HTLibProxy.get().removeDummyEntity(entityID);
            case UPDATE -> {
                HTLibProxy.get().getDummyEntity(entityID).ifPresent(dummyEntity -> {
                    dummyEntity.load(entityNBT);
                });
            }
            case CLEAR -> HTLibProxy.get().clearDummyEntities();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {

        CREATE,

        REMOVE,

        UPDATE,

        CLEAR,

        ;

    }

}
