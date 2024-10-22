package hungteen.htlib.common.impl.spawn;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.interfaces.raid.PositionComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.SpawnType;
import hungteen.htlib.common.impl.position.HTPositionComponents;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.helper.NBTHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
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
public interface HTSpawnComponents {

    HTCodecRegistry<ISpawnComponent> SPAWNS = HTRegistryManager.create(HTLibHelper.prefix("spawn"), HTSpawnComponents::getDirectCodec);

    ResourceKey<ISpawnComponent> CREEPER_4_8 = create("creeper_4_8");
    ResourceKey<ISpawnComponent> POWERED_CREEPER_3_5 = create("powered_creeper_3_5");
    ResourceKey<ISpawnComponent> SPIDER_5 = create("spider_5");
    ResourceKey<ISpawnComponent> LONG_TERM_SKELETON = create("long_term_skeleton");
    ResourceKey<ISpawnComponent> WITHER_SKELETON = create("wither_skeleton");
    ResourceKey<ISpawnComponent> DIAMOND_ZOMBIE_3_6 = create("diamond_zombie_3_6");

    static void register(BootstapContext<ISpawnComponent> context) {
        final HolderGetter<PositionComponent> positions = HTPositionComponents.registry().helper().lookup(context);
        context.register(CREEPER_4_8, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.CREEPER).build(),
                UniformInt.of(4, 8)
        ));
        context.register(POWERED_CREEPER_3_5, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.CREEPER)
                        .nbt(NBTHelper.creeperTag(true, 3, 50))
                        .build(),
                UniformInt.of(3, 5)
        ));
        context.register(SPIDER_5, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.SPIDER).build(),
                ConstantInt.of(5)
        ));
        context.register(LONG_TERM_SKELETON, new DurationSpawn(
                HTSpawnComponents.builder().entityType(EntityType.SKELETON).build(),
                400,
                100,
                1,
                0
        ));
        context.register(WITHER_SKELETON, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.WITHER_SKELETON).placement(positions.getOrThrow(HTPositionComponents.TEST))
                        .nbt(NBTHelper.merge(
                                NBTHelper.attributeTags(List.of(Pair.of(Attributes.MAX_HEALTH, 50D))),
                                NBTHelper.healthTag(50)
                        ))
                        .build(),
                ConstantInt.of(1)
        ));
        context.register(DIAMOND_ZOMBIE_3_6, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.ZOMBIE)
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

    static Codec<ISpawnComponent> getDirectCodec() {
        return HTSpawnTypes.registry().byNameCodec().dispatch(ISpawnComponent::getType, SpawnType::codec);
    }

    static Codec<Holder<ISpawnComponent>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<Pair<Integer, ISpawnComponent>> pairDirectCodec() {
        return Codec.mapPair(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0),
                getDirectCodec().fieldOf("spawn")
        ).codec();
    }

    static Codec<Pair<IntProvider, Holder<ISpawnComponent>>> pairCodec() {
        return Codec.mapPair(
                IntProvider.codec(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", ConstantInt.of(0)),
                getCodec().fieldOf("spawn")
        ).codec();
    }

    static ResourceKey<ISpawnComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static HTCodecRegistry<ISpawnComponent> registry() {
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

        public SpawnComponent.SpawnSetting build() {
            return new SpawnComponent.SpawnSetting(entityType, nbt, enableDefaultSpawn, persist, Optional.ofNullable(placeComponent));
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
