package hungteen.htlib.util;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:53
 **/
public class EntityUtil {

    /**
     * get predicate items.
     */
    public static List<EntityType<?>> getFilterTypes(Predicate<EntityType<?>> predicate) {
        return ForgeRegistries.ENTITIES.getValues().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(ForgeRegistries.ENTITIES::getKey))
                .collect(Collectors.toList());
    }

}
