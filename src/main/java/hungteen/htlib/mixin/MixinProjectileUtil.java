package hungteen.htlib.mixin;

import hungteen.htlib.common.world.entity.DummyEntityManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-04 13:58
 **/
@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {

    /**
     * 不能碰到边界之外。
     */
    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void getEntityHitResult(Entity entity, Vec3 p_37289_, Vec3 p_37290_, AABB aabb, Predicate<Entity> p_37292_, double p_37293_, CallbackInfoReturnable<HitResult> result) {
        DummyEntityManager.getCollisionEntities(entity.level()).forEach(dummyEntity -> {
            if(! dummyEntity.ignoreEntity(entity) && dummyEntity.requireBlockProjectile(entity, aabb)){
                if (!entity.level().isClientSide()){
                    dummyEntity.collideWith(entity);
                }
                result.setReturnValue(null);
            }
        });
    }

    /**
     * 不能碰到边界之外。
     */
    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void getEntityHitResult(Level level, Entity entity, Vec3 from, Vec3 to, AABB aabb, Predicate<Entity> predicate, float p_150182_, CallbackInfoReturnable<EntityHitResult> result) {
        DummyEntityManager.getCollisionEntities(entity.level()).forEach(dummyEntity -> {
            if(! dummyEntity.ignoreEntity(entity) && dummyEntity.requireBlockProjectile(entity, aabb)){
                if (!entity.level().isClientSide()){
                    dummyEntity.collideWith(entity);
                }
                result.setReturnValue(null);
            }
        });
    }

}
