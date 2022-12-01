package hungteen.htlib.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 22:02
 **/
@Mixin(Entity.class)
public class MixinEntity {

//    @Inject(method = "collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;",
//            at = @At("RETURN"),
//            locals = LocalCapture.CAPTURE_FAILHARD
//    )
//    private static void collideBoundingBox(@Nullable Entity entity, Vec3 vec3, AABB aabb, Level level, List<VoxelShape> voxelShapes, CallbackInfoReturnable<Vec3> result, ImmutableList.Builder<VoxelShape> builder) {
//        if(entity != null){
//            Util.getProxy().getFormations(level).forEach(formation -> {
//                if(formation.isInsideCloseToBorder(entity, aabb.expandTowards(vec3))){
//                    builder.add(formation.getFormationCollisionShape());
//                }
//            });
//        }
//    }
}
