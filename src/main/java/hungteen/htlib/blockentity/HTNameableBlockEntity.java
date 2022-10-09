package hungteen.htlib.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 09:34
 **/
public abstract class HTNameableBlockEntity extends HTBlockEntity implements Nameable {

    private Component name;

    public HTNameableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState state) {
        super(blockEntityType, blockPos, state);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
        }

    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    public void setCustomName(Component component) {
        this.name = component;
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    /**
     * display name when there is no custom name.
     */
    protected abstract Component getDefaultName();


}
