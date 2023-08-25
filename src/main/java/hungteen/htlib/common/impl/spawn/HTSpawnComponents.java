package hungteen.htlib.common.impl.spawn;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.api.interfaces.raid.ISpawnType;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:57
 **/
public interface HTSpawnComponents {

    HTCodecRegistry<ISpawnComponent> SPAWNS = HTRegistryManager.create(HTLibHelper.prefix("spawn"), HTSpawnComponents::getDirectCodec);

    ResourceKey<ISpawnComponent> TEST_1 = create("test_1");
    ResourceKey<ISpawnComponent> TEST_2 = create("test_2");
    ResourceKey<ISpawnComponent> TEST_3 = create("test_3");

    static void register(BootstapContext<ISpawnComponent> context) {
        context.register(TEST_1, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.CREEPER).build(),
                10
        ));
        context.register(TEST_2, new OnceSpawn(
                HTSpawnComponents.builder().entityType(EntityType.SPIDER).build(),
                5
        ));
        context.register(TEST_3, new DurationSpawn(
                HTSpawnComponents.builder().entityType(EntityType.SKELETON).build(),
                400,
                100,
                1,
                0
        ));
    }

    static Codec<ISpawnComponent> getDirectCodec(){
        return HTSpawnTypes.registry().byNameCodec().dispatch(ISpawnComponent::getType, ISpawnType::codec);
    }

    static Codec<Holder<ISpawnComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<Pair<Integer, ISpawnComponent>> pairDirectCodec(){
        return Codec.mapPair(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0),
                getDirectCodec().fieldOf("spawn")
        ).codec();
    }

    static Codec<Pair<IntProvider, Holder<ISpawnComponent>>> pairCodec(){
        return Codec.mapPair(
                IntProvider.codec(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", ConstantInt.of(0)),
                getCodec().fieldOf("spawn")
        ).codec();
    }

    static ResourceKey<ISpawnComponent> create(String name) {
        return registry().createKey(HTLibHelper.prefix(name));
    }

    static IHTCodecRegistry<ISpawnComponent> registry() {
        return SPAWNS;
    }

    static SpawnSettingBuilder builder(){
        return new SpawnSettingBuilder();
    }


    class SpawnSettingBuilder {

        private EntityType<?> entityType = EntityType.PIG;
        private CompoundTag nbt = new CompoundTag();
        private boolean enableDefaultSpawn = true;
        private boolean persist = true;
        private Holder<IPositionComponent> placeComponent = null;

        public SpawnComponent.SpawnSetting build() {
            return new SpawnComponent.SpawnSetting(entityType, nbt, enableDefaultSpawn, persist, Optional.ofNullable(placeComponent));
        }

        public SpawnSettingBuilder entityType(EntityType<?> type) {
            this.entityType = type;
            return this;
        }

        public SpawnSettingBuilder nbt(CompoundTag tag){
            this.nbt = tag;
            return this;
        }

        public SpawnSettingBuilder enableDefaultSpawn(boolean flag){
            this.enableDefaultSpawn = flag;
            return this;
        }

        public SpawnSettingBuilder persist(boolean flag){
            this.persist = flag;
            return this;
        }

        public SpawnSettingBuilder placement(Holder<IPositionComponent> placeComponent){
            this.placeComponent = placeComponent;
            return this;
        }
    }
}
