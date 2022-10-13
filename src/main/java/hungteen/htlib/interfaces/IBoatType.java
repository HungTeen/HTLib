package hungteen.htlib.interfaces;

import hungteen.htlib.entity.HTBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 22:41
 *
 * Copy from {@link net.minecraft.world.entity.vehicle.Boat.Type}
 **/
public interface IBoatType extends INameEntry {

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
    };

    Block getPlanks();

    /**
     * {@link HTBoat#getDropItem()}
     */
    Item getDropItem();

}
