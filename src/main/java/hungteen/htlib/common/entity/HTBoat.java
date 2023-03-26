package hungteen.htlib.common.entity;

import hungteen.htlib.common.WoodIntegrations;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:40
 **/
public class HTBoat extends Boat {

    private static final EntityDataAccessor<String> BOAT_TYPE = SynchedEntityData.defineId(HTBoat.class, EntityDataSerializers.STRING);

    public HTBoat(EntityType<? extends HTBoat> entityType, Level level) {
        super(entityType, level);
    }

    public HTBoat(Level level, double x, double y, double z) {
        this(HTEntities.BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BOAT_TYPE, WoodIntegrations.IBoatType.DEFAULT.getRegistryName());
    }

    @Override
    protected void checkFallDamage(double p_38307_, boolean p_38308_, BlockState blockState, BlockPos blockPos) {
        this.lastYd = this.getDeltaMovement().y;
        if (!this.isPassenger()) {
            if (p_38308_) {
                if (this.fallDistance > 3.0F) {
                    if (this.status != Boat.Status.ON_LAND) {
                        this.resetFallDistance();
                        return;
                    }

                    this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
                    if (!this.level.isClientSide && !this.isRemoved()) {
                        this.kill();
                        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            for(int i = 0; i < 3; ++i) {
                                this.spawnAtLocation(this.getHTBoatType().getPlanks());
                            }

                            for(int j = 0; j < 2; ++j) {
                                this.spawnAtLocation(Items.STICK);
                            }
                        }
                    }
                }

                this.resetFallDistance();
            } else if (!this.level.getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && p_38307_ < 0.0D) {
                this.fallDistance -= (float)p_38307_;
            }

        }
    }

    @Override
    public Item getDropItem() {
        return getHTBoatType().getBoatItem();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if(compoundTag.contains("HTBoatType")){
            this.setHTBoatType(WoodIntegrations.getBoatType(compoundTag.getString("HTBoatType")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putString("HTBoatType", this.getHTBoatType().getRegistryName());
    }

    public WoodIntegrations.IBoatType getHTBoatType() {
        return WoodIntegrations.getBoatType(entityData.get(BOAT_TYPE));
    }

    public void setHTBoatType(WoodIntegrations.IBoatType type){
        entityData.set(BOAT_TYPE, type.getRegistryName());
    }

}
