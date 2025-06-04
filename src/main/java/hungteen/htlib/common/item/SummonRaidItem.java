package hungteen.htlib.common.item;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.codec.RaidItemEntry;
import hungteen.htlib.common.codec.RaidItemSetting;
import hungteen.htlib.common.impl.RaidItemEntries;
import hungteen.htlib.common.world.raid.AbstractRaid;
import hungteen.htlib.util.helper.CodecHelper;
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

    private static final String ITEM_SETTING_TAG = "ItemSetting"; // 物品设置直接存为NBT。
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
                if(raidComponentOpt.isPresent()) {
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
        getItemSetting(stack).textComponents().forEach(s -> {
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

    public static void setItemSetting(ItemStack stack, RaidItemSetting itemSetting) {
        CodecHelper.encodeNbt(RaidItemSetting.CODEC, itemSetting)
                .resultOrPartial(msg -> HTLib.getLogger().error("ItemSetting encode error : " + msg))
                .ifPresent(tag -> {
                    stack.getOrCreateTag().put(ITEM_SETTING_TAG, tag);
                });
    }

    @NotNull
    public static RaidItemSetting getItemSetting(ItemStack stack) {
        return CodecHelper.parse(RaidItemSetting.CODEC, stack.getOrCreateTag().get(ITEM_SETTING_TAG))
                .result()
                .orElse(RaidItemSetting.DEFAULT);
    }

    /**
     * 更新组件。
     */
    public static void updateItem(ItemStack stack, ResourceKey<RaidItemEntry> resourceKey, RaidItemEntry entry) {
        setItemSetting(stack, entry.itemSetting());
        setEntryKey(stack, resourceKey);
    }

    public static ItemStack create(ResourceKey<RaidItemEntry> resourceKey, RaidItemEntry entry) {
        ItemStack stack = new ItemStack(HTItems.SUMMON_RAID_ITEM.get());
        updateItem(stack, resourceKey, entry);
        return stack;
    }

}
