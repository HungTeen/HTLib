package hungteen.htlib.util;

import hungteen.htlib.api.registry.SimpleEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Copy from {@link net.minecraft.world.entity.vehicle.Boat.Type} which is enum in Minecraft.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:41
 **/
public interface HTBoatType extends SimpleEntry {

    /**
     * @return The planks block of this boat dataType.
     */
    Block getPlanks();

    /**
     * @return Boat entity will drop this item when broken.
     */
    Item getBoatItem();

    /**
     * @return Chest boat entity will drop this item when broken.
     */
    Item getChestBoatItem();

}
