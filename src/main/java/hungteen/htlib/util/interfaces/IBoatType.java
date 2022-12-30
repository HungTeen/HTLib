package hungteen.htlib.util.interfaces;

import hungteen.htlib.api.interfaces.ISimpleEntry;
import hungteen.htlib.common.entity.HTBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:41
 *
 * Copy from {@link net.minecraft.world.entity.vehicle.Boat.Type}
 **/
public interface IBoatType extends ISimpleEntry {

    IBoatType DEFAULT = new IBoatType(){
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

    Block getPlanks();

    /**
     * {@link HTBoat#getDropItem()}
     */
    Item getBoatItem();

    /**
     * {@link hungteen.htlib.common.entity.HTChestBoat#getDropItem()}
     */
    Item getChestBoatItem();

}
