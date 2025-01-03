package hungteen.htlib.common.entity;

import com.google.common.collect.ImmutableList;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.impl.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/3 11:04
 */
public class SeatEntity extends HTEntity {

    private static final ImmutableList<Vec3i> DISMOUNT_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
    private static final ImmutableList<Vec3i> DISMOUNT_OFFSETS = (new ImmutableList.Builder<Vec3i>()).addAll(DISMOUNT_HORIZONTAL_OFFSETS).addAll(DISMOUNT_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(DISMOUNT_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();
    private static final EntityDataAccessor<Float> MAX_Y_ROT = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.FLOAT);
    private boolean relyOnBlock = false;

    public SeatEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public SeatEntity(Level world, Vec3 pos, float yRot, float maxYRot) {
        this(HTLibEntities.SEAT.get(), world);
        this.setPos(pos);
        this.setRot(yRot, 0);
        this.setSeatMaxYRot(maxYRot);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MAX_Y_ROT, 120F);
    }

    public static boolean seatAt(Level level, LivingEntity entity, BlockPos seatPos, double yOffset, Direction direction, float maxYRot, boolean relyOnBlock){
        return seatAt(level, entity, seatPos, yOffset, direction.getOpposite().toYRot(), maxYRot, relyOnBlock);
    }

    public static boolean seatAt(Level level, LivingEntity entity, BlockPos seatPos, double yOffset, float yRot, float maxYRot, boolean relyOnBlock){
        if(! level.isClientSide){
            List<SeatEntity> seats = EntityHelper.getPredicateEntities(entity, MathHelper.getBlockAABB(seatPos), SeatEntity.class, s -> true);
            if(seats.isEmpty()){
                final SeatEntity seat = new SeatEntity(level, new Vec3(seatPos.getX() + 0.5F, seatPos.getY() + yOffset, seatPos.getZ() + 0.5F), yRot, maxYRot);
                seat.setRelyOnBlock(relyOnBlock);
                level.addFreshEntity(seat);
                entity.startRiding(seat);
            }
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if(EntityHelper.isServer(this)){
            if(this.isSeatInvalid()){
                this.removeSeat();
            }
        }
    }

    public boolean isSeatInvalid(){
        return this.getPassengers().isEmpty() || (this.isRelyOnBlock() && this.level().isEmptyBlock(this.blockPosition()));
    }

    public void removeSeat(){
        this.discard();
        if(this.isRelyOnBlock()){
            this.level().updateNeighbourForOutputSignal(this.blockPosition(), this.level().getBlockState(this.blockPosition()).getBlock());
        }
    }

    @Override
    protected void addPassenger(Entity entity) {
        super.addPassenger(entity);
        entity.setYRot(this.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        super.onPassengerTurned(entity);
        this.clampRotation(entity);
    }

    @Override
    public void positionRider(Entity entity, Entity.MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        this.clampRotation(entity);
    }

    /**
     * See respawn anchor block.
     */
    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for(Vec3i vec3i : DISMOUNT_OFFSETS) {
            pos.set(this.blockPosition()).move(vec3i);
            Vec3 vec3 = DismountHelper.findSafeDismountLocation(entity.getType(), this.level(), pos, true);
            if (vec3 != null) {
                return vec3;
            }
        }
        return super.getDismountLocationForPassenger(entity);
    }

    /**
     * Copy from vanilla boat.
     */
    protected void clampRotation(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        final float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        final float f1 = Mth.clamp(f, - getSeatMaxYRot(), getSeatMaxYRot());
        entity.yRotO += f1 - f;
        entity.setYRot(entity.getYRot() + f1 - f);
        entity.setYHeadRot(entity.getYRot());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("RelyOnBlock")){
            this.setRelyOnBlock(tag.getBoolean("RelyOnBlock"));
        }
        if(tag.contains("SeatMaxYRot")){
            this.setSeatMaxYRot(tag.getFloat("SeatMaxYRot"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("RelyOnBlock", this.isRelyOnBlock());
        tag.putFloat("SeatMaxYRot", this.getSeatMaxYRot());
    }

    public boolean isRelyOnBlock() {
        return relyOnBlock;
    }

    public void setRelyOnBlock(boolean relyOnBlock) {
        this.relyOnBlock = relyOnBlock;
    }

    public float getSeatMaxYRot(){
        return entityData.get(MAX_Y_ROT);
    }

    public void setSeatMaxYRot(float maxYRot){
        entityData.set(MAX_Y_ROT, maxYRot);
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity entity) {
        return super.getVehicleAttachmentPoint(entity);
    }
}
