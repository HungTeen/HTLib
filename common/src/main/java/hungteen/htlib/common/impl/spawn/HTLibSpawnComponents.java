package hungteen.htlib.common.impl.spawn;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.raid.SpawnComponent;
import hungteen.htlib.api.raid.PositionComponent;
import hungteen.htlib.api.raid.SpawnType;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.common.impl.position.HTLibPositionComponents;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import hungteen.htlib.util.helper.NBTHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:57
 **/
public interface HTLibSpawnComponents {

    HTCodecRegistry<SpawnComponent> SPAWNS = HTRegistryManager.codec(HTLibHelper.prefix("spawn"), HTLibSpawnComponents::getDirectCodec);

    ResourceKey<SpawnComponent> CREEPER_4_8 = create("creeper_4_8");
    ResourceKey<SpawnComponent> POWERED_CREEPER_3_5 = create("powered_creeper_3_5");
    ResourceKey<SpawnComponent> SPIDER_5 = create("spider_5");
    ResourceKey<SpawnComponent> LONG_TERM_SKELETON = create("long_term_skeleton");
    ResourceKey<SpawnComponent> WITHER_SKELETON = create("wither_skeleton");
    ResourceKey<SpawnComponent> DIAMOND_ZOMBIE_3_6 = create("diamond_zombie_3_6");

    static void register(BootstrapContext<SpawnComponent> context) {
        final HolderGetter<PositionComponent> positions = HTLibPositionComponents.registry().helper().lookup(context);
        context.register(CREEPER_4_8, new OnceSpawn(
                HTLibSpawnComponents.builder().entityType(EntityType.CREEPER).build(),
                UniformInt.of(4, 8)
        ));
        context.register(POWERED_CREEPER_3_5, new OnceSpawn(
                HTLibSpawnComponents.builder().entityType(EntityType.CREEPER)
                        .nbt(NBTHelper.creeperTag(true, 3, 50))
                        .build(),
                UniformInt.of(3, 5)
        ));
        context.register(SPIDER_5, new OnceSpawn(
                HTLibSpawnComponents.builder().entityType(EntityType.SPIDER).build(),
                ConstantInt.of(5)
        ));
        context.register(LONG_TERM_SKELETON, new DurationSpawn(
                HTLibSpawnComponents.builder().entityType(EntityType.SKELETON).build(),
                400,
                100,
                1,
                0
        ));
        context.register(WITHER_SKELETON, new OnceSpawn(
                HTLibSpawnComponents.builder().entityType(EntityType.WITHER_SKELETON).placement(positions.getOrThrow(HTLibPositionComponents.TEST))
                        .nbt(NBTHelper.merge(
                                NBTHelper.attributeTags(List.of(Pair.of(Attributes.MAX_HEALTH.value(), 50D))),
                                NBTHelper.healthTag(50)
                        ))
                        .build(),
                ConstantInt.of(1)
        ));
        context.register(DIAMOND_ZOMBIE_3_6, new OnceSpawn(
                HTLibSpawnComponents.builder().entityType(EntityType.ZOMBIE)
                        .nbt(NBTHelper.armorTag(List.of(
                                new ItemStack(Items.DIAMOND_BOOTS),
                                new ItemStack(Items.DIAMOND_LEGGINGS),
                                new ItemStack(Items.DIAMOND_CHESTPLATE),
                                new ItemStack(Items.DIAMOND_HELMET)
                        )))
                        .build(),
                UniformInt.of(3, 6)
        ));
    }

    static Codec<SpawnComponent> getDirectCodec() {
        return HTLibSpawnTypes.registry().byNameCodec().dispatch(SpawnComponent::getType, SpawnType::codec);
    }

    static Codec<Holder<SpawnComponent>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<Pair<Integer, SpawnComponent>> pairDirectCodec() {
        return Codec.mapPair(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0),
                getDirectCodec().fieldOf("spawn")
        ).codec();
    }

    static Codec<Pair<IntProvider, Holder<SpawnComponent>>> pairCodec() {
        return Codec.mapPair(
                IntProvider.codec(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", ConstantInt.of(0)),
                getCodec().fieldOf("spawn")
        ).codec();
    }

    static ResourceKey<SpawnComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<SpawnComponent> registry() {
        return SPAWNS;
    }

    static SpawnSettingBuilder builder() {
        return new SpawnSettingBuilder();
    }


    class SpawnSettingBuilder {

        private EntityType<?> entityType = EntityType.PIG;
        private CompoundTag nbt = new CompoundTag();
        private boolean enableDefaultSpawn = true;
        private boolean persist = true;
        private Holder<PositionComponent> placeComponent = null;

        public SpawnComponentImpl.SpawnSetting build() {
            return new SpawnComponentImpl.SpawnSetting(entityType, nbt, enableDefaultSpawn, persist, Optional.ofNullable(placeComponent));
        }

        public SpawnSettingBuilder entityType(EntityType<?> type) {
            this.entityType = type;
            return this;
        }

        public SpawnSettingBuilder nbt(CompoundTag tag) {
            this.nbt = tag;
            return this;
        }

        public SpawnSettingBuilder enableDefaultSpawn(boolean flag) {
            this.enableDefaultSpawn = flag;
            return this;
        }

        public SpawnSettingBuilder persist(boolean flag) {
            this.persist = flag;
            return this;
        }

        public SpawnSettingBuilder placement(Holder<PositionComponent> placeComponent) {
            this.placeComponent = placeComponent;
            return this;
        }
    }
}
