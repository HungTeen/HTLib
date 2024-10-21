package hungteen.htlib.data;

import hungteen.htlib.common.impl.position.HTPositionComponents;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.VanillaHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-25 12:44
 **/
public class HTDatapackEntriesGen extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(HTPositionComponents.registry().getRegistryKey(), HTPositionComponents::register)
            .add(HTResultComponents.registry().getRegistryKey(), HTResultComponents::register)
            .add(HTSpawnComponents.registry().getRegistryKey(), HTSpawnComponents::register)
            .add(HTWaveComponents.registry().getRegistryKey(), HTWaveComponents::register)
            .add(HTRaidComponents.registry().getRegistryKey(), HTRaidComponents::register)
            ;

    private final String modId;

    public HTDatapackEntriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder, String modId, Set<String> modIds) {
        super(output, registries, datapackEntriesBuilder, modIds);
        this.modId = modId;
    }

    HTDatapackEntriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        this(output, provider, BUILDER, HTLibHelper.get().getModID(), Set.of(VanillaHelper.get().getModID(), HTLibHelper.get().getModID()));
    }

    @Override
    public String getName() {
        return this.modId + " datapack entries";
    }
}
