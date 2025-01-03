package hungteen.htlib.common.blockentity;

import hungteen.htlib.util.helper.impl.BlockHelper;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-04 08:55
 **/
public abstract class ContainerBlockEntity extends HTNameableBlockEntity implements WorldlyContainer {

    public ContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState state) {
        super(blockEntityType, blockPos, state);
    }

    public abstract NonNullList<ItemStack> getItems();

    /**
     * Ignore direction.
     */
    public boolean canPlaceItem(int id, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canPlaceItemThroughFace(int id, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(id, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int id, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return getItems().size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : getItems()) {
            if(! stack.isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int id) {
        return getItems().get(id);
    }

    @Override
    public ItemStack removeItem(int id, int count) {
        return ContainerHelper.removeItem(this.getItems(), id, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int id) {
        return ContainerHelper.takeItem(this.getItems(), id);
    }

    @Override
    public void setItem(int id, ItemStack stack) {
        this.getItems().set(id, stack);
        if(stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return BlockHelper.stillValid(player, this);
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }

    @Override
    protected Component getDefaultName() {
        return Component.empty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("ContainerItems", ContainerHelper.saveAllItems(new CompoundTag(), this.getItems(), provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.getItems().clear();
        if(tag.contains("ContainerItems")){
            ContainerHelper.loadAllItems(tag.getCompound("ContainerItems"), this.getItems(), provider);
        }
    }

}
