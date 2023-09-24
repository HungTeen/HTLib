package hungteen.htlib.common.entity;

import hungteen.htlib.util.helper.registry.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-02 13:44
 **/
public class HTEntity extends Entity {

    private boolean firstSpawn = false;

    public HTEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.firstSpawn = tag.getBoolean("first_spawn");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("first_spawn", this.firstSpawn);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.tickCount <= 5) {
            this.refreshDimensions();
        }
        if(! this.firstSpawn){
            this.firstSpawn = true;
            this.onFirstSpawn();
        }
    }

    public void onFirstSpawn(){

    }

    protected void tickMove() {
        this.tickMove(1F, 0.8F);
    }

    protected void tickMove(float motionFactor, float motionFactorInWater) {
        final Vec3 curSpeed = this.getDeltaMovement();
        final Vec3 nextTickPos = this.position().add(curSpeed);
        if (this.isInWater()) {
            ParticleHelper.spawnParticles(level(), ParticleTypes.BUBBLE, nextTickPos.subtract(curSpeed.scale(0.25)), 4, curSpeed);
            this.setDeltaMovement(curSpeed.scale(motionFactorInWater));
        } else {
            this.setDeltaMovement(curSpeed.scale(motionFactor));
        }
        if (! this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0, this.getGravityVelocity(), 0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
