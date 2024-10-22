package hungteen.htlib.data.loot;

import hungteen.htlib.common.impl.registry.suit.TreeSuits;
import hungteen.htlib.util.helper.CompatHelper;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/18 14:42
 */
public abstract class HTBlockLootGen extends BlockLootSubProvider {

    public HTBlockLootGen(Set<Item> explosionResistant) {
        super(explosionResistant, CompatHelper.allFeatures());
    }

    /**
     * Gen wood-related at once.
     */
    protected void woodIntegration(TreeSuits.TreeSuit woodIntegration) {
        woodIntegration.getWoodBlocks().forEach(pair -> {
            final Block block = pair.getValue();
            switch (pair.getKey()) {
                case SLAB -> this.add(block, this::createSlabItemTable);
                case DOOR -> this.add(block, this::createDoorTable);
            }
        });
    }

}
