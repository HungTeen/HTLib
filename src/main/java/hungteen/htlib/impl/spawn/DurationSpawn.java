package hungteen.htlib.impl.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.ISpawnComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 17:56
 **/
public class DurationSpawn extends SpawnComponent {

    /**
     * entityType : 生物的类型，The getSpawnEntities entityType of the entity.
     * nbt : 附加数据，CompoundTag of the entity.
     * placementType : 放置类型，决定放在什么地方，
     * spawnTick : 生成的时间，When to getSpawnEntities the entity.
     * spawnCount : 生成数量，How many entities to getSpawnEntities.
     */
    public static final Codec<DurationSpawn> CODEC = RecordCodecBuilder.<DurationSpawn>mapCodec(instance -> instance.group(
            SpawnSettings.CODEC.fieldOf("spawn_settings").forGetter(DurationSpawn::getSpawnSettings),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0).forGetter(DurationSpawn::getStartSpawnTick),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("duration").forGetter(DurationSpawn::getSpawnDuration),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("spawn_interval").forGetter(DurationSpawn::getSpawnInterval),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("each_spawn_count").forGetter(DurationSpawn::getEachSpawnCount),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("spawn_offset", 0).forGetter(DurationSpawn::getSpawnOffset)
    ).apply(instance, DurationSpawn::new)).codec();
    private final int startSpawnTick;
    private final int spawnDuration;
    private final int spawnInterval;
    private final int eachSpawnCount;
    private final int spawnOffset;

    public DurationSpawn(SpawnSettings spawnSettings, int startSpawnTick, int spawnDuration, int spawnInterval, int eachSpawnCount, int spawnOffset){
        super(spawnSettings);
        this.startSpawnTick = startSpawnTick;
        this.spawnDuration = spawnDuration;
        this.spawnInterval = spawnInterval;
        this.eachSpawnCount = eachSpawnCount;
        this.spawnOffset = spawnOffset;
    }

    private boolean canSpawn(int tick) {
        return tick >= this.getStartSpawnTick() + this.getSpawnOffset() && ! this.finishedSpawn(tick) && (tick - this.getStartSpawnTick()) % this.getSpawnInterval() == this.getSpawnOffset();
    }

    @Override
    public List<Entity> getSpawnEntities(ServerLevel level, IRaid raid, int tick) {
        List<Entity> entities = new ArrayList<>();
        if(canSpawn(tick)){
            for(int i = 0; i < this.getEachSpawnCount(); ++ i){
                this.spawnEntity(level, raid).ifPresent(entities::add);
            }
        }
        return entities;
    }

    @Override
    public boolean finishedSpawn(int tick) {
        return tick > this.getStartSpawnTick() + this.getSpawnDuration();
    }

    @Override
    public ISpawnComponentType<?> getType() {
        return HTSpawnComponents.DURATION_SPAWN_TYPE;
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
