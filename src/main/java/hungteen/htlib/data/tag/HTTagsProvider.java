package hungteen.htlib.data.tag;

import hungteen.htlib.util.helper.registry.RegistryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/10 17:01
 */
public abstract class HTTagsProvider<T> extends TagsProvider<T> {

    private final RegistryHelper<T> helper;

    public HTTagsProvider(PackOutput output, RegistryHelper<T> helper, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, helper.resourceKey(), provider, modId, existingFileHelper);
        this.helper = helper;
    }

    public ResourceLocation res(T object){
        return getHelper().getKey(object);
    }

    public Optional<ResourceKey<T>> key(T object){
        return getHelper().getResourceKey(object);
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
