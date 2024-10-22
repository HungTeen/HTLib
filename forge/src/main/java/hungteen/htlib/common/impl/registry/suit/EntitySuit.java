package hungteen.htlib.common.impl.registry.suit;

import com.mojang.datafixers.util.Pair;
import hungteen.htlib.HTLibForgeInitializer;
import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.htlib.util.helper.registry.ItemHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
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
    private PlacementData<T> placementData;

    public EntitySuit(ResourceLocation registryName, Supplier<EntityType.Builder<T>> entityTypeBuilder, boolean isLiving, boolean hasSpawnEgg) {
        this.registryName = registryName;
        this.entityTypeBuilder = entityTypeBuilder;
        this.isLiving = isLiving;
        this.hasSpawnEgg = hasSpawnEgg;
        if (this.isLiving) {
            this.placementData = new PlacementData<>(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, spawnType, pos, source) -> {
                return Mob.checkMobSpawnRules((EntityType<? extends Mob>) type, level, spawnType, pos, source);
            }, SpawnPlacementRegisterEvent.Operation.OR);
        }
    }

    public void register(RegisterEvent event) {
        if (EntityHelper.get().matchEvent(event)) {
            this.entityType = entityTypeBuilder.get().build(this.registryName.toString());
            EntityHelper.get().register(event, this.registryName, () -> this.entityType);
        }
        if (ItemHelper.get().matchEvent(event) && hasSpawnEgg && colors != null) {
            ItemHelper.get().register(event, StringHelper.suffix(this.registryName, "spawn_egg"), () -> {
                return new ForgeSpawnEggItem(() -> {
                    return (EntityType<? extends Mob>) this.entityType;
                }, colors.getFirst(), colors.getSecond(), new Item.Properties());
            });
        }
    }

    public void addAttribute(EntityAttributeCreationEvent ev) {
        if (this.attributeSupplier != null) {
            ev.put((EntityType<? extends LivingEntity>) entityType, attributeSupplier);
        } else if (this.isLiving) {
            ev.put((EntityType<? extends LivingEntity>) entityType, Mob.createMobAttributes().build());
            HTLibForgeInitializer.getLogger().warn("{} has no attribute, HTLib will make one for you.", registryName);
        }
    }

    public void addPlacement(SpawnPlacementRegisterEvent ev) {
        if (placementData != null) {
            ev.register(entityType, placementData.placement, placementData.heightMap, placementData.predicate, placementData.operation);
        } else if (this.isLiving) {
            HTLibForgeInitializer.getLogger().warn("{} has no spawn placement.", registryName);
        }
    }

    public void clear() {
        this.attributeSupplier = null;
        this.colors = null;
        this.placementData = null;
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

    public void setHeightMap(Heightmap.Types heightMap) {
        if (this.placementData != null) {
            this.placementData.heightMap = heightMap;
        }
    }

    public void setPlacement(SpawnPlacements.Type placement) {
        if (this.placementData != null) {
            this.placementData.placement = placement;
        }
    }

    public void setOperation(SpawnPlacementRegisterEvent.Operation operation) {
        if (this.placementData != null) {
            this.placementData.operation = operation;
        }
    }

    public void setPredicate(SpawnPlacements.SpawnPredicate<T> predicate) {
        if (this.placementData != null) {
            this.placementData.predicate = predicate;
        }
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

    static class PlacementData<T extends Entity> {
        Heightmap.Types heightMap;
        SpawnPlacements.Type placement;
        SpawnPlacements.SpawnPredicate<T> predicate;
        SpawnPlacementRegisterEvent.Operation operation;

        public PlacementData(SpawnPlacements.Type placement, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<T> predicate, SpawnPlacementRegisterEvent.Operation operation) {
            this.heightMap = heightMap;
            this.placement = placement;
            this.predicate = predicate;
            this.operation = operation;
        }

    }

}