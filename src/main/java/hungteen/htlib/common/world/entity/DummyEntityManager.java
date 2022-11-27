package hungteen.htlib.common.world.entity;

import com.google.common.collect.Maps;
import hungteen.htlib.HTLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;
import java.util.Map;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:24
 **/
public class DummyEntityManager extends SavedData {

    private static final String FORMATION_FILE_ID = "dummy_entities";
    private final ServerLevel level;
    private final Map<Integer, DummyEntity> entityMap = Maps.newHashMap();
    private int currentEntityID = 1;
    private int tick = 0;

    public DummyEntityManager(ServerLevel level) {
        this.level = level;
        this.setDirty();
    }

//    /**
//     * tick all raid in running.
//     * {@link ChallengeManager#tickChallenges(World)}
//     */
//    public void tick() {
//        Iterator<Challenge> iterator = this.challengeMap.values().iterator();
//        while (iterator.hasNext()) {
//            Challenge raid = iterator.next();
//            if (! ConfigUtil.isRaidEnable()) {
//                raid.remove();
//            }
//            if (raid.isRemoving()) {
//                iterator.remove();
//                this.setDirty();
//            } else {
//                this.world.getProfiler().push("Challenge Tick");
//                raid.tick();
//                this.world.getProfiler().pop();
//            }
//        }
//
//        if (++ this.tick % 200 == 0) {
//            this.setDirty();
//        }
//    }

    public static List<DummyEntity> getDummyEntities(ServerLevel serverLevel) {
        return get(serverLevel).entityMap.values().stream().toList();
    }

//    public void sync(boolean add, int id){
//        PlayerHelper.getServerPlayers(this.level).forEach(player -> {
//            NetworkHandler.sendToClient(player, new FormationPacket(this.level.dimension(), add, this.level.getEntity(id).getId()));
//        });
//    }

    /**
     * 最初用实体实现踩的坑 ：<br>
     * Note : Can not use isAlive to check, because EntityJoinLevelEvent is before that. <br>
     * Note : entityId is not sync to the Level currently, thats why ignore checking. <br>
     */
    private void add(DummyEntity dummyEntity){
        add(dummyEntity, true);
    }

    private void remove(DummyEntity dummyEntity){
        add(dummyEntity, true);
    }

//    /**
//     * Only run when server started {@link hungteen.immortal.ImmortalMod#serverStarted(ServerStartedEvent)}.
//     */
//    public void update(){
//        // remove formation that does not exist.
//        this.dummyEntities.removeIf(id -> {
//            if(! EntityHelper.isEntityValid(this.level.getEntity(id))){
//                this.sync(false, id);
//                return true;
//            }
//            return false;
//        });
//        // add existed formation that out of the set.
//        this.level.getEntities().getAll().forEach(entity -> {
//            if(entity instanceof Formation && ! this.dummyEntities.contains(entity.getUUID())){
//                this.add((Formation) entity);
//            }
//        });
//        this.setDirty();
//    }

    private void add(DummyEntity entity, boolean sync){
        this.entityMap.put(entity.getEntityID(), entity);
        this.setDirty();
        if(sync) {
            this.sync(true, id);
        }
    }

    private void remove(DummyEntity entity, boolean sync){
        this.entityMap.remove(entity.getEntityID());
        this.setDirty();
        if(sync){
            this.sync(false, id);
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
                    HTDummyEntities.DUMMY_ENTITY_TYPES.byNameCodec()
                            .parse(NbtOps.INSTANCE, tag.getCompound("DummyEntityType_" + num))
                            .resultOrPartial(msg -> HTLib.getLogger().error(msg))
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
            HTDummyEntities.DUMMY_ENTITY_TYPES.byNameCodec()
                    .encodeStart(NbtOps.INSTANCE, entity.getEntityType())
                    .resultOrPartial(msg -> HTLib.getLogger().error(msg))
                    .ifPresent(nbt -> {
                        tag.put("DummyEntityType_" + num, nbt);
                    });
            tag.put("DummyEntityTag_" + num, entity.save(new CompoundTag()));
        }
        return tag;
    }

}
