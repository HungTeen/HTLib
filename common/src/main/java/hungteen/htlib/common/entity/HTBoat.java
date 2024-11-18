package hungteen.htlib.common.entity;

import hungteen.htlib.common.impl.HTLibBoatTypes;
import hungteen.htlib.util.HTBoatType;
import hungteen.htlib.util.HasHTBoatType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:40
 **/
public class HTBoat extends Boat implements HasHTBoatType {

    private static final EntityDataAccessor<String> BOAT_TYPE = SynchedEntityData.defineId(HTBoat.class, EntityDataSerializers.STRING);

    public HTBoat(EntityType<? extends HTBoat> entityType, Level level) {
        super(entityType, level);
    }

    public HTBoat(Level level, double x, double y, double z) {
        this(HTLibEntities.BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOAT_TYPE, HTLibBoatTypes.DEFAULT.getRegistryName());
    }

    @Override
    public @Nullable ItemEntity spawnAtLocation(ItemLike itemLike) {
        // Replace vanilla planks with the correct planks.
        if(itemLike.equals(getVariant().getPlanks())){
            itemLike = getHTBoatType().getPlanks();
        }
        return super.spawnAtLocation(itemLike);
    }

    @Override
    public Item getDropItem() {
        return getHTBoatType().getBoatItem();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if(compoundTag.contains("HTBoatType")){
            this.setHTBoatType(HTLibBoatTypes.getBoatType(compoundTag.getString("HTBoatType")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putString("HTBoatType", this.getHTBoatType().getRegistryName());
    }

    public HTBoatType getHTBoatType() {
        return HTLibBoatTypes.getBoatType(entityData.get(BOAT_TYPE));
    }

    public void setHTBoatType(HTBoatType type){
        entityData.set(BOAT_TYPE, type.getRegistryName());
    }

}
