package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.registry.RegistryHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/10 17:01
 */
public abstract class HTTagsProvider<T> extends TagsProvider<T> {

    private final RegistryHelper<T> helper;

    public HTTagsProvider(DataGenerator generator, RegistryHelper<T> helper, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, helper.getRegistry(), modId, existingFileHelper);
        this.helper = helper;
    }

    @Override
    public String getName() {
        return this.modId + " " + getHelper().resourceKey().location() + " tags";
    }

    /**
     * Directly support helper methods.
     */
    public RegistryHelper<T> getHelper() {
        return helper;
    }
}
