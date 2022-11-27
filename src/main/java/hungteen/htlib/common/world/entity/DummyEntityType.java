package hungteen.htlib.common.world.entity;

import hungteen.htlib.api.interfaces.ISimpleRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:34
 **/
public final class DummyEntityType<T extends DummyEntity> implements ISimpleRegistry {

    private final ResourceLocation location;
    private final Factory<T> factory;

    public DummyEntityType(ResourceLocation location, Factory<T> factory) {
        this.location = location;
        this.factory = factory;
    }

    @Nullable
    public T create(Level level, CompoundTag tag) {
        return this.factory.create(this, level, tag);
    }

    @Override
    public MutableComponent getComponent() {
        return Component.translatable("entity." + this.getModID() + ".dummy." + this.getName());
    }

    @Override
    public String getName() {
        return location.getPath();
    }

    @Override
    public String getModID() {
        return location.getNamespace();
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    public interface Factory<T extends DummyEntity> {
        T create(DummyEntityType<T> entityType, Level level, CompoundTag tag);
    }
}
