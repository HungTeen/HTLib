package hungteen.htlib.data.tag;

import hungteen.htlib.api.util.helper.HTRegistryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/27 20:41
 */
public abstract class HTHolderTagsProvider<T> extends IntrinsicHolderTagsProvider<T> {
    private final HTRegistryHelper<T> helper;

    public HTHolderTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, HTRegistryHelper<T> registryHelper) {
        super(output, registryHelper.resourceKey(), provider, (obj) -> registryHelper.getResourceKey(obj).orElseThrow());
        this.helper = registryHelper;
    }

    protected void addTags(HolderLookup.Provider provider) {
    }


    public HTRegistryHelper<T> getHelper() {
        return this.helper;
    }
}
