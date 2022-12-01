package hungteen.htlib.common.world.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.SpawnDummyEntityPacket;
import hungteen.htlib.util.helper.PlayerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:24
 **/
public class DummyEntityManager extends SavedData {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FORMATION_FILE_ID = "dummy_entities";
    private final ServerLevel level;
    private final Map<Integer, DummyEntity> entityMap = Maps.newHashMap();
    private int currentEntityID = 1;

    public DummyEntityManager(ServerLevel level) {
        this.level = level;
        this.setDirty();
    }

    /**
     * tick all raid in running.
     * {@link HTLib#HTLib()}
     */
    public void tick() {
        final Set<Integer> removedEnttiy = Sets.newHashSet();
        for (DummyEntity entity : this.entityMap.values()) {
            if(entity.isRemoved()){
                removedEnttiy.add(entity.getEntityID());
            } else{
                this.level.getProfiler().push("Dummy Entity Tick");
                entity.tick();
                this.level.getProfiler().pop();
            }
        }

        removedEnttiy.forEach(entityMap::remove);

        if (this.level.getGameTime() % 200 == 0) {
            this.setDirty();
        }
    }

    public void sync(boolean add, DummyEntity dummyEntity){
        PlayerHelper.getServerPlayers(this.level).forEach(player -> {
            NetworkHandler.sendToClient(player, new SpawnDummyEntityPacket(add, dummyEntity));
        });
    }

    public static void setDirty(ServerLevel level){
        get(level).setDirty();
    }

    public static Optional<DummyEntity> getDummyEntity(ServerLevel level, int id){
        return Optional.ofNullable(get(level).entityMap.getOrDefault(id, null));
    }

    public static List<DummyEntity> getDummyEntities(ServerLevel serverLevel) {
        return get(serverLevel).entityMap.values().stream().toList();
    }

    public static <T extends DummyEntity> T createEntity(ServerLevel level, Function<Integer, T> function){
        DummyEntityManager manager = get(level);
        final int entityID = manager.getUniqueId();
        T entity = function.apply(entityID);
        manager.add(entity);
        return entity;
    }

    public static void removeEntity(ServerLevel level, DummyEntity dummyEntity){
        get(level).remove(dummyEntity);
    }

    /**
     * 最初用实体实现踩的坑 ：<br>
     * Note : Can not use isAlive to check, because EntityJoinLevelEvent is before that. <br>
     * Note : entityId is not sync to the Level currently, thats why ignore checking. <br>
     */
    public void add(DummyEntity dummyEntity){
        add(dummyEntity, true);
    }

    private void remove(DummyEntity dummyEntity){
        remove(dummyEntity, true);
    }

    private void add(DummyEntity entity, boolean sync){
        this.entityMap.put(entity.getEntityID(), entity);
        this.setDirty();
        if(sync) {
            this.sync(true, entity);
        }
    }

    private void remove(DummyEntity entity, boolean sync){
        this.entityMap.remove(entity.getEntityID());
        this.setDirty();
        if(sync){
            this.sync(false, entity);
        }
    }

    public int getUniqueId() {
        this.setDirty();
        return this.currentEntityID++;
    }

    public static DummyEntityManager get(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(compound -> load(level, compound), () -> new DummyEntityManager(level), FORMATION_FILE_ID);
    }

    private static DummyEntityManager load(ServerLevel level, CompoundTag tag) {
        final DummyEntityManager manager = new DummyEntityManager(level);
        if(tag.contains("CurrentEntityID")){
            manager.currentEntityID = tag.getInt("CurrentEntityID");
        }
        if(tag.contains("DummyEntityCount")){
            final int count = tag.getInt("DummyEntityCount");
            for(int i = 0; i < count; ++i) {
                final int num = i;
                if(tag.contains("DummyEntityType_" + num)){
                    HTDummyEntities.getCodec()
                            .parse(NbtOps.INSTANCE, tag.get("DummyEntityType_" + num))
                            .resultOrPartial(LOGGER::error)
                            .ifPresent(entityType -> {
                                final CompoundTag nbt = tag.getCompound("DummyEntityTag_" + num);
                                final DummyEntity dummyEntity = entityType.create(level, nbt);
                                manager.add(dummyEntity, false);
                            });
                }
            }
        }
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        final List<DummyEntity> list = getDummyEntities(this.level);
        tag.putInt("CurrentEntityID", this.currentEntityID);
        tag.putInt("DummyEntityCount", list.size());
        for(int i = 0; i < list.size(); ++ i){
            final DummyEntity entity = list.get(i);
            final int num = i;
            HTDummyEntities.getCodec()
                    .encodeStart(NbtOps.INSTANCE, entity.getEntityType())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(nbt -> {
                        tag.put("DummyEntityType_" + num, nbt);
                    });
            tag.put("DummyEntityTag_" + num, entity.save(new CompoundTag()));
        }
        return tag;
    }

}
