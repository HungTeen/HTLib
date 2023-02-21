package hungteen.htlib.common.menu.container;

import net.minecraft.world.SimpleContainer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-14 13:04
 **/
public class SimpleCraftingContainer extends SimpleContainer {

    private final int rows;
    private final int columns;


    public SimpleCraftingContainer(int rows, int columns) {
        super(rows * columns);
        this.rows = rows;
        this.columns = columns;
    }

    public int getWidth(){
        return this.columns;
    }

    public int getHeight(){
        return this.rows;
    }
}
