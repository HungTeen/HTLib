package hungteen.htlib.common.item;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.codec.RaidItemEntry;
import hungteen.htlib.common.codec.RaidItemSetting;
import hungteen.htlib.common.impl.RaidItemEntries;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.util.helper.PlayerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/4 18:22
 **/
public class SummonRaidItem extends HTItem {

    private static final String ENTRY_KEY_TAG = "EntryKey";

    /**
     * 打开信息界面。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        return super.use(level, player, hand);
    }

    /**
     * 触发一次袭击。
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel serverLevel) {
            Optional<RaidItemEntry> entryOpt = get(serverLevel, context.getItemInHand());
            if (entryOpt.isPresent()) {
                Optional<RaidItemEntry> raidComponentOpt = get(serverLevel, context.getItemInHand());
                if (raidComponentOpt.isPresent()) {
                    Vec3 summonPos = context.getClickLocation().add(0, 1, 0);
                    AbstractRaid.summonRaid(serverLevel, entryOpt.get().dummyEntityType().getLocation(), raidComponentOpt.get().raidKey(), summonPos);
                    if (context.getPlayer() != null) {
                        PlayerHelper.setCooldown(context.getPlayer(), context.getItemInHand().getItem(), 20);
                        if (!context.getPlayer().isCreative()) {
                            context.getItemInHand().shrink(1);
                        }
                    }
                    return InteractionResult.CONSUME;
                }
            }
        } else {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        // 前置描述。
        getItemSetting(Optional.ofNullable(level), stack).textComponents().forEach(s -> {
            components.add(Component.translatable(s));
        });
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return getItemSetting(itemStack).name().orElse(super.getDescriptionId(itemStack));
    }

    public static Optional<RaidItemEntry> get(Level level, ItemStack stack) {
        return RaidItemEntries.registry().getOptValue(level, getEntryKey(stack));
    }

    public static void setEntryKey(ItemStack stack, ResourceKey<RaidItemEntry> resourceKey) {
        stack.getOrCreateTag().putString(ENTRY_KEY_TAG, resourceKey.location().toString());
    }

    public static ResourceKey<RaidItemEntry> getEntryKey(ItemStack stack) {
        return RaidItemEntries.registry().createKey(new ResourceLocation(stack.getOrCreateTag().getString(ENTRY_KEY_TAG)));
    }

    @NotNull
    public static RaidItemSetting getItemSetting(ItemStack stack) {
        return getItemSetting(HTLib.PROXY.getLevel(), stack);
    }

    @NotNull
    public static RaidItemSetting getItemSetting(Optional<Level> level, ItemStack stack) {
        if(level.isPresent()) {
            Optional<RaidItemEntry> raidItemEntry = get(level.get(), stack);
            if (raidItemEntry.isPresent()) {
                RaidItemSetting itemSetting = raidItemEntry.get().itemSetting();
                if (itemSetting != null) {
                    return itemSetting;
                }
            }
        }
        return RaidItemSetting.DEFAULT;
    }

    public static ItemStack create(ResourceKey<RaidItemEntry> resourceKey) {
        ItemStack stack = new ItemStack(HTItems.SUMMON_RAID_ITEM.get());
        setEntryKey(stack, resourceKey);
        return stack;
    }

}
