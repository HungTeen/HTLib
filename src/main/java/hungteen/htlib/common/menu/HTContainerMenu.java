package hungteen.htlib.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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
        addInventories(leftX, leftY, rows, columns, startId, (id, x, y) -> new Slot(inventory, id, x, y));
    }

    public void addInventories(int leftX, int leftY, int rows, int columns, int startId, ISlotSupplier supplier){
        for(int i = 0; i < rows; ++ i) {
            for(int j = 0; j < columns; ++ j) {
                this.addSlot(supplier.get(startId + j + i * columns, leftX + 18 * j, leftY + 18 * i));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotId) {
        return ItemStack.EMPTY;
    }
    
    public interface ISlotSupplier {

        Slot get(int slotId, int posX, int posY);
    }
}
