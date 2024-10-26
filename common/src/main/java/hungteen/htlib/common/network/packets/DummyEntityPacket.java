package hungteen.htlib.common.network.packets;

import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.HTPlayToClientPayload;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.HTLibDummyEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 09:31
 **/
public class DummyEntityPacket implements HTPlayToClientPayload {

    private final Operation operation;
    private int entityID;
    private ResourceLocation entityType;
    private CompoundTag entityNBT;

    public static final StreamCodec<FriendlyByteBuf, DummyEntityPacket> STREAM_CODEC = StreamCodec.of(
            DummyEntityPacket::encode,
            DummyEntityPacket::new
    );

    public DummyEntityPacket() {
        this.operation = Operation.CLEAR;
    }

    public DummyEntityPacket(DummyEntity dummyEntity, CompoundTag nbt) {
        this(Operation.UPDATE, dummyEntity);
        this.entityNBT = nbt;
    }

    public DummyEntityPacket(Operation operation, DummyEntity dummyEntity) {
        this.operation = operation;
        this.entityID = dummyEntity.getEntityID();
        if (this.operation == Operation.CREATE) {
            this.entityType = dummyEntity.getEntityType().getLocation();
            this.entityNBT = dummyEntity.save(new CompoundTag());
        }
    }

    public DummyEntityPacket(FriendlyByteBuf buffer) {
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

    public static void encode(FriendlyByteBuf buffer, DummyEntityPacket packet) {
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

    public enum Operation {

        CREATE,

        REMOVE,

        UPDATE,

        CLEAR,

        ;

    }

}
