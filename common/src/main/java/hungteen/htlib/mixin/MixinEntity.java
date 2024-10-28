package hungteen.htlib.mixin;

import com.google.common.collect.ImmutableList;
import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 22:02
 **/
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract void baseTick();

    /**
     * 不能移动到边界之外。
     */
    @Inject(method = "Lnet/minecraft/world/entity/Entity;collectColliders(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void collectColliders(@Nullable Entity entity, Level level, List<VoxelShape> voxelShapes, AABB aabb, CallbackInfoReturnable<List<VoxelShape>> result, ImmutableList.Builder<VoxelShape> builder) {
        if(entity != null){
            HTLibProxy.get().getDummyEntities(level).stream().filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
                if(! dummyEntity.ignoreEntity(entity) && dummyEntity.isCloseToBorder(entity, aabb)){
                    if(! level.isClientSide){
                        dummyEntity.collideWith(entity);
                    }
                    dummyEntity.getCollisionShapes(entity).ifPresent(builder::add);
                }
            });
            result.setReturnValue(builder.build());
        }
    }
}
