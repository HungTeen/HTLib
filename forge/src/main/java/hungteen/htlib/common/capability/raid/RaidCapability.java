package hungteen.htlib.common.capability.raid;

import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.api.raid.HTRaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 09:48
 **/
public class RaidCapability implements IRaidCapability {

    public static Capability<IRaidCapability> RAID_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    private Entity entity;
    private HTRaid raid = null;
    private int wave = 0;

    public static Optional<IRaidCapability> getRaid(Entity entity){
        return entity.getCapability(RAID_CAP).resolve();
    }

    public void init(Entity entity){
        this.entity = entity;
    }

    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("CurrentWave", this.wave);
        if(raid != null){
            tag.putInt("RaidID", this.raid.getEntityID());
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if(tag.contains("CurrentWave")){
            this.wave = tag.getInt("CurrentWave");
        }
        if(entity != null && tag.contains("RaidID")){
            if (entity.level() instanceof ServerLevel) {
                DummyEntityManager.getDummyEntity((ServerLevel) entity.level(), tag.getInt("RaidID")).ifPresent(dummyEntity -> {
                    if(dummyEntity instanceof HTRaid){
                        this.raid = (HTRaid)dummyEntity;
                    }
                });
            }

            if (this.raid != null) {
                this.raid.addRaider(entity);
//                if (this.isPatrolLeader()) {
//                    this.raid.setLeader(this.wave, this);
//                }
            }
        }
    }

    @Override
    public boolean isRaider() {
        return this.getRaid() != null;
    }

    @Override
    public void setRaid(HTRaid raid) {
        this.raid = raid;
    }

    @Override
    public HTRaid getRaid() {
        return this.raid;
    }

    @Override
    public void setWave(int wave) {
        this.wave = wave;
    }

    @Override
    public int getWave() {
        return this.wave;
    }
}
