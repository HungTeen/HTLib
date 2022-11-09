package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.BlockHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:46
 **/
public class HTBlockTagGen extends BlockTagsProvider {

    public HTBlockTagGen(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, modId, existingFileHelper);
    }

    protected List<Block> getFilterItems(Predicate<Block> predicate) {
        return BlockHelper.getFilterBlocks(predicate);
    }

    @Override
    public String getName() {
        return this.modId + " block tags";
    }

}
