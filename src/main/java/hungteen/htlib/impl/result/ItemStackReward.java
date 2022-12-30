package hungteen.htlib.impl.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.IResultComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:43
 **/
public class ItemStackReward implements IResultComponent {

    public static final Codec<ItemStackReward> CODEC = RecordCodecBuilder.<ItemStackReward>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("for_victory", true).forGetter(ItemStackReward::forVictory),
            Codec.BOOL.optionalFieldOf("for_loss", false).forGetter(ItemStackReward::forLoss),
            ItemStack.CODEC.listOf().fieldOf("rewards").forGetter(ItemStackReward::getRewards)
    ).apply(instance, ItemStackReward::new)).codec();
    private final boolean forVictory;
    private final boolean forLoss;
    private final List<ItemStack> rewards;

    public ItemStackReward(boolean forVictory, boolean forLoss, List<ItemStack> rewards) {
        this.forVictory = forVictory;
        this.forLoss = forLoss;
        this.rewards = rewards;
    }

    @Override
    public void apply(IRaid raid, ServerLevel level, int tick) {

    }

    @Override
    public void applyToDefender(IRaid raid, Entity defender, int tick) {
        if(this.forVictory() && tick == 0 && defender instanceof Player){
            this.giveRewardTo((Player) defender);
        }
    }

    @Override
    public void applyToRaider(IRaid raid, Entity raider, int tick) {
        if(this.forLoss() && tick == 0 && raider instanceof Player){
            this.giveRewardTo((Player) raider);
        }
    }

    private void giveRewardTo(Player player){
        getRewards().forEach(itemStack -> {
            ItemStack stack = itemStack.copy();
            if (player.addItem(stack)) {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else {
                ItemEntity itementity = player.drop(stack, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setOwner(player.getUUID());
                }
            }
        });
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    @Override
    public boolean forVictory() {
        return forVictory;
    }

    @Override
    public boolean forLoss() {
        return forLoss;
    }

    @Override
    public IResultComponentType<?> getType() {
        return HTResultComponents.ITEM_STACK_TYPE;
    }
}
