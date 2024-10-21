package hungteen.htlib.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/4/18 14:33
 */
public abstract class HTLootTableGen extends LootTableProvider {
    public HTLootTableGen(PackOutput output, Set<ResourceLocation> set, List<SubProviderEntry> entries) {
        super(output, set, entries);
    }

}
