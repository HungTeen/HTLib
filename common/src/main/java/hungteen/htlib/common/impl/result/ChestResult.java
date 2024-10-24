package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.raid.HTRaid;
import hungteen.htlib.api.raid.ResultComponent;
import hungteen.htlib.api.raid.ResultType;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.WorldHelper;
import hungteen.htlib.util.helper.impl.LootHelper;
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
public record ChestResult(boolean forLoss, boolean forVictory, boolean onGround, ResourceLocation loot) implements ResultComponent {

    public static final MapCodec<ChestResult> CODEC = RecordCodecBuilder.<ChestResult>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("for_loss", false).forGetter(ChestResult::forLoss),
            Codec.BOOL.optionalFieldOf("for_victory", true).forGetter(ChestResult::forVictory),
            Codec.BOOL.optionalFieldOf("on_ground", true).forGetter(ChestResult::onGround),
            ResourceLocation.CODEC.fieldOf("loot").forGetter(ChestResult::loot)
    ).apply(instance, ChestResult::new));


    @Override
    public void apply(HTRaid raid, ServerLevel level, int tick) {
        if(tick == 0 && ((raid.isDefeated() && forVictory()) || (raid.isLost() && forLoss()))){
            BlockPos pos = onGround() ? WorldHelper.getSuitableHeightPos(level, MathHelper.toBlockPos(raid.getPosition())) : MathHelper.toBlockPos(raid.getPosition());
            if(level.isEmptyBlock(pos) || level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()){
                level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
                if(level.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity containerBlockEntity){
                    containerBlockEntity.setLootTable(LootHelper.get().createKey(loot()));
                }
            }
        }
    }

    @Override
    public void applyToDefender(HTRaid raid, Entity defender, int tick) {

    }

    @Override
    public void applyToRaider(HTRaid raid, Entity raider, int tick) {

    }

    @Override
    public ResultType<?> getType() {
        return HTLibResultTypes.CHEST;
    }
}
