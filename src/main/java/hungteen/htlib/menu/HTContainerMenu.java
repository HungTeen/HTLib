package hungteen.htlib.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-09 10:21
 **/
public abstract class HTContainerMenu extends AbstractContainerMenu {

    public HTContainerMenu(int id, @Nullable MenuType<?> menuType) {
        super(menuType, id);
    }

    /**
     * default offset.
     */
    public void addInventoryAndHotBar(Container inventory, int leftX, int leftY) {
        this.addPlayerInventory(inventory, leftX, leftY);
        this.addPlayerHotBar(inventory, leftX, leftY + 58);
    }

    public void addPlayerInventory(Container inventory, int leftX, int leftY) {
        addInventories(inventory, leftX, leftY, 3, 9, 9);
    }

    public void addPlayerHotBar(Container inventory, int leftX, int leftY) {
        addInventories(inventory, leftX, leftY, 1, 9, 0);
    }

    public void addInventories(Container inventory, int leftX, int leftY, int rows, int columns, int startId){
        for(int i = 0; i < rows; ++ i) {
            for(int j = 0; j < columns; ++ j) {
                this.addSlot(new Slot(inventory, startId + j + i * columns, leftX + 18 * j, leftY + 18 * i));
            }
        }
    }
}
