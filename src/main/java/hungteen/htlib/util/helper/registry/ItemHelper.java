package hungteen.htlib.util.helper.registry;

import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class ItemHelper extends RegistryHelper<Item> {

    private static final ItemHelper HELPER = new ItemHelper();

    public static ResourceLocation itemTexture(Item item) {
        return StringHelper.itemTexture(get().getKey(item));
    }

    public static ResourceLocation itemTexture(Item item, String suffix) {
        return StringHelper.itemTexture(get().getKey(item), suffix);
    }

    /* Common Methods */

    public static ItemHelper get(){
        return HELPER;
    }

    @Override
    public Registry<Item> getRegistry() {
        return Registry.ITEM;
    }

}
