package hungteen.htlib.capability;

import hungteen.htlib.interfaces.IPlayerDataManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:27
 **/
public abstract class HTPlayerCapProvider<T extends IPlayerDataManager> implements ICapabilitySerializable<CompoundTag> {

    protected HTPlayerCapability<T> playerCap;

    protected LazyOptional<HTPlayerCapability<T>> playerCapOpt = LazyOptional.of(this::create);

    public HTPlayerCapProvider(Player player){
        this.playerCapOpt.ifPresent(cap -> cap.init(player));
    }

    protected abstract @NotNull HTPlayerCapability<T> create();

    @NotNull
    @Override
    public abstract <K> LazyOptional<K> getCapability(@NotNull Capability<K> cap);

    @NotNull
    @Override
    public <K> LazyOptional<K> getCapability(@NotNull Capability<K> cap, @Nullable Direction side) {
        return this.getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        return playerCap.get().saveToNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerCap.get().loadFromNBT(nbt);
    }
}
