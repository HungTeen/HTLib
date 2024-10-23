package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.registry.RegistryHelper;
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
    private final RegistryHelper<T> helper;

    public HTHolderTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, RegistryHelper<T> registryHelper) {
        super(output, registryHelper.resourceKey(), provider, (obj) -> registryHelper.getResourceKey(obj).orElseThrow());
        this.helper = registryHelper;
    }

    protected void addTags(HolderLookup.Provider provider) {
    }


    public RegistryHelper<T> getHelper() {
        return this.helper;
    }
}
