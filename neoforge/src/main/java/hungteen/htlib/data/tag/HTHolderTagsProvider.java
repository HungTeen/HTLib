package hungteen.htlib.data.tag;

import hungteen.htlib.api.util.helper.HTRegistryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/27 20:41
 */
public abstract class HTHolderTagsProvider<T> extends IntrinsicHolderTagsProvider<T> {
    private final HTRegistryHelper<T> helper;

    public HTHolderTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, HTRegistryHelper<T> registryHelper, String modId, ExistingFileHelper fileHelper) {
        super(output, registryHelper.resourceKey(), provider, (obj) -> registryHelper.getResourceKey(obj).orElseThrow(), modId, fileHelper);
        this.helper = registryHelper;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }

    public void addTag(TagKey<T> tag, ResourceLocation... location){
        for(ResourceLocation loc : location){
            this.tag(tag).add(getHelper().createKey(loc));
        }
    }

    public HTRegistryHelper<T> getHelper() {
        return this.helper;
    }
}
