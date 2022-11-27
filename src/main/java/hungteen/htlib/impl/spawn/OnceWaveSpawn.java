package hungteen.htlib.impl.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.util.interfaces.ISpawnComponentType;
import hungteen.htlib.impl.placement.HTPlacements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 17:56
 **/
public class OnceWaveSpawn extends BaseWaveSpawn {

    /**
     * entityType : 生物的类型，The spawn type of the entity.
     * nbt : 附加数据，CompoundTag of the entity.
     * placementType : 放置类型，决定放在什么地方，
     * spawnTick : 生成的时间，When to spawn the entity.
     * spawnCount : 生成数量，How many entities to spawn.
     */
    public static final Codec<OnceWaveSpawn> CODEC = RecordCodecBuilder.<OnceWaveSpawn>mapCodec(instance -> instance.group(
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(OnceWaveSpawn::getEntityType),
            CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(OnceWaveSpawn::getEntityNBT),
            Codec.optionalField("placement_type", HTPlacements.getCodec()).forGetter(OnceWaveSpawn::getSpawnPlacement),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spawn_tick").forGetter(OnceWaveSpawn::getSpawnTick),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spawn_count").forGetter(OnceWaveSpawn::getSpawnCount)
    ).apply(instance, OnceWaveSpawn::new)).codec();
    private final int spawnTick;
    private final int spawnCount;

    public OnceWaveSpawn(EntityType<?> entityType, CompoundTag entityNBT, Optional<PlaceComponent> spawnPlacement, int spawnTick, int spawnCount){
        super(entityType, entityNBT, spawnPlacement);
        this.spawnTick = spawnTick;
        this.spawnCount = spawnCount;
    }

    @Override
    public boolean canSpawn(int tick) {
        return tick == this.getSpawnTick();
    }

    @Override
    public void spawn(ServerLevel level, Vec3 origin, int tick) {
        for(int i = 0; i < this.getSpawnCount(); ++ i){
            this.spawnEntity(level, origin);
        }
    }

    @Override
    public boolean finishedSpawn(int tick) {
        return tick > this.getSpawnTick();
    }

    @Override
    public ISpawnComponentType<?> getType() {
        return HTWaveSpawns.ONCE_SPAWN_TYPE;
    }

    public int getSpawnTick() {
        return spawnTick;
    }

    public int getSpawnCount() {
        return spawnCount;
    }
}
