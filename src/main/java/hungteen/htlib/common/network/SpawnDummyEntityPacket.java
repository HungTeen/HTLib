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
public class SpawnDummyEntityPacket {

    private final boolean add;
    private final int entityID;
    private ResourceLocation entityType;
    private CompoundTag entityNBT;

    public SpawnDummyEntityPacket(boolean add, DummyEntity dummyEntity) {
        this.add = add;
        this.entityID = dummyEntity.getEntityID();
        if(this.add){
            this.entityType = dummyEntity.getEntityType().getLocation();
            this.entityNBT = dummyEntity.save(new CompoundTag());
        }
    }

    public SpawnDummyEntityPacket(FriendlyByteBuf buffer) {
        this.add = buffer.readBoolean();
        this.entityID = buffer.readInt();
        if(this.add){
            this.entityType = new ResourceLocation(buffer.readUtf());
            this.entityNBT = buffer.readNbt();
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.add);
        buffer.writeInt(this.entityID);
        if(this.add){
            buffer.writeUtf(this.entityType.toString());
            buffer.writeNbt(this.entityNBT);
        }
    }

    public static class Handler {

        public static void onMessage(SpawnDummyEntityPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(()->{
                if(message.add){
                    HTDummyEntities.DUMMY_ENTITY_TYPES.getValue(message.entityType).ifPresent(type -> {
                        Optional<Level> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
                        DummyEntity dummyEntity = world.map(w -> type.create(w, message.entityNBT)).orElse(null);
                        if (dummyEntity != null) {
                            HTLib.PROXY.addDummyEntity(dummyEntity);
                        }
                    });
                } else{
                    HTLib.PROXY.removeDummyEntity(message.entityID);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
