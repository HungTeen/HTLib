package hungteen.htlib.common.registry.suit;

import hungteen.htlib.util.HTBoatType;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/17 22:59
 **/
public class HTBoatSetting {

    private final Item.Properties itemProperties;
    private final HTBoatType boatType;
    private boolean enabled = true;

    public HTBoatSetting(HTBoatType boatType) {
        this.itemProperties = new Item.Properties().stacksTo(16);
        this.boatType = boatType;
    }

    public void itemProperties(Consumer<Item.Properties> consumer) {
        consumer.accept(itemProperties);
    }

    public Item.Properties getItemProperties() {
        return itemProperties;
    }

    /**
     * Ban boat items.
     */
    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Nullable
    public HTBoatType getBoatType() {
        return boatType;
    }
}
