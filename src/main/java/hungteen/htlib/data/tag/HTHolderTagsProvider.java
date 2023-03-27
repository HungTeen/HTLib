package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.registry.RegistryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/27 20:41
 */
public abstract class HTHolderTagsProvider<T> extends IntrinsicHolderTagsProvider<T> {
    private final RegistryHelper<T> helper;

    public HTHolderTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, RegistryHelper<T> registryHelper, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registryHelper.resourceKey(), provider, (obj) -> registryHelper.getResourceKey(obj).orElseThrow(), modId, existingFileHelper);
        this.helper = registryHelper;
    }

    protected void addTags(HolderLookup.Provider provider) {
    }

    public String getName() {
        return this.modId + " " + this.getHelper().resourceKey().location() + " tags";
    }

    public RegistryHelper<T> getHelper() {
        return this.helper;
    }
}
