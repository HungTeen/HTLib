package hungteen.htlib.mixin;

import com.google.common.collect.ImmutableList;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 22:02
 **/
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void collideBoundingBox(@Nullable Entity entity, Vec3 vec3, AABB aabb, Level level, List<VoxelShape> voxelShapes, CallbackInfoReturnable<Vec3> result, ImmutableList.Builder<VoxelShape> builder) {
        if(entity != null){
            HTLib.PROXY.getDummyEntities(level).stream().filter(DummyEntity::hasCollision).forEach(dummyEntity -> {
                if(dummyEntity.isCloseToBorder(entity, aabb.expandTowards(vec3))){
                    builder.addAll(dummyEntity.getCollisionShapes(entity));
                }
            });
            result.setReturnValue(Entity.collideWithShapes(vec3, aabb, builder.build()));
        }
    }
}
