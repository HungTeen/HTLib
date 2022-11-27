package hungteen.htlib.common.world.entity;

import hungteen.htlib.util.interfaces.IDummyEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:44
 **/
public abstract class DummyEntity implements IDummyEntity {

    private final DummyEntityType<?> entityType;
    private final Level level;

    public DummyEntity(DummyEntityType<?> entityType, Level level) {
        this.entityType = entityType;
        this.level = level;
    }

    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public DummyEntityType<?> getEntityType() {
        return entityType;
    }
}
