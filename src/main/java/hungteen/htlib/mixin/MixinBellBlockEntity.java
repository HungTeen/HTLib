package hungteen.htlib.mixin;

import hungteen.htlib.common.capability.raid.RaidCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/5 10:46
 **/
@Mixin(BellBlockEntity.class)
public class MixinBellBlockEntity {

    @Inject(method = "areRaidersNearby(Lnet/minecraft/core/BlockPos;Ljava/util/List;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void areRaidersNearby(BlockPos p_155200_, List<LivingEntity> p_155201_, CallbackInfoReturnable<Boolean> cir) {
        for (LivingEntity livingEntity : p_155201_) {
            if (RaidCapability.isRaider(livingEntity)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "isRaiderWithinRange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void isRaiderWithinRange(BlockPos p_155197_, LivingEntity p_155198_, CallbackInfoReturnable<Boolean> cir) {
        if (RaidCapability.isRaider(p_155198_)) {
            cir.setReturnValue(true);
        }
    }
}
