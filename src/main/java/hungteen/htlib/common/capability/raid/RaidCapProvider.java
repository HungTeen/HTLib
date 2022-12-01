package hungteen.htlib.common.capability.raid;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 09:51
 **/
public class RaidCapProvider implements ICapabilitySerializable<CompoundTag> {

    private RaidCapability raidCapability;
    private LazyOptional<RaidCapability> raiderCapOpt = LazyOptional.of(this::create);

    public RaidCapProvider(Entity raider){
        this.raiderCapOpt.ifPresent(cap -> cap.init(raider));
    }

    private @NotNull RaidCapability create(){
        if(raidCapability == null){
            raidCapability = new RaidCapability();
        }
        return raidCapability;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return this.getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == RaidCapability.RAID_CAP){
            return raiderCapOpt.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return raidCapability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        raidCapability.deserializeNBT(nbt);
    }
}
