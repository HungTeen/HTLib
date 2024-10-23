package hungteen.htlib.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/18 14:33
 */
public abstract class HTLootTableGen extends LootTableProvider {
    public HTLootTableGen(PackOutput output, Set<ResourceKey<LootTable>> set, List<SubProviderEntry> entries, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, set, entries, provider);
    }

}
