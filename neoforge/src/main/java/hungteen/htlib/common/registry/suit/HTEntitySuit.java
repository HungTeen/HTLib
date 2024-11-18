package hungteen.htlib.common.registry.suit;

import com.mojang.datafixers.util.Pair;
import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.util.NeoHelper;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.impl.EntityHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 实体系列管理一个实体需要注册的所有逻辑。<br>
 * 1. 注册 {@link EntityType}, {@link net.minecraft.world.item.SpawnEggItem}。<br>
 * 2. 注册实体的属性， {@link AttributeSupplier}。<br>
 * 3. 注册实体的生成规则，{@link HTPlacementData}。<br>
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/7 11:25
 */
public class HTEntitySuit<T extends Entity> implements SimpleEntry {

    private final ResourceLocation registryName;
    private final Supplier<EntityType.Builder<T>> entityTypeBuilder;
    private EntityType<T> entityType;
    private AttributeSupplier attributeSupplier;
    private Pair<Integer, Integer> colors;
    private final boolean isLiving;
    private HTPlacementData<T> placementData;

    /**
     * 通过 {@link EntitySuitBuilder} 构建实体类型。
     * @param registryName 实体类型注册名。
     * @param entityTypeBuilder 实体类型构造器。
     * @param isLiving 是否是活着的实体。
     */
    protected HTEntitySuit(ResourceLocation registryName, Supplier<EntityType.Builder<T>> entityTypeBuilder, boolean isLiving) {
        this.registryName = registryName;
        this.entityTypeBuilder = entityTypeBuilder;
        this.isLiving = isLiving;
        if (this.isLiving) {
            this.placementData = new HTPlacementData<>(SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (dataType, level, spawnType, pos, source) -> {
                return Mob.checkMobSpawnRules((EntityType<? extends Mob>) dataType, level, spawnType, pos, source);
            }, RegisterSpawnPlacementsEvent.Operation.OR);
        }
    }

    public void register(RegisterEvent event) {
        if (NeoHelper.canRegister(event, EntityHelper.get())) {
            this.entityType = entityTypeBuilder.get().build(this.registryName.toString());
            NeoHelper.register(event, EntityHelper.get(), this.registryName, () -> this.entityType);
        }
        if (NeoHelper.canRegister(event, ItemHelper.get()) && colors != null) {
            NeoHelper.register(event, ItemHelper.get(), StringHelper.suffix(this.registryName, "spawn_egg"), () -> {
                return new DeferredSpawnEggItem(() -> {
                    return (EntityType<? extends Mob>) this.entityType;
                }, colors.getFirst(), colors.getSecond(), new Item.Properties());
            });
        }
    }

    public Optional<AttributeSupplier> getAttributeSupplier() {
        if (this.attributeSupplier != null) {
            return Optional.of(attributeSupplier);
        } else if (this.isLiving) {
            HTLibAPI.logger().warn("{} has no attribute, HTLib will make one for you.", registryName);
            return Optional.of(Mob.createMobAttributes().build());
        }
        return Optional.empty();
    }

    public void addPlacement(RegisterSpawnPlacementsEvent ev) {
        if (placementData != null) {
            ev.register(entityType, placementData.placement, placementData.heightMap, placementData.predicate, placementData.operation);
        } else if (this.isLiving) {
            HTLibAPI.logger().warn("{} has no spawn placement.", registryName);
        }
    }

    public void clear() {
        this.attributeSupplier = null;
        this.colors = null;
    }

    public void setAttributeSupplier(AttributeSupplier attributeSupplier) {
        this.attributeSupplier = attributeSupplier;
    }

    /**
     * 设置生成蛋颜色，请确保该实体继承了 {@link Mob}。
     * @param background 背景颜色。
     * @param highlight 高亮颜色。
     */
    public void setEggColor(int background, int highlight) {
        this.colors = Pair.of(background, highlight);
    }

    /**
     * 取消刷怪蛋。
     */
    public void noEgg() {
        this.colors = null;
    }

    public void setHeightMap(Heightmap.Types heightMap) {
        if (this.placementData != null) {
            this.placementData.heightMap = heightMap;
        }
    }

    public void setPlacement(SpawnPlacementType placement) {
        if (this.placementData != null) {
            this.placementData.placement = placement;
        }
    }

    public void setOperation(RegisterSpawnPlacementsEvent.Operation operation) {
        if (this.placementData != null) {
            this.placementData.operation = operation;
        }
    }

    public void setPredicate(SpawnPlacements.SpawnPredicate<T> predicate) {
        if (this.placementData != null) {
            this.placementData.predicate = predicate;
        }
    }

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
    public String name() {
        return this.registryName.getPath();
    }

    @Override
    public String getModID() {
        return this.registryName.getNamespace();
    }

    static class HTPlacementData<T extends Entity> {
        Heightmap.Types heightMap;
        SpawnPlacementType placement;
        SpawnPlacements.SpawnPredicate<T> predicate;
        RegisterSpawnPlacementsEvent.Operation operation;

        public HTPlacementData(SpawnPlacementType placement, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<T> predicate, RegisterSpawnPlacementsEvent.Operation operation) {
            this.heightMap = heightMap;
            this.placement = placement;
            this.predicate = predicate;
            this.operation = operation;
        }

    }

    public static class EntitySuitBuilder<T extends Entity> {

        private final HTEntitySuit<T> suit;

        public EntitySuitBuilder(ResourceLocation registryName, Supplier<EntityType.Builder<T>> entityTypeBuilder, boolean isLiving) {
            this.suit = new HTEntitySuit<>(registryName, entityTypeBuilder, isLiving);
        }

        public EntitySuitBuilder<T> attribute(AttributeSupplier supplier){
            suit.setAttributeSupplier(supplier);
            return this;
        }

        public EntitySuitBuilder<T> spawnEgg(int background, int highlight){
            assert suit.isLiving : new IllegalStateException("You can't set spawn egg for non-living entity.");
            suit.setEggColor(background, highlight);
            return this;
        }

        public EntitySuitBuilder<T> noEgg(){
            suit.noEgg();
            return this;
        }

        public EntitySuitBuilder<T> heightMap(Heightmap.Types dataType){
            suit.setHeightMap(dataType);
            return this;
        }

        public EntitySuitBuilder<T> spawn(SpawnPlacementType dataType){
            suit.setPlacement(dataType);
            return this;
        }

        public EntitySuitBuilder<T> predicate(SpawnPlacements.SpawnPredicate<T> predicate){
            suit.setPredicate(predicate);
            return this;
        }

        public EntitySuitBuilder<T> op(RegisterSpawnPlacementsEvent.Operation operation){
            suit.setOperation(operation);
            return this;
        }

        public HTEntitySuit<T> build() {
            return suit;
        }
    }

}