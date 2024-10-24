package hungteen.htlib.common.entity;

import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.util.BoatType;
import hungteen.htlib.util.HasHTBoatType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-08 19:45
 **/
public class HTChestBoat extends ChestBoat implements HasHTBoatType {

    private static final EntityDataAccessor<String> BOAT_TYPE = SynchedEntityData.defineId(HTChestBoat.class, EntityDataSerializers.STRING);

    public HTChestBoat(EntityType<? extends HTChestBoat> type, Level level) {
        super(type, level);
    }

    public HTChestBoat(Level level, double x, double y, double z) {
        this(HTLibEntities.CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOAT_TYPE, BoatType.DEFAULT.getRegistryName());
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
        return getHTBoatType().getChestBoatItem();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if(compoundTag.contains("HTBoatType")){
            this.setHTBoatType(BoatTypes.getBoatType(compoundTag.getString("HTBoatType")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putString("HTBoatType", this.getHTBoatType().getRegistryName());
    }

    public BoatType getHTBoatType() {
        return BoatTypes.getBoatType(entityData.get(BOAT_TYPE));
    }

    public void setHTBoatType(BoatType type){
        entityData.set(BOAT_TYPE, type.getRegistryName());
    }

}
