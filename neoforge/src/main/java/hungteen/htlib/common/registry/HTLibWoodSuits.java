package hungteen.htlib.common.registry;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.common.registry.suit.HTWoodSuit;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.*;

/**
 * Used to manage wood-related registrations.
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/21 10:13
 */
public interface HTLibWoodSuits {

    HTSimpleRegistryImpl<HTWoodSuit> WOODS = HTRegistryManager.simple(HTLibHelper.prefix("wood"));

    /**
     * Modders should call this method in their mod constructor.
     */
    static HTWoodSuit register(HTWoodSuit type) {
        return registry().register(type);
    }

    static Collection<HTWoodSuit> getSuits() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    static HTSimpleRegistry<HTWoodSuit> registry(){
        return WOODS;
    }

    static Set<Block> getSignBlocks(){
        final Set<Block> blocks = new HashSet<>();
        getSuits().forEach(wood -> {
            wood.getBlockOpt(HTWoodSuit.HTWoodVariant.STANDING_SIGN).ifPresent(blocks::add);
            wood.getBlockOpt(HTWoodSuit.HTWoodVariant.WALL_SIGN).ifPresent(blocks::add);
        });
        return blocks;
    }

    static Set<Block> getHangingSignBlocks(){
        final Set<Block> blocks = new HashSet<>();
        getSuits().forEach(wood -> {
            wood.getBlockOpt(HTWoodSuit.HTWoodVariant.HANGING_SIGN).ifPresent(blocks::add);
            wood.getBlockOpt(HTWoodSuit.HTWoodVariant.WALL_HANGING_SIGN).ifPresent(blocks::add);
        });
        return blocks;
    }

    static Optional<HTWoodSuit> getWoodSuit(String name) {
        return registry().getValue(name);
    }

    static HTWoodSuit.WoodSuitBuilder builder(ResourceLocation location) {
        return new HTWoodSuit.WoodSuitBuilder(location);
    }

}
