package hungteen.htlib.common.world.entity;

import hungteen.htlib.api.registry.SimpleEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:34
 **/
public final class DummyEntityType<T extends DummyEntityImpl> implements SimpleEntry {

    private final ResourceLocation location;
    private final Factory<T> factory;

    public DummyEntityType(ResourceLocation location, Factory<T> factory) {
        this.location = location;
        this.factory = factory;
    }

    public T create(Level level, CompoundTag tag) {
        T dummyEntity = this.factory.create(this, level, tag);
        dummyEntity.load(tag);
        return dummyEntity;
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

    public interface Factory<T extends DummyEntityImpl> {

        @NotNull
        T create(DummyEntityType<T> entityType, Level level, CompoundTag tag);
    }
}
