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
public class DurationWaveSpawn extends BaseWaveSpawn {

    /**
     * entityType : 生物的类型，The spawn type of the entity.
     * nbt : 附加数据，CompoundTag of the entity.
     * placementType : 放置类型，决定放在什么地方，
     * spawnTick : 生成的时间，When to spawn the entity.
     * spawnCount : 生成数量，How many entities to spawn.
     */
    public static final Codec<DurationWaveSpawn> CODEC = RecordCodecBuilder.<DurationWaveSpawn>mapCodec(instance -> instance.group(
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(DurationWaveSpawn::getEntityType),
            CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(DurationWaveSpawn::getEntityNBT),
            Codec.optionalField("placement_type", HTPlacements.getCodec()).forGetter(DurationWaveSpawn::getSpawnPlacement),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0).forGetter(DurationWaveSpawn::getStartSpawnTick),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("duration").forGetter(DurationWaveSpawn::getSpawnDuration),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("spawn_interval").forGetter(DurationWaveSpawn::getSpawnInterval),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("each_spawn_count").forGetter(DurationWaveSpawn::getEachSpawnCount),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("spawn_offset", 0).forGetter(DurationWaveSpawn::getSpawnOffset)
    ).apply(instance, DurationWaveSpawn::new)).codec();
    private final int startSpawnTick;
    private final int spawnDuration;
    private final int spawnInterval;
    private final int eachSpawnCount;
    private final int spawnOffset;

    public DurationWaveSpawn(EntityType<?> entityType, CompoundTag entityNBT, Optional<PlaceComponent> spawnPlacement, int startSpawnTick, int spawnDuration, int spawnInterval, int eachSpawnCount, int spawnOffset){
        super(entityType, entityNBT, spawnPlacement);
        this.startSpawnTick = startSpawnTick;
        this.spawnDuration = spawnDuration;
        this.spawnInterval = spawnInterval;
        this.eachSpawnCount = eachSpawnCount;
        this.spawnOffset = spawnOffset;
    }

    @Override
    public boolean canSpawn(int tick) {
        return tick >= this.getStartSpawnTick() + this.getSpawnOffset() && ! this.finishedSpawn(tick) && (tick - this.getStartSpawnTick()) % this.getSpawnInterval() == this.getSpawnOffset();
    }

    @Override
    public void spawn(ServerLevel level, Vec3 origin, int tick) {
        for(int i = 0; i < this.getEachSpawnCount(); ++ i){
            this.spawnEntity(level, origin);
        }
    }

    @Override
    public boolean finishedSpawn(int tick) {
        return tick > this.getStartSpawnTick() + this.getSpawnDuration();
    }

    @Override
    public ISpawnComponentType<?> getType() {
        return HTWaveSpawns.DURATION_SPAWN_TYPE;
    }

    public int getStartSpawnTick() {
        return startSpawnTick;
    }

    public int getSpawnDuration() {
        return spawnDuration;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public int getEachSpawnCount() {
        return eachSpawnCount;
    }

    public int getSpawnOffset() {
        return spawnOffset;
    }
}
