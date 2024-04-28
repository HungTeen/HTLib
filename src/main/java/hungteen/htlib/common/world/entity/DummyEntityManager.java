package hungteen.htlib.common.world.entity;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.event.events.DummyEntityEvent;
import hungteen.htlib.common.network.DummyEntityPacket;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.util.helper.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

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

    public static DummyEntity createDummyEntity(ServerLevel level, ResourceLocation location, Vec3 position, CompoundTag tag) {
        BlockPos blockpos = new BlockPos(position);
        Optional<? extends DummyEntityType<?>> opt = HTDummyEntities.getEntityType(location);
        if(Level.isInSpawnableBounds(blockpos) && opt.isPresent()){
            final DummyEntityType<?> dummyEntityType = opt.get();
            CompoundTag compoundtag = tag.copy();
            compoundtag.putInt("DummyEntityID", DummyEntityManager.get(level).getUniqueId());
            Vec3.CODEC.encodeStart(NbtOps.INSTANCE, position)
                    .result().ifPresent(nbt -> compoundtag.put("Position", nbt));

            DummyEntity dummyEntity = dummyEntityType.create(level, compoundtag);
            if (dummyEntity != null && ! MinecraftForge.EVENT_BUS.post(new DummyEntityEvent.DummyEntitySpawnEvent(level, dummyEntity))) {
                DummyEntityManager.get(level).add(dummyEntity);
            }
            return dummyEntity;
        }
        return null;
    }

    /**
     * {@link HTLib#HTLib()}
     */
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel) {
            DummyEntityManager.get((ServerLevel) event.level).tick();
        }
    }

    /**
     * tick all raid in running.
     */
    protected void tick() {
        final List<DummyEntity> removedEntity = new ArrayList<>();
        for (DummyEntity entity : this.entityMap.values()) {
            if(entity.isRemoved()){
                removedEntity.add(entity);
            } else{
                this.level.getProfiler().push("Dummy Entity Tick");
                entity.tick();
                this.level.getProfiler().pop();
            }
        }

        removedEntity.forEach(this::remove);

        if (this.level.getGameTime() % 200 == 0) {
            this.setDirty();
        }
    }

    public void syncToClient(ServerPlayer player){
        this.entityMap.forEach((id, entity) -> {
            NetworkHandler.sendToClient(player, new DummyEntityPacket(DummyEntityPacket.Operation.CREATE, entity));
        });
    }

    public void sync(boolean add, DummyEntity dummyEntity){
        PlayerHelper.getServerPlayers(this.level).forEach(player -> {
            NetworkHandler.sendToClient(player, new DummyEntityPacket(add ? DummyEntityPacket.Operation.CREATE : DummyEntityPacket.Operation.REMOVE, dummyEntity));
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

    public static Stream<DummyEntity> getDummyEntities(ServerLevel serverLevel, ResourceLocation entityType) {
        return DummyEntityManager.getDummyEntities(serverLevel).stream()
                .filter(dummyEntity -> dummyEntity.getEntityType().getLocation().equals(entityType));
    }

    public static Stream<DummyEntity> getDummyEntities(ServerLevel serverLevel, ResourceLocation entityType, Vec3 position, int limit) {
        return DummyEntityManager.getDummyEntities(serverLevel, entityType)
                .sorted(Comparator.comparingDouble(dummyEntity -> dummyEntity.getPosition().distanceTo(position)))
                .limit(limit);
    }

    public static Stream<DummyEntity> getCollisionEntities(Level level){
        return HTLib.PROXY.getDummyEntities(level).stream().filter(DummyEntity::hasCollision);
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

    public static void markRemoveEntities(List<DummyEntity> dummyEntities){
        dummyEntities.forEach(DummyEntity::remove);
    }

    /**
     * 最初用实体实现踩的坑 ：<br>
     * Note : Can not use isAlive to check, because EntityJoinLevelEvent is before that. <br>
     * Note : entityId is not sync to the Level currently, thats why ignore checking. <br>
     */
    public void add(DummyEntity dummyEntity){
        add(dummyEntity, true);
    }

    public void remove(DummyEntity dummyEntity){
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
                                manager.add(dummyEntity, true);
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
