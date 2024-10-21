package hungteen.htlib.common.impl.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.ISpawnComponent;
import hungteen.htlib.common.impl.position.HTPositionComponents;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:14
 **/
public abstract class SpawnComponent implements ISpawnComponent {

    private final SpawnSetting spawnSetting;

    public SpawnComponent(SpawnSetting spawnSettings) {
        this.spawnSetting = spawnSettings;
    }

    /**
     * Copy from {@link net.minecraft.server.commands.SummonCommand}
     */
    public Optional<Entity> spawnEntity(ServerLevel level, IRaid raid){
        final Vec3 spawnPosition = raid.getPlaceComponent().apply(this).getPlacePosition(level, raid.getPosition());
        if (Level.isInSpawnableBounds(MathHelper.toBlockPos(spawnPosition))) {
            CompoundTag compoundtag = this.getEntityNBT().copy();
            compoundtag.putString("id", EntityHelper.get().getKey(this.getEntityType()).toString());
            Entity entity = EntityType.loadEntityRecursive(compoundtag, level, (e) -> {
                e.moveTo(spawnPosition.x, spawnPosition.y, spawnPosition.z, e.getYRot(), e.getXRot());
                return e;
            });
            if (entity == null) {
                HTLib.getLogger().error("Fail to create entity {}", this.getEntityType().toString());
            } else {
                if (this.enableDefaultSpawn() && entity instanceof Mob mob) {
                    ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, null, null);
                }

                if(getSpawnSetting().persist()&& entity instanceof Mob mob) {
                    mob.setPersistenceRequired();
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    HTLib.getLogger().warn("Error duplicate UUID");
                } else {
                    return Optional.of(entity);
                }
            }
        } else{
            HTLib.getLogger().warn("Can not getSpawnEntities entity at position {}", spawnPosition);
        }
        return Optional.empty();
    }

    @Override
    public Optional<IPositionComponent> getSpawnPlacement() {
        return this.getSpawnSetting().placeComponent().map(Holder::get);
    }

    public EntityType<?> getEntityType() {
        return getSpawnSetting().entityType();
    }

    public CompoundTag getEntityNBT(){
        return getSpawnSetting().nbt();
    }

    public boolean enableDefaultSpawn(){
        return getSpawnSetting().enableDefaultSpawn();
    }

    public SpawnSetting getSpawnSetting() {
        return spawnSetting;
    }

    public record SpawnSetting(EntityType<?> entityType, CompoundTag nbt, boolean enableDefaultSpawn, boolean persist, Optional<Holder<IPositionComponent>> placeComponent){

        /**
         * entityType : 生物的类型，The getSpawnEntities entityType of the entity.
         * nbt : 附加数据，CompoundTag of the entity.
         * placementType : 放置类型，决定放在什么地方，
         * spawnTick : 生成的时间，When to getSpawnEntities the entity.
         * spawnCount : 生成数量，How many entities to getSpawnEntities.
         */
        public static final Codec<SpawnSetting> CODEC = RecordCodecBuilder.<SpawnSetting>mapCodec(instance -> instance.group(
                ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(SpawnSetting::entityType),
                CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(SpawnSetting::nbt),
                Codec.BOOL.optionalFieldOf("enable_default_spawn", true).forGetter(SpawnSetting::enableDefaultSpawn),
                Codec.BOOL.optionalFieldOf("persist", true).forGetter(SpawnSetting::persist),
                Codec.optionalField("spawn_placement", HTPositionComponents.getCodec()).forGetter(SpawnSetting::placeComponent)
                ).apply(instance, SpawnSetting::new)).codec();
    }

}
