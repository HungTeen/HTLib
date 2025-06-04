package hungteen.htlib.common.item;

import hungteen.htlib.HTLib;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/4 19:26
 **/
public class HTItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HTLib.MOD_ID);

    public static final RegistryObject<Item> SUMMON_RAID_ITEM = ITEMS.register("summon_raid_item", SummonRaidItem::new);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
