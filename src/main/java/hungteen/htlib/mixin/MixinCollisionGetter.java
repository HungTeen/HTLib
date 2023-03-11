package hungteen.htlib.mixin;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 21:10
 **/
@Mixin(CollisionGetter.class)
public interface MixinCollisionGetter {

    @Shadow
    List<VoxelShape> getEntityCollisions(@Nullable Entity p_186427_, AABB p_186428_);

    @Shadow
    Iterable<VoxelShape> getBlockCollisions(@Nullable Entity p_186435_, AABB p_186436_);

    @Shadow
    VoxelShape borderCollision(Entity p_186441_, AABB p_186442_);

    /**
     * @author HT
     * @reason Mixin不支持注入接口。
     */
    @Overwrite()
    default boolean noCollision(@Nullable Entity entity, AABB aabb) {
        for(VoxelShape voxelshape : this.getBlockCollisions(entity, aabb)) {
            if (!voxelshape.isEmpty()) {
                return false;
            }
        }

        if (!this.getEntityCollisions(entity, aabb).isEmpty()) {
            return false;
        } else if (entity == null) {
            return true;
        } else {
            AtomicBoolean hasCollision = new AtomicBoolean(false);
            HTLib.PROXY.getDummyEntities(entity.level).stream().filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
                if (! dummyEntity.ignoreEntity(entity) && dummyEntity.isCloseToBorder(entity, aabb) && dummyEntity.getCollisionShapes(entity).isPresent()) {
                    if (Shapes.joinIsNotEmpty(dummyEntity.getCollisionShapes(entity).get(), Shapes.create(aabb), BooleanOp.AND)) {
                        hasCollision.set(true);
                    }
                }
            });
            if (hasCollision.get()) {
                return false;
            }

            VoxelShape voxelshape1 = this.borderCollision(entity, aabb);
            return voxelshape1 == null || !Shapes.joinIsNotEmpty(voxelshape1, Shapes.create(aabb), BooleanOp.AND);
        }
    }

}
