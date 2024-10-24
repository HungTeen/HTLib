package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/24 12:30
 **/
public interface LootHelper extends HTResourceHelper<LootTable> {

    HTResourceHelper<LootTable> HELPER = () -> Registries.LOOT_TABLE;

    /* Common Methods */

    static HTResourceHelper<LootTable> get(){
        return HELPER;
    }
}
