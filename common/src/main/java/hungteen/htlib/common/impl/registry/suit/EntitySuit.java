package hungteen.htlib.common.impl.registry.suit;

import com.mojang.datafixers.util.Pair;
import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.registry.SimpleEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * TODO Placement & EntitySuit
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/7 11:25
 */
public class EntitySuit<T extends Entity> implements SimpleEntry {

    private final ResourceLocation registryName;
    private final Supplier<EntityType.Builder<T>> entityTypeBuilder;
    private EntityType<T> entityType;
    private AttributeSupplier attributeSupplier;
    private Pair<Integer, Integer> colors;
    private boolean isLiving;
    private boolean hasSpawnEgg;

    public EntitySuit(ResourceLocation registryName, Supplier<EntityType.Builder<T>> entityTypeBuilder, boolean isLiving, boolean hasSpawnEgg) {
        this.registryName = registryName;
        this.entityTypeBuilder = entityTypeBuilder;
        this.isLiving = isLiving;
        this.hasSpawnEgg = hasSpawnEgg;
//        if (this.isLiving) {
//            this.placementData = new PlacementData<>(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (dataType, level, spawnType, pos, source) -> {
//                return Mob.checkMobSpawnRules((EntityType<? extends Mob>) dataType, level, spawnType, pos, source);
//            }, SpawnPlacementRegisterEvent.Operation.OR);
//        }
    }

//    public void register(RegisterEvent event) {
//        if (EntityHelper.get().matchEvent(event)) {
//            this.entityType = entityTypeBuilder.get().build(this.registryName.toString());
//            EntityHelper.get().register(event, this.registryName, () -> this.entityType);
//        }
//        if (ItemHelper.get().matchEvent(event) && hasSpawnEgg && colors != null) {
//            ItemHelper.get().register(event, StringHelper.suffix(this.registryName, "spawn_egg"), () -> {
//                return new ForgeSpawnEggItem(() -> {
//                    return (EntityType<? extends Mob>) this.entityType;
//                }, colors.getFirst(), colors.getSecond(), new Item.Properties());
//            });
//        }
//    }

    public Optional<AttributeSupplier> getAttributeSupplier() {
        if (this.attributeSupplier != null) {
            return Optional.of(attributeSupplier);
        } else if (this.isLiving) {
            HTLibAPI.logger().warn("{} has no attribute, HTLib will make one for you.", registryName);
            return Optional.of(Mob.createMobAttributes().build());
        }
        return Optional.empty();
    }

//    public void addPlacement(SpawnPlacementRegisterEvent ev) {
//        if (placementData != null) {
//            ev.register(entityType, placementData.placement, placementData.heightMap, placementData.predicate, placementData.operation);
//        } else if (this.isLiving) {
//            HTLibAPI.logger().warn("{} has no spawn placement.", registryName);
//        }
//    }

    public void clear() {
        this.attributeSupplier = null;
        this.colors = null;
    }

    public void setAttributeSupplier(AttributeSupplier attributeSupplier) {
        this.attributeSupplier = attributeSupplier;
    }

    public void setEggColor(int background, int highlight) {
        this.colors = Pair.of(background, highlight);
    }

    public void setSpawnEgg(boolean has) {
        this.hasSpawnEgg = has;
    }

//    public void setHeightMap(Heightmap.Types heightMap) {
//        if (this.placementData != null) {
//            this.placementData.heightMap = heightMap;
//        }
//    }
//
//    public void setPlacement(SpawnPlacements.Type placement) {
//        if (this.placementData != null) {
//            this.placementData.placement = placement;
//        }
//    }
//
//    public void setOperation(SpawnPlacementRegisterEvent.Operation operation) {
//        if (this.placementData != null) {
//            this.placementData.operation = operation;
//        }
//    }
//
//    public void setPredicate(SpawnPlacements.SpawnPredicate<T> predicate) {
//        if (this.placementData != null) {
//            this.placementData.predicate = predicate;
//        }
//    }

    public EntityType<T> getEntityType() {
        return entityType;
    }

    @NotNull
    public EntityType<T> get() {
        if (entityType == null)
            throw new IllegalStateException("You are not allowed to call this method before entity registration !");
        return entityType;
    }

    @Override
    public String getName() {
        return this.registryName.getPath();
    }

    @Override
    public String getModID() {
        return this.registryName.getNamespace();
    }

//    static class PlacementData<T extends Entity> {
//        Heightmap.Types heightMap;
//        SpawnPlacements.Type placement;
//        SpawnPlacements.SpawnPredicate<T> predicate;
//        SpawnPlacementRegisterEvent.Operation operation;
//
//        public PlacementData(SpawnPlacements.Type placement, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<T> predicate, SpawnPlacementRegisterEvent.Operation operation) {
//            this.heightMap = heightMap;
//            this.placement = placement;
//            this.predicate = predicate;
//            this.operation = operation;
//        }
//
//    }

//    public record PlacementData<T extends Entity>(SpawnPlacementType placementType, @Nullable Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation){
//
//    }

}