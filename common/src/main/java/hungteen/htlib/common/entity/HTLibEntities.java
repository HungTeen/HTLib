package hungteen.htlib.common.entity;

import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:07
 **/
public interface HTLibEntities {

    HTVanillaRegistry<EntityType<?>> ENTITY_TYPES = HTRegistryManager.vanilla(Registries.ENTITY_TYPE, HTLibHelper.get().getModID());

    Supplier<EntityType<HTBoat>> BOAT = ENTITY_TYPES.register("boat", () -> {
        return EntityType.Builder.<HTBoat>of(HTBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(HTLibHelper.prefix("boat").toString());
    });

    Supplier<EntityType<HTChestBoat>> CHEST_BOAT = ENTITY_TYPES.register("chest_boat", () -> {
        return EntityType.Builder.<HTChestBoat>of(HTChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(HTLibHelper.prefix("chest_boat").toString());
    });

    Supplier<EntityType<SeatEntity>> SEAT = ENTITY_TYPES.register("seat", () -> {
        return EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0F, 0F).clientTrackingRange(10).build(HTLibHelper.prefix("seat").toString());
    });

    static HTVanillaRegistry<EntityType<?>> registry() {
        return ENTITY_TYPES;
    }

}
