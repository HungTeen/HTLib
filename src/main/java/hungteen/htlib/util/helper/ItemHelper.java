package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class ItemHelper extends RegistryHelper<Item>{

    private static final ItemHelper HELPER = new ItemHelper();

    public static ResourceLocation itemTexture(Item item) {
        return StringHelper.itemTexture(ItemHelper.getKey(item));
    }

    public static ResourceLocation itemTexture(Item item, String suffix) {
        return StringHelper.itemTexture(ItemHelper.getKey(item), suffix);
    }

    /**
     * Get predicate registry objects.
     */
    public static List<Item> getFilterItems(Predicate<Item> predicate) {
        return HELPER.getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<Item>, Item>> getItemWithKeys() {
        return HELPER.getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(Item object) {
        return HELPER.getResourceLocation(object);
    }

    @Override
    public IForgeRegistry<Item> getForgeRegistry() {
        return ForgeRegistries.ITEMS;
    }

}
