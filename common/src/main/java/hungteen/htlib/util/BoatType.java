package hungteen.htlib.util;

import hungteen.htlib.api.registry.SimpleEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Copy from {@link net.minecraft.world.entity.vehicle.Boat.Type} which is enum in Minecraft.
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:41
 **/
public interface BoatType extends SimpleEntry {

    BoatType DEFAULT = new BoatType() {
        @Override
        public String getName() {
            return "oak";
        }

        @Override
        public String getModID() {
            return "minecraft";
        }

        @Override
        public Block getPlanks() {
            return Blocks.OAK_PLANKS;
        }

        @Override
        public Item getBoatItem() {
            return Items.OAK_BOAT;
        }

        @Override
        public Item getChestBoatItem() {
            return Items.OAK_CHEST_BOAT;
        }
    };

    /**
     * @return The planks block of this boat type.
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
