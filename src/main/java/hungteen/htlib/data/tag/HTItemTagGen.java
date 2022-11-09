package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.ItemHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:45
 **/
public class HTItemTagGen extends ItemTagsProvider {

    public HTItemTagGen(DataGenerator generator, BlockTagsProvider blockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, modId, existingFileHelper);
    }

    protected List<Item> getFilterItems(Predicate<Item> predicate) {
        return ItemHelper.getFilterItems(predicate);
    }

    @Override
    public String getName() {
        return this.modId + " item tags";
    }

}
