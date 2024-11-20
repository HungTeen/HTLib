package hungteen.htlib.data.tag;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/10 17:01
 */
public abstract class HTTagsProvider<T> extends TagsProvider<T> {

    private final HTResourceHelper<T> helper;

    public HTTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, HTResourceHelper<T> helper) {
        super(output, helper.resourceKey(), provider);
        this.helper = helper;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }

    public void addTag(TagKey<T> tag, ResourceLocation... location){
        for(ResourceLocation loc : location){
            this.tag(tag).add(getHelper().createKey(loc));
        }
    }

    /**
     * Directly support helper methods.
     */
    public HTResourceHelper<T> getHelper() {
        return helper;
    }
}
