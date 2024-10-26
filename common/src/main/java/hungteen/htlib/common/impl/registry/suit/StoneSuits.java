package hungteen.htlib.common.impl.registry.suit;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.block.wood.HTStairBlock;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 15:05
 */
public class StoneSuits {

    private static final HTSimpleRegistryImpl<StoneSuit> SUITS = HTRegistryManager.createSimple(HTLibHelper.prefix("stone_suit"));

    public static HTSimpleRegistry<StoneSuit> registry(){
        return SUITS;
    }

    public static class StoneSuit extends BlockSuit<HTStoneTypes> {

        private final EnumMap<HTStoneTypes, HTBlockSetting> stoneSettings = new EnumMap<>(HTStoneTypes.class);
        private final EnumMap<HTStoneTypes, Block> stoneBlocks = new EnumMap<>(HTStoneTypes.class);

        public StoneSuit(ResourceLocation registryName) {
            super(registryName);
            stoneSettings.put(HTStoneTypes.BLOCK, new HTBlockSetting(
                    r -> r,
                    Block.Properties.ofFullCopy(Blocks.STONE),
                    Block::new
            ));
            stoneSettings.put(HTStoneTypes.POLISHED_BLOCK, new HTBlockSetting(
                    r -> StringHelper.prefix(r, "polished"),
                    Block.Properties.ofFullCopy(Blocks.STONE),
                    Block::new
            ));
            stoneSettings.put(HTStoneTypes.STAIRS, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "stairs"),
                    Block.Properties.ofFullCopy(Blocks.STONE),
                    p -> new HTStairBlock(stoneBlocks.get(HTStoneTypes.STAIRS).defaultBlockState(), p)
            ));
            stoneSettings.put(HTStoneTypes.POLISHED_STAIRS, new HTBlockSetting(
                    r -> StringHelper.expand(r, "polished", "stairs"),
                    Block.Properties.ofFullCopy(Blocks.STONE),
                    p -> new HTStairBlock(stoneBlocks.get(HTStoneTypes.STAIRS).defaultBlockState(), p)
            ));
            stoneSettings.put(HTStoneTypes.SLAB, new HTBlockSetting(
                    r -> StringHelper.suffix(r, "slab"),
                    Block.Properties.ofFullCopy(Blocks.STONE),
                    SlabBlock::new
            ));
            stoneSettings.put(HTStoneTypes.POLISHED_SLAB, new HTBlockSetting(
                    r -> StringHelper.expand(r, "polished", "slab"),
                    Block.Properties.ofFullCopy(Blocks.STONE),
                    SlabBlock::new
            ));
        }

        @Override
        public Map<HTStoneTypes, HTBlockSetting> getSettingMap() {
            return this.stoneSettings;
        }

        @Override
        public Map<HTStoneTypes, Block> getBlockMap() {
            return this.stoneBlocks;
        }

    }

    public enum HTStoneTypes {

        BLOCK,
        POLISHED_BLOCK,
        STAIRS,
        POLISHED_STAIRS,
        SLAB,
        POLISHED_SLAB,
        ;


    }
}
