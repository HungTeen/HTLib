package hungteen.htlib.data.tag;

import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/10 17:01
 */
public abstract class HTTagsProvider<T> extends TagsProvider<T> {

    private final IHTResourceHelper<T> helper;

    public HTTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, IHTResourceHelper<T> helper, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, helper.resourceKey(), provider, modId, existingFileHelper);
        this.helper = helper;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }

    /**
     * Directly support helper methods.
     */
    public IHTResourceHelper<T> getHelper() {
        return helper;
    }
}
