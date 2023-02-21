package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultComponentType;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-12 21:59
 **/
public record ChestResult(boolean forLoss, boolean forVictory, boolean onGround, ResourceLocation loot) implements IResultComponent {

    public static final Codec<ChestResult> CODEC = RecordCodecBuilder.<ChestResult>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("for_loss", false).forGetter(ChestResult::forLoss),
            Codec.BOOL.optionalFieldOf("for_victory", true).forGetter(ChestResult::forVictory),
            Codec.BOOL.optionalFieldOf("on_ground", true).forGetter(ChestResult::onGround),
            ResourceLocation.CODEC.fieldOf("loot").forGetter(ChestResult::loot)
    ).apply(instance, ChestResult::new)).codec();


    @Override
    public void apply(IRaid raid, ServerLevel level, int tick) {
        if(tick == 0 && ((raid.isDefeated() && forVictory()) || (raid.isLost() && forLoss()))){
            BlockPos pos = onGround() ? WorldHelper.getSuitableHeightPos(level, MathHelper.toBlockPos(raid.getPosition())) : MathHelper.toBlockPos(raid.getPosition());
            if(level.isEmptyBlock(pos) || level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()){
                level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
                RandomizableContainerBlockEntity.setLootTable(level, level.getRandom(), pos, loot());
            }
        }
    }

    @Override
    public void applyToDefender(IRaid raid, Entity defender, int tick) {

    }

    @Override
    public void applyToRaider(IRaid raid, Entity raider, int tick) {

    }

    @Override
    public IResultComponentType<?> getType() {
        return HTResultComponents.CHEST;
    }
}
