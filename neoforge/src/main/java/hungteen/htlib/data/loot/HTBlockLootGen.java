package hungteen.htlib.data.loot;

import hungteen.htlib.common.registry.suit.HTWoodSuit;
import hungteen.htlib.util.helper.impl.VanillaHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/18 14:42
 */
public abstract class HTBlockLootGen extends BlockLootSubProvider {

    public HTBlockLootGen(Set<Item> explosionResistant, HolderLookup.Provider provider) {
        this(explosionResistant, VanillaHelper.allFeatures(), provider);
    }

    public HTBlockLootGen(Set<Item> explosionResistant, FeatureFlagSet flags, HolderLookup.Provider provider) {
        super(explosionResistant, flags, provider);
    }

    /**
     * Gen wood-related at once.
     */
    protected void woodSuitGen(HTWoodSuit suit) {
        suit.entryBlocks().forEach(pair -> {
            final Block block = pair.getValue();
            switch (pair.getKey()) {
                case SLAB -> this.add(block, this::createSlabItemTable);
                case DOOR -> this.add(block, this::createDoorTable);
            }
        });
    }

}
