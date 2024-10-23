package hungteen.htlib.data;

import hungteen.htlib.common.impl.position.HTLibPositionComponents;
import hungteen.htlib.common.impl.raid.HTLibRaidComponents;
import hungteen.htlib.common.impl.result.HTLibResultComponents;
import hungteen.htlib.common.impl.spawn.HTLibSpawnComponents;
import hungteen.htlib.common.impl.wave.HTLibWaveComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-25 12:44
 **/
public class HTDatapackEntriesGen extends HTRegistriesDatapackGenerator {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(HTLibPositionComponents.registry().getRegistryKey(), HTLibPositionComponents::register)
            .add(HTLibResultComponents.registry().getRegistryKey(), HTLibResultComponents::register)
            .add(HTLibSpawnComponents.registry().getRegistryKey(), HTLibSpawnComponents::register)
            .add(HTLibWaveComponents.registry().getRegistryKey(), HTLibWaveComponents::register)
            .add(HTLibRaidComponents.registry().getRegistryKey(), HTLibRaidComponents::register)
            ;

    public HTDatapackEntriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder) {
        super(output, registries, datapackEntriesBuilder);
    }

    HTDatapackEntriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        this(output, provider, BUILDER);
    }

}
