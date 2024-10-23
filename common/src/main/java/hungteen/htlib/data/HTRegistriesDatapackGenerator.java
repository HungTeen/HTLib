package hungteen.htlib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;

import java.util.concurrent.CompletableFuture;

/**
 * Copy from forge implementation.
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/23 16:13
 **/
public class HTRegistriesDatapackGenerator extends RegistriesDatapackGenerator {
    private final CompletableFuture<HolderLookup.Provider> fullRegistries;

    public HTRegistriesDatapackGenerator(PackOutput output, CompletableFuture<RegistrySetBuilder.PatchedRegistries> registries) {
        super(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches));
        this.fullRegistries = registries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    public HTRegistriesDatapackGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder registryBuilder) {
        this(output, RegistryPatchGenerator.createLookup(registries, registryBuilder));
    }

    public CompletableFuture<HolderLookup.Provider> getFullRegistries() {
        return this.fullRegistries;
    }

}
