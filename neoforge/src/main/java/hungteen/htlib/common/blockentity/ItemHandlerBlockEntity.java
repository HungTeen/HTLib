package hungteen.htlib.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-04 08:54
 **/
@Deprecated(since = "1.0.0", forRemoval = true)
public abstract class ItemHandlerBlockEntity extends HTNameableBlockEntity {

//    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public ItemHandlerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState state) {
        super(blockEntityType, blockPos, state);
    }

    public abstract ItemStackHandler getItemHandler();

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
//        this.lazyItemHandler = LazyOptional.of(this::getItemHandler);
    }

    @Override
    public void invalidateCapabilities() {
        super.invalidateCapabilities();
//        this.lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("ItemHandler", this.getItemHandler().serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if(tag.contains("ItemHandler")){
            this.getItemHandler().deserializeNBT(provider, tag.getCompound("ItemHandler"));
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("GrassCarp");
    }
}
