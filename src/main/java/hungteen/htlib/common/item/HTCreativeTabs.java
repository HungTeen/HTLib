package hungteen.htlib.common.item;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.codec.RaidItemEntry;
import hungteen.htlib.common.impl.RaidItemEntries;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.VanillaHelper;
import hungteen.htlib.util.helper.registry.ItemHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/4 19:56
 **/
public interface HTCreativeTabs {

    DeferredRegister<CreativeModeTab> TABS = ItemHelper.tab().createRegister(HTLib.id());

    RegistryObject<CreativeModeTab> RAID_ITEMS = register("raid_items", builder ->
            builder.icon(() -> new ItemStack((HTItems.SUMMON_RAID_ITEM.get())))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .displayItems((parameters, output) -> {
                        HolderLookup.RegistryLookup<RaidItemEntry> summonEntries = parameters.holders().lookupOrThrow(RaidItemEntries.registry().getRegistryKey());
                        summonEntries.listElementIds().map(key -> {
                            return SummonRaidItem.create(key);
                        }).forEach(output::accept);
                    }).withSearchBar().withBackgroundLocation(VanillaHelper.get().containerTexture("creative_inventory/tab_item_search"))
    );

    static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

    private static RegistryObject<CreativeModeTab> register(String name, Consumer<CreativeModeTab.Builder> consumer) {
        return TABS.register(name, () -> {
            final CreativeModeTab.Builder builder = CreativeModeTab.builder().title(Component.translatable(StringHelper.langKey("itemGroup", HTLib.id(), name)));
            consumer.accept(builder);
            return builder.build();
        });
    }

}
