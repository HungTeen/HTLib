package hungteen.htlib.common.impl.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.api.interfaces.raid.SpawnType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 17:56
 **/
public class OnceSpawn extends SpawnComponent {

    /**
     * entityType : 生物的类型，The getSpawnEntities entityType of the entity.
     * nbt : 附加数据，CompoundTag of the entity.
     * placementType : 放置类型，决定放在什么地方，
     * spawnTick : 生成的时间，When to getSpawnEntities the entity.
     * spawnCount : 生成数量，How many entities to getSpawnEntities.
     */
    public static final Codec<OnceSpawn> CODEC = RecordCodecBuilder.<OnceSpawn>mapCodec(instance -> instance.group(
            SpawnSetting.CODEC.fieldOf("setting").forGetter(OnceSpawn::getSpawnSetting),
            IntProvider.POSITIVE_CODEC.fieldOf("spawn_count").forGetter(OnceSpawn::getSpawnCount)
    ).apply(instance, OnceSpawn::new)).codec();
    private final IntProvider spawnCount;

    public OnceSpawn(SpawnSetting spawnSettings, IntProvider spawnCount){
        super(spawnSettings);
        this.spawnCount = spawnCount;
    }

    @Override
    public List<Entity> getSpawnEntities(ServerLevel level, IRaid raid, int tick, int startTick) {
        List<Entity> entities = new ArrayList<>();
        if(tick == startTick){
            final int spawnCount = getSpawnCount().sample(level.random);
            for(int i = 0; i < spawnCount; ++ i){
                this.spawnEntity(level, raid).ifPresent(entities::add);
            }
        }
        return entities;
    }

    @Override
    public boolean finishedSpawn(int tick, int startTick) {
        return tick > startTick;
    }

    @Override
    public SpawnType<?> getType() {
        return HTLibSpawnTypes.ONCE;
    }

    public IntProvider getSpawnCount() {
        return spawnCount;
    }
}
