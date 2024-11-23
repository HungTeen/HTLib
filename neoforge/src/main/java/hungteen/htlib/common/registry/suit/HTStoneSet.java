package hungteen.htlib.common.registry.suit;

import hungteen.htlib.common.block.wood.HTStairBlock;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * TODO 考虑压力板什么的。
 * 石头系列管理一整套同类方块变体 {@link HTStoneVariant}，例如圆石、末地石等等。<br>
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/17 22:06
 **/
public class HTStoneSet extends HTBlockSet<HTStoneSet.HTStoneVariant> {

    private final EnumMap<HTStoneVariant, HTBlockSetting> settingMap = new EnumMap<>(HTStoneVariant.class);
    private final EnumMap<HTStoneVariant, Block> blockMap = new EnumMap<>(HTStoneVariant.class);

    public HTStoneSet(ResourceLocation registryName) {
        super(registryName);
        settingMap.put(HTStoneVariant.BLOCK, new HTBlockSetting(
                r -> r,
                Block.Properties.ofFullCopy(Blocks.STONE),
                Block::new
        ));
        settingMap.put(HTStoneVariant.POLISHED_BLOCK, new HTBlockSetting(
                r -> StringHelper.prefix(r, "polished"),
                Block.Properties.ofFullCopy(Blocks.STONE),
                Block::new
        ));
        settingMap.put(HTStoneVariant.STAIRS, new HTBlockSetting(
                r -> StringHelper.suffix(r, "stairs"),
                Block.Properties.ofFullCopy(Blocks.STONE),
                p -> new HTStairBlock(blockMap.get(HTStoneVariant.STAIRS).defaultBlockState(), p)
        ));
        settingMap.put(HTStoneVariant.POLISHED_STAIRS, new HTBlockSetting(
                r -> StringHelper.expand(r, "polished", "stairs"),
                Block.Properties.ofFullCopy(Blocks.STONE),
                p -> new HTStairBlock(blockMap.get(HTStoneVariant.STAIRS).defaultBlockState(), p)
        ));
        settingMap.put(HTStoneVariant.SLAB, new HTBlockSetting(
                r -> StringHelper.suffix(r, "slab"),
                Block.Properties.ofFullCopy(Blocks.STONE),
                SlabBlock::new
        ));
        settingMap.put(HTStoneVariant.POLISHED_SLAB, new HTBlockSetting(
                r -> StringHelper.expand(r, "polished", "slab"),
                Block.Properties.ofFullCopy(Blocks.STONE),
                SlabBlock::new
        ));
    }

    @Override
    public void register(RegisterEvent event){
        super.register(event);
    }

    @Override
    public Map<HTStoneVariant, HTBlockSetting> getSettingMap() {
        return this.settingMap;
    }

    @Override
    public Map<HTStoneVariant, Block> getBlockMap() {
        return this.blockMap;
    }

    public enum HTStoneVariant {

        BLOCK,
        
        POLISHED_BLOCK,
        
        STAIRS,
        
        POLISHED_STAIRS,
        
        SLAB,
        
        POLISHED_SLAB,
        
        ;


    }
}
