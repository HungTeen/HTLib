package hungteen.htlib.common.registry;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.common.registry.suit.HTWoodSet;
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

    HTSimpleRegistryImpl<HTWoodSet> WOODS = HTRegistryManager.simple(HTLibHelper.prefix("wood"));

    /**
     * Modders should call this method in their mod constructor.
     */
    static HTWoodSet register(HTWoodSet type) {
        return registry().register(type);
    }

    static Collection<HTWoodSet> getSuits() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    static HTSimpleRegistry<HTWoodSet> registry(){
        return WOODS;
    }

    static Set<Block> getSignBlocks(){
        final Set<Block> blocks = new HashSet<>();
        getSuits().forEach(wood -> {
            wood.getBlockOpt(HTWoodSet.HTWoodVariant.STANDING_SIGN).ifPresent(blocks::add);
            wood.getBlockOpt(HTWoodSet.HTWoodVariant.WALL_SIGN).ifPresent(blocks::add);
        });
        return blocks;
    }

    static Set<Block> getHangingSignBlocks(){
        final Set<Block> blocks = new HashSet<>();
        getSuits().forEach(wood -> {
            wood.getBlockOpt(HTWoodSet.HTWoodVariant.HANGING_SIGN).ifPresent(blocks::add);
            wood.getBlockOpt(HTWoodSet.HTWoodVariant.WALL_HANGING_SIGN).ifPresent(blocks::add);
        });
        return blocks;
    }

    static Optional<HTWoodSet> getWoodSuit(String name) {
        return registry().getValue(name);
    }

    static HTWoodSet.WoodSuitBuilder builder(ResourceLocation location) {
        return new HTWoodSet.WoodSuitBuilder(location);
    }

}
