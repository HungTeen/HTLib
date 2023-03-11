package hungteen.htlib.mixin;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 19:28
 **/
@Mixin(DismountHelper.class)
public class MixinDismountHelper {

    /**
     * 防止卡骑乘bug到边界之外。
     */
    @Inject(method = "canDismountTo(Lnet/minecraft/world/level/CollisionGetter;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/AABB;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/CollisionGetter;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void canDismountTo(CollisionGetter collisionGetter, LivingEntity livingEntity, AABB aabb, CallbackInfoReturnable<Boolean> result) {
        HTLib.PROXY.getDummyEntities(livingEntity.level).stream().filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
            if (dummyEntity.requireBlock(livingEntity, aabb)) {
                result.setReturnValue(false);
            }
        });
    }

//    @Inject(method = "findSafeDismountLocation(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/CollisionGetter;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/phys/Vec3;",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/CollisionGetter;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"),
//            locals = LocalCapture.PRINT,
//            cancellable = true
//    )
//    private static void findSafeDismountLocation(EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos blockPos, boolean p_38445_, CallbackInfoReturnable<Vec3> result) {
//        if (collisionGetter instanceof Level) {
//            AtomicBoolean blocked = new AtomicBoolean(false);
//            DummyEntityManager.getCollisionEntities((Level) collisionGetter).filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
//                if (dummyEntity.requireBlock(blockPos, aabb)) {
//                    blocked.set(true);
//                }
//            });
//            if(blocked.get()){
//                result.setReturnValue(null);
//            }
//        }
//    }

    /**
     * @author
     * @reason
     */
    @Overwrite()
    public static Vec3 findSafeDismountLocation(EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos blockPos, boolean p_38445_) {
        if (p_38445_ && entityType.isBlockDangerous(collisionGetter.getBlockState(blockPos))) {
            return null;
        } else {
            double d0 = collisionGetter.getBlockFloorHeight(DismountHelper.nonClimbableShape(collisionGetter, blockPos), () -> {
                return DismountHelper.nonClimbableShape(collisionGetter, blockPos.below());
            });
            if (!DismountHelper.isBlockFloorValid(d0)) {
                return null;
            } else if (p_38445_ && d0 <= 0.0D && entityType.isBlockDangerous(collisionGetter.getBlockState(blockPos.below()))) {
                return null;
            } else {
                Vec3 vec3 = Vec3.upFromBottomCenterOf(blockPos, d0);
                AABB aabb = entityType.getDimensions().makeBoundingBox(vec3);

                for (VoxelShape voxelshape : collisionGetter.getBlockCollisions(null, aabb)) {
                    if (!voxelshape.isEmpty()) {
                        return null;
                    }
                }

                if (collisionGetter instanceof Level) {
                    AtomicBoolean blocked = new AtomicBoolean(false);
                    DummyEntityManager.getCollisionEntities((Level) collisionGetter).filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
                        if (dummyEntity.requireBlock(blockPos, aabb)) {
                            blocked.set(true);
                        }
                    });
                    if(blocked.get()){
                        return null;
                    }
                }

                return !collisionGetter.getWorldBorder().isWithinBounds(aabb) ? null : vec3;
            }
        }
    }

}
