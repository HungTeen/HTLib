package hungteen.htlib.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.world.entity.DummyEntityType;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import net.minecraft.resources.ResourceKey;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/4 18:33
 **/
public record RaidItemEntry(RaidItemSetting itemSetting, DummyEntityType<?> dummyEntityType,
                            ResourceKey<IRaidComponent> raidKey) {

    public static final Codec<RaidItemEntry> CODEC = RecordCodecBuilder.<RaidItemEntry>mapCodec(instance -> instance.group(
            RaidItemSetting.CODEC.fieldOf("item_setting").forGetter(RaidItemEntry::itemSetting),
            HTDummyEntities.registry().byNameCodec().fieldOf("dummy_entity_type").forGetter(RaidItemEntry::dummyEntityType),
            HTRaidComponents.registry().getKeyCodec().fieldOf("raid").forGetter(RaidItemEntry::raidKey)
    ).apply(instance, RaidItemEntry::new)).codec();

}