package hungteen.htlib.common.impl;

import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.codec.RaidItemEntry;
import hungteen.htlib.common.codec.RaidItemSetting;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.VanillaHelper;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/4 18:33
 **/
public interface RaidItemEntries {

    HTCodecRegistry<RaidItemEntry> RAID_ITEMS = HTRegistryManager.create(HTLibHelper.prefix("raid_item"), () -> RaidItemEntry.CODEC, () -> RaidItemEntry.CODEC);

    ResourceKey<RaidItemEntry> RAID_ENVELOPE = create("raid_envelope");
    ResourceKey<RaidItemEntry> CREEPER_EGG = create("creeper_egg");

    static void register(BootstapContext<RaidItemEntry> context) {
        context.register(RAID_ENVELOPE, new RaidItemEntry(
                RaidItemSetting.builder()
                        .nameTip("test_raid_envelope")
                        .build(),
                HTDummyEntities.DEFAULT_RAID,
                HTRaidComponents.COMMON
        ));
        context.register(CREEPER_EGG, new RaidItemEntry(
                RaidItemSetting.builder()
                        .nameTip("creeper_raid")
                        .model(VanillaHelper.get().prefix("creeper_spawn_egg"))
                        .colors(List.of(ColorHelper.CREEPER_GREEN.rgb()))
                        .build(),
                HTDummyEntities.DEFAULT_RAID,
                HTRaidComponents.TEST
        ));
    }

    static ResourceKey<RaidItemEntry> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static IHTCodecRegistry<RaidItemEntry> registry() {
        return RAID_ITEMS;
    }
}
