package hungteen.htlib.common.world.entity;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import hungteen.htlib.common.network.packets.DummyEntityPacket;
import hungteen.htlib.util.helper.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
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
    private final Map<Integer, DummyEntityImpl> entityMap = Maps.newHashMap();
    private int currentEntityID = 1;

    public DummyEntityManager(ServerLevel level) {
        this.level = level;
        this.setDirty();
    }

    /**
     * Usually called by command, generic usage.
     */
    @Nullable
    public static DummyEntityImpl createDummyEntity(ServerLevel level, ResourceLocation location, Vec3 position, CompoundTag tag) {
        final BlockPos blockpos = MathHelper.toBlockPos(position);
        final Optional<? extends DummyEntityType<?>> opt = HTLibDummyEntities.getEntityType(location);
        if(Level.isInSpawnableBounds(blockpos) && opt.isPresent()){
            final DummyEntityType<?> dummyEntityType = opt.get();
            CompoundTag compoundtag = tag.copy();
            compoundtag.putInt("DummyEntityID", DummyEntityManager.get(level).getUniqueId());
            Vec3.CODEC.encodeStart(NbtOps.INSTANCE, position)
                    .result().ifPresent(nbt -> compoundtag.put("Position", nbt));

            DummyEntityImpl dummyEntity = dummyEntityType.create(level, compoundtag);
            return addEntity(level, dummyEntity);
        }
        return null;
    }

    /**
     * Specific usage.
     */
    @Nullable
    public static <T extends DummyEntityImpl> T addEntity(ServerLevel level, T dummyEntity) {
        if (dummyEntity != null && ! MinecraftForge.EVENT_BUS.post(new DummyEntityEvent.DummyEntitySpawnEvent(level, dummyEntity))) {
            DummyEntityManager.get(level).add(dummyEntity);
            return dummyEntity;
        }
        return null;
    }

    /**
     * {@link HTLibForgeInitializer#HTLib()}
     */
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel) {
            DummyEntityManager.get((ServerLevel) event.level).tick();
        }
    }

    /**
     * tick all raid in running.
     */
    void tick() {
        final List<DummyEntityImpl> removedEntity = new ArrayList<>();
        for (DummyEntityImpl entity : this.entityMap.values()) {
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

    /**
     * 玩家进入世界初始化同步。
     */
    public void initialize(ServerPlayer player){
        this.entityMap.forEach((id, entity) -> {
            NetworkHandler.sendToClient(player, new DummyEntityPacket(DummyEntityPacket.Operation.CREATE, entity));
        });
    }

    /**
     * 玩家退出世界清除同步。
     */
    public void finalize(ServerPlayer player){
        NetworkHandler.sendToClient(player, new DummyEntityPacket());
    }

    public void sync(boolean add, DummyEntityImpl dummyEntity){
        NetworkHandler.sendToClient(new DummyEntityPacket(add ? DummyEntityPacket.Operation.CREATE : DummyEntityPacket.Operation.REMOVE, dummyEntity));
    }

    public static void setDirty(ServerLevel level){
        get(level).setDirty();
    }

    public static Optional<DummyEntityImpl> getDummyEntity(ServerLevel level, int id){
        return Optional.ofNullable(get(level).entityMap.getOrDefault(id, null));
    }

    public static List<DummyEntityImpl> getDummyEntities(ServerLevel serverLevel) {
        return get(serverLevel).entityMap.values().stream().toList();
    }

    public static Stream<DummyEntityImpl> getDummyEntities(ServerLevel serverLevel, ResourceLocation entityType) {
        return DummyEntityManager.getDummyEntities(serverLevel).stream()
                .filter(dummyEntity -> dummyEntity.getEntityType().getLocation().equals(entityType));
    }

    public static Stream<DummyEntityImpl> getDummyEntities(ServerLevel serverLevel, ResourceLocation entityType, Vec3 position, int limit) {
        return DummyEntityManager.getDummyEntities(serverLevel, entityType)
                .sorted(Comparator.comparingDouble(dummyEntity -> dummyEntity.getPosition().distanceTo(position)))
                .limit(limit);
    }

    public static Stream<DummyEntityImpl> getCollisionEntities(Level level){
        return HTLibForgeInitializer.PROXY.getDummyEntities(level).stream().filter(DummyEntityImpl::hasCollision);
    }

    public static <T extends DummyEntityImpl> T createEntity(ServerLevel level, Function<Integer, T> function){
        DummyEntityManager manager = get(level);
        final int entityID = manager.getUniqueId();
        T entity = function.apply(entityID);
        manager.add(entity);
        return entity;
    }

    public static void removeEntity(ServerLevel level, DummyEntityImpl dummyEntity){
        get(level).remove(dummyEntity);
    }

    public static void markRemoveEntities(List<DummyEntityImpl> dummyEntities){
        dummyEntities.forEach(DummyEntityImpl::remove);
    }

    /**
     * 最初用实体实现踩的坑 ：<br>
     * Note : Can not use isAlive to check, because EntityJoinLevelEvent is before that. <br>
     * Note : entityId is not sync to the Level currently, thats why ignore checking. <br>
     */
    public void add(DummyEntityImpl dummyEntity){
        add(dummyEntity, true);
    }

    public void remove(DummyEntityImpl dummyEntity){
        remove(dummyEntity, true);
    }

    private void add(DummyEntityImpl entity, boolean sync){
        this.entityMap.put(entity.getEntityID(), entity);
        this.setDirty();
        if(sync) {
            this.sync(true, entity);
        }
    }

    private void remove(DummyEntityImpl entity, boolean sync){
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
                    HTLibDummyEntities.getCodec()
                            .parse(NbtOps.INSTANCE, tag.get("DummyEntityType_" + num))
                            .resultOrPartial(LOGGER::error)
                            .ifPresent(entityType -> {
                                final CompoundTag nbt = tag.getCompound("DummyEntityTag_" + num);
                                final DummyEntityImpl dummyEntity = entityType.create(level, nbt);
                                manager.add(dummyEntity, true);
                            });
                }
            }
        }
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        final List<DummyEntityImpl> list = getDummyEntities(this.level);
        tag.putInt("CurrentEntityID", this.currentEntityID);
        tag.putInt("DummyEntityCount", list.size());
        for(int i = 0; i < list.size(); ++ i){
            final DummyEntityImpl entity = list.get(i);
            final int num = i;
            HTLibDummyEntities.getCodec()
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
