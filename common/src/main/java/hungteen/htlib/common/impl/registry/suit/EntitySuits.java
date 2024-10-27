package hungteen.htlib.common.impl.registry.suit;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Entity Types, Attributes, Placements, Spawn Eggs.
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/6 15:57
 */
public class EntitySuits {

    private static final HTSimpleRegistryImpl<EntitySuit<?>> SUITS = HTRegistryManager.simple(HTLibHelper.prefix("entity_suit"));

    public static HTSimpleRegistry<EntitySuit<?>> registry() {
        return SUITS;
    }

    public static <T extends Entity> EntitySuit<T> register(EntitySuit<T> suit) {
        return registry().register(suit);
    }

    public static Collection<EntitySuit<?>> getSuits() {
        return registry().getValues();
    }

    public static <T extends Entity> Builder<T> nonLiving(ResourceLocation name, Supplier<EntityType.Builder<T>> entityTypeBuilder){
        return new Builder<>(name, entityTypeBuilder, false, false);
    }

    public static <T extends Entity> Builder<T> living(ResourceLocation name, Supplier<EntityType.Builder<T>> entityTypeBuilder){
        return new Builder<>(name, entityTypeBuilder, true, true);
    }

    public static class Builder<T extends Entity> {

        private final EntitySuit<T> suit;

        public Builder(ResourceLocation registryName, Supplier<EntityType.Builder<T>> entityTypeBuilder, boolean isLiving, boolean hasSpawnEgg) {
            this.suit = new EntitySuit<>(registryName, entityTypeBuilder, isLiving, hasSpawnEgg);
        }

        public Builder<T> attribute(AttributeSupplier supplier){
            suit.setAttributeSupplier(supplier);
            return this;
        }

        public Builder<T> color(int background, int highlight){
            suit.setEggColor(background, highlight);
            return this;
        }

        public Builder<T> noEgg(){
            suit.setSpawnEgg(false);
            return this;
        }

//        public Builder<T> heightMap(Heightmap.Types dataType){
//            suit.setHeightMap(dataType);
//            return this;
//        }
//
//        public Builder<T> spawn(SpawnPlacements.Type dataType){
//            suit.setPlacement(dataType);
//            return this;
//        }
//
//        public Builder<T> predict(SpawnPlacements.SpawnPredicate<T> predicate){
//            suit.setPredicate(predicate);
//            return this;
//        }
//
//        public Builder<T> op(SpawnPlacementRegisterEvent.Operation operation){
//            suit.setOperation(operation);
//            return this;
//        }

        public EntitySuit<T> build() {
            return suit;
        }
    }
}
