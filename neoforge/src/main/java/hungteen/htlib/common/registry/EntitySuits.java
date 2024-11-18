package hungteen.htlib.common.registry;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.common.registry.suit.HTEntitySuit;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Entity Types, Attributes, Placements, Spawn Eggs.
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/6 15:57
 */
public class EntitySuits {

    private static final HTSimpleRegistryImpl<HTEntitySuit<?>> SUITS = HTRegistryManager.simple(HTLibHelper.prefix("entity_suit"));

    public static HTSimpleRegistry<HTEntitySuit<?>> registry() {
        return SUITS;
    }

    public static <T extends Entity> HTEntitySuit<T> register(HTEntitySuit<T> suit) {
        return registry().register(suit);
    }

    public static Collection<HTEntitySuit<?>> getSuits() {
        return registry().getValues();
    }

    public static <T extends Entity> HTEntitySuit.EntitySuitBuilder<T> nonLiving(ResourceLocation name, Supplier<EntityType.Builder<T>> entityTypeBuilder){
        return new HTEntitySuit.EntitySuitBuilder<>(name, entityTypeBuilder, false);
    }

    public static <T extends Entity> HTEntitySuit.EntitySuitBuilder<T> living(ResourceLocation name, Supplier<EntityType.Builder<T>> entityTypeBuilder){
        return new HTEntitySuit.EntitySuitBuilder<>(name, entityTypeBuilder, true);
    }

}
