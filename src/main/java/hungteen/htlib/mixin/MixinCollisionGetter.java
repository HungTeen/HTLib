package hungteen.htlib.mixin;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 21:10
 **/
@Mixin(CollisionGetter.class)
public interface MixinCollisionGetter {

    @Inject(method = "noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/CollisionGetter;borderCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Lnet/minecraft/world/phys/shapes/VoxelShape;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            remap = false
    )
    default void noCollision(@Nullable Entity entity, AABB aabb, CallbackInfoReturnable<Boolean> result) {
        if(entity != null){
            AtomicBoolean hasCollision = new AtomicBoolean(false);
            HTLib.PROXY.getDummyEntities(entity.level).stream().filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
                if (! dummyEntity.ignoreEntity(entity) && dummyEntity.isCloseToBorder(entity, aabb) && dummyEntity.getCollisionShapes(entity).isPresent()) {
                    if (Shapes.joinIsNotEmpty(dummyEntity.getCollisionShapes(entity).get(), Shapes.create(aabb), BooleanOp.AND)) {
                        hasCollision.set(true);
                    }
                }
            });
            if (hasCollision.get()) {
                result.setReturnValue(false);
            }
        }
    }

}
