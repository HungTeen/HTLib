package hungteen.htlib.common.network;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 09:31
 **/
public class DummyEntityPacket {

    private final Operation operation;
    private int entityID;
    private ResourceLocation entityType;
    private CompoundTag entityNBT;

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
        if(this.operation == Operation.CREATE){
            this.entityType = dummyEntity.getEntityType().getLocation();
            this.entityNBT = dummyEntity.save(new CompoundTag());
        }
    }

    public DummyEntityPacket(FriendlyByteBuf buffer) {
        this.operation = Operation.values()[buffer.readInt()];
        if(this.operation != Operation.CLEAR){
            this.entityID = buffer.readInt();
            if(this.operation == Operation.CREATE){
                this.entityType = new ResourceLocation(buffer.readUtf());
                this.entityNBT = buffer.readNbt();
            } else if(this.operation == Operation.UPDATE){
                this.entityNBT = buffer.readNbt();
            }
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.operation.ordinal());
        if(this.operation != Operation.CLEAR){
            buffer.writeInt(this.entityID);
            if(this.operation == Operation.CREATE){
                buffer.writeUtf(this.entityType.toString());
                buffer.writeNbt(this.entityNBT);
            } else if(this.operation == Operation.UPDATE){
                buffer.writeNbt(this.entityNBT);
            }
        }
    }

    public static class Handler {

        public static void onMessage(DummyEntityPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(()->{
                if(message.operation == Operation.CREATE){
                    HTDummyEntities.getEntityType(message.entityType).ifPresent(type -> {
                        Optional<Level> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
                        DummyEntity dummyEntity = world.map(w -> type.create(w, message.entityNBT)).orElse(null);
                        if (dummyEntity != null) {
                            HTLib.PROXY.addDummyEntity(dummyEntity);
                        } else{
                            HTLib.getLogger().error("Fail to sync Dummy Entity on client side");
                        }
                    });
                } else if(message.operation == Operation.REMOVE){
                    HTLib.PROXY.removeDummyEntity(message.entityID);
                } else if(message.operation == Operation.UPDATE){
                    HTLib.PROXY.getDummyEntity(message.entityID).ifPresent(dummyEntity -> {
                        dummyEntity.load(message.entityNBT);
                    });
                } else if(message.operation == Operation.CLEAR){
                    HTLib.PROXY.clearDummyEntities();
                }
            });
            ctx.get().setPacketHandled(true);
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
