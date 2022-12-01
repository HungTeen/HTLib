package hungteen.htlib.impl.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.ISpawnComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 17:56
 **/
public class OnceSpawn extends BaseSpawn {

    /**
     * entityType : 生物的类型，The getSpawnEntities type of the entity.
     * nbt : 附加数据，CompoundTag of the entity.
     * placementType : 放置类型，决定放在什么地方，
     * spawnTick : 生成的时间，When to getSpawnEntities the entity.
     * spawnCount : 生成数量，How many entities to getSpawnEntities.
     */
    public static final Codec<OnceSpawn> CODEC = RecordCodecBuilder.<OnceSpawn>mapCodec(instance -> instance.group(
            SpawnSettings.CODEC.fieldOf("spawn_settings").forGetter(OnceSpawn::getSpawnSettings),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spawn_tick").forGetter(OnceSpawn::getSpawnTick),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("spawn_count").forGetter(OnceSpawn::getSpawnCount)
    ).apply(instance, OnceSpawn::new)).codec();
    private final int spawnTick;
    private final int spawnCount;

    public OnceSpawn(SpawnSettings spawnSettings, int spawnTick, int spawnCount){
        super(spawnSettings);
        this.spawnTick = spawnTick;
        this.spawnCount = spawnCount;
    }

    private boolean canSpawn(int tick) {
        return tick == this.getSpawnTick();
    }

    @Override
    public List<Entity> getSpawnEntities(ServerLevel level, IRaid raid, int tick) {
        List<Entity> entities = new ArrayList<>();
        if(canSpawn(tick)){
            for(int i = 0; i < this.getSpawnCount(); ++ i){
                this.spawnEntity(level, raid).ifPresent(entities::add);
            }
        }
        return entities;
    }

    @Override
    public boolean finishedSpawn(int tick) {
        return tick > this.getSpawnTick();
    }

    @Override
    public ISpawnComponentType<?> getType() {
        return HTSpawnComponents.ONCE_SPAWN_TYPE;
    }

    public int getSpawnTick() {
        return spawnTick;
    }

    public int getSpawnCount() {
        return spawnCount;
    }
}
