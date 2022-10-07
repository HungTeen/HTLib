package hungteen.htlib.util;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class ItemUtil {

    /**
     * get predicate items.
     */
    public static List<Item> getFilterItems(Predicate<Item> predicate) {
        return ForgeRegistries.ITEMS.getValues().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(ForgeRegistries.ITEMS::getKey))
                .collect(Collectors.toList());
    }

}
