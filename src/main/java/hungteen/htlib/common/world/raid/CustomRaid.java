package hungteen.htlib.common.world.raid;

import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.util.interfaces.IDummyEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 21:02
 **/
public class CustomRaid implements DummyEntity {

    private static final MutableComponent CHALLENGE_NAME_COMPONENT = Component.translatable("raid.htlib.name");
    private static final MutableComponent CHALLENGE_WARN = Component.translatable("raid.htlib.too_far_away").withStyle(ChatFormatting.RED);
    private final ServerBossEvent challengeBar = new ServerBossEvent(CHALLENGE_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    private final int id;
    public final ServerLevel serverLevel;
    public final ResourceLocation raidLocation;
    protected RaidComponent raidComponent;
    protected BlockPos center;
    protected Status status = Status.PREPARE;
    protected int tick = 0;
    protected int stopTick = 0;
    protected int currentWave = 0;
    protected int currentSpawn = 0;
    protected Set<Entity> raiders = new HashSet<>();
    protected Set<UUID> heroes = new HashSet<>();
    private boolean firstTick = false;
    private int currentMaxLevel = 0;

    public CustomRaid(int id, ServerLevel serverLevel, ResourceLocation res, BlockPos pos) {
        this.id = id;
        this.serverLevel = serverLevel;
        this.raidLocation = res;
        this.center = pos;
    }

    public CustomRaid(ServerLevel serverLevel, CompoundTag nbt) {
        this.serverLevel = serverLevel;
        this.id = nbt.getInt("challenge_id");
        this.status = Status.values()[nbt.getInt("challenge_status")];
        this.raidLocation = new ResourceLocation(nbt.getString("challenge_resource"));
        this.tick = nbt.getInt("challenge_tick");
        this.stopTick = nbt.getInt("stop_tick");
        this.currentWave = nbt.getInt("current_wave");
        this.currentSpawn = nbt.getInt("current_spawn");
        this.firstTick = nbt.getBoolean("first_tick");
        {// for raid center position.
            CompoundNBT tmp = nbt.getCompound("center_pos");
            this.center = new BlockPos(tmp.getInt("pos_x"), tmp.getInt("pos_y"), tmp.getInt("pos_z"));
        }
        {// for raiders entity id.
            ListNBT list = nbt.getList("raiders", 11);
            for(int i = 0; i < list.size(); ++ i) {
                final Entity entity = world.getEntity(NBTUtil.loadUUID(list.get(i)));
                if(entity != null) {
                    this.raiders.add(entity);
                }
            }
        }
        {// for heroes uuid.
            ListNBT list = nbt.getList("heroes", 11);
            for(int i = 0; i < list.size(); ++ i) {
                this.heroes.add(NBTUtil.loadUUID(list.get(i)));
            }
        }
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putInt("challenge_id", this.id);
        nbt.putInt("challenge_status", this.status.ordinal());
        nbt.putString("challenge_resource", this.raidLocation.toString());
        nbt.putInt("challenge_tick", this.tick);
        nbt.putInt("stop_tick", this.stopTick);
        nbt.putInt("current_wave", this.currentWave);
        nbt.putInt("current_spawn", this.currentSpawn);
        nbt.putBoolean("first_tick", this.firstTick);
        {// for raid center position.
            CompoundNBT tmp = new CompoundNBT();
            tmp.putInt("pos_x", this.center.getX());
            tmp.putInt("pos_y", this.center.getY());
            tmp.putInt("pos_z", this.center.getZ());
            nbt.put("center_pos", tmp);
        }
        {// for raiders entity id.
            ListNBT list = new ListNBT();
            for(Entity entity : this.raiders) {
                list.add(NBTUtil.createUUID(entity.getUUID()));
            }
            nbt.put("raiders", list);
        }
        {// for heroes uuid.
            ListNBT list = new ListNBT();
            for(UUID uuid : this.heroes) {
                list.add(NBTUtil.createUUID(uuid));
            }
            nbt.put("heroes", list);
        }
    }

    /**
     * {@link PVZChallengeData#tick()}
     */
    public void tick() {
        /* skip tick */
        if(this.isRemoving() || this.serverLevel.players().isEmpty()) {
            return ;
        }
        /* not allow to be peaceful */
        if(this.serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
            return ;
        }
        /* is raid component valid */
        if(this.getRaidComponent() == null) {
            this.remove();
            PVZMod.LOGGER.warn("Challenge Tick Error : Where is the challenge component ?");
            return ;
        }
        this.tickBar();
        if(this.isStopping()) {
            /* has stopped */
            if(++ this.stopTick >= ConfigUtil.getRaidWaitTime()) {
                this.remove();
            }
        }
        if(this.isPreparing()) {
            /* prepare state */
            if(this.tick >= this.raidComponent.getPrepareCD(this.currentWave)) {
                this.waveStart();
            }
        } else if(this.isRunning()) {
            /* running state */
            if(this.tick >= this.raidComponent.getLastDuration(this.currentWave)
                    || (this.raiders.isEmpty() && this.raidComponent.isWaveFinish(this.currentWave, this.currentSpawn))) {
                this.checkNextWave();
            }
            if(this.isLoss()) {//fail to start next wave.
                this.onLoss();
                return ;
            }
            if(this.isVictory()) {
                this.onVictory();
                return ;
            }
            this.tickWave();
        } else if(this.isLoss()) {
            /* loss state */
            if(this.tick >= this.raidComponent.getLossTick()) {
                this.remove();
            }
        } else if(this.isVictory()) {
            /* running state */
            if(this.tick >= this.raidComponent.getWinTick()) {
                this.remove();
            }
        }
        if(! this.firstTick){//first tick.
            this.firstTick = true;
            this.getPlayers().forEach(p -> PlayerUtil.playClientSound(p, this.raidComponent.getPrepareSound()));
        }
        ++ this.tick;
    }

    /**
     * {@link #tick()}
     */
    protected void tickWave() {
        /* update difficulty level */
        if(this.getServerLevel().getDifficulty() == Difficulty.HARD){
            if(this.tick % 10 == 2){
                this.currentMaxLevel = 0;
                this.getPlayers().forEach(p -> {
                    this.currentMaxLevel += PlayerUtil.getResource(p, Resources.TREE_LVL);
                });
            }
        } else {
            this.currentMaxLevel = 0;
        }

        /* check spawn entities */
        final List<ISpawnComponent> spawns = this.raidComponent.getSpawns(this.currentWave);
        while(this.currentSpawn < spawns.size() && this.tick >= spawns.get(this.currentSpawn).getSpawnTick()) {
            this.spawnEntities(spawns.get(this.currentSpawn++));
        }

        /* update raiders list */
        Iterator<Entity> it = this.raiders.iterator();
        while(it.hasNext()) {
            Entity entity = it.next();
            if(! entity.isAlive()) {
                it.remove();
            }
        }
    }

    protected void spawnEntities(ISpawnComponent spawn) {
        final int count = spawn.getSpawnAmount();
        for(int i = 0; i < count; ++ i) {
            Entity entity = this.createEntity(spawn);
            if(entity != null) {
                this.raiders.add(entity);
                if(entity instanceof MobEntity) {
                    // avoid despawn.
                    ((MobEntity) entity).setPersistenceRequired();

                    //close to center goal.
                    if (this.getRaidComponent().shouldCloseToCenter()) {
                        ((MobEntity) entity).goalSelector.addGoal(0, new ChallengeMoveGoal(((MobEntity) entity), this));
                    }
                }
                if(entity instanceof AbstractPAZEntity){//init skills.
                    AbstractPAZEntity.randomInitSkills((AbstractPAZEntity) entity, Math.max(0, this.currentMaxLevel - this.getRaidComponent().getRecommendLevel()));
                }
            }
        }
    }

    /**
     * copy from {@link SummonCommand}
     */
    private Entity createEntity(ISpawnComponent spawn) {
        final IPlacementComponent placement = spawn.getPlacement() != null ? spawn.getPlacement() : this.raidComponent.getPlacement(this.currentWave);
        final BlockPos pos = placement.getPlacePosition(this.serverLevel, this.center);
        return EntityUtil.createWithNBT(this.serverLevel, spawn.getSpawnType(), spawn.getNBT(), pos);
    }

    /**
     * {@link #tick()}
     */
    protected void tickBar() {
        if(this.tick % 10 == 0 && ! this.serverLevel.players().isEmpty()) {
            this.updatePlayers();
        }
        this.challengeBar.setColor(this.raidComponent.getBarColor());
        if(this.isPreparing()) {
            this.challengeBar.setName(this.raidComponent.getTitle());
            this.challengeBar.setPercent(this.tick * 1.0F / this.raidComponent.getPrepareCD(this.currentWave));
        } else if(this.isRunning()) {
            this.challengeBar.setName(this.raidComponent.getTitle().copy().append(" - ").append(new TranslationTextComponent("event.minecraft.raid.raiders_remaining", this.raiders.size())));
            this.challengeBar.setPercent(1 - this.tick * 1.0F / this.raidComponent.getLastDuration(this.currentWave));
        } else if(this.isVictory()) {
            this.challengeBar.setName(this.raidComponent.getTitle().copy().append(" - ").append(this.raidComponent.getWinTitle()));
            this.challengeBar.setPercent(1F);
        } else if(this.isLoss()) {
            this.challengeBar.setName(this.raidComponent.getTitle().copy().append(" - ").append(this.raidComponent.getLossTitle()));
            this.challengeBar.setPercent(1F);
        }
    }

    /**
     * player who is alive and in suitable range can be tracked.
     */
    private Predicate<ServerPlayerEntity> validPlayer() {
        return (player) -> {
            final int range = ConfigUtil.getRaidRange();
            return player.isAlive() && Math.abs(player.getX() - this.center.getX()) < range
                    && Math.abs(player.getY() - this.center.getY()) < range
                    && Math.abs(player.getZ() - this.center.getZ()) < range;
        };
    }

    /**
     * {@link #tickBar()}
     */
    protected void updatePlayers() {
        final Set<ServerPlayerEntity> oldPlayers = Sets.newHashSet(this.challengeBar.getPlayers());
        final Set<ServerPlayerEntity> newPlayers = Sets.newHashSet(this.serverLevel.getPlayers(this.validPlayer()));

        /* add new join players */
        newPlayers.forEach(p -> {
            if(! oldPlayers.contains(p)) {
                this.challengeBar.addPlayer(p);
            }
        });

        /* remove offline players */
        oldPlayers.forEach(p -> {
            if(! newPlayers.contains(p)) {

                this.challengeBar.removePlayer(p);
            }
        });

        /* add heroes */
        this.challengeBar.getPlayers().forEach(p -> {
            if(! this.heroes.contains(p.getUUID())) {
                this.heroes.add(p.getUUID());
            }
        });

        if(this.challengeBar.getPlayers().isEmpty()){
            if(! this.isStopping()) {
                ++ this.stopTick;
                this.heroes.forEach(uuid -> {
                    PlayerEntity player = this.serverLevel.getPlayerByUUID(uuid);
                    if(player != null) {
                        PlayerUtil.sendMsgTo(player, CHALLENGE_WARN);
                    }
                });
            }
        } else {
            this.stopTick = 0;
        }
    }

    /**
     * run when prepare time is finished.
     */
    protected void waveStart() {
        this.tick = 0;
        this.status = Status.RUNNING;
        this.getPlayers().forEach(p -> {
            if(this.getRaidComponent().showRoundTitle()){
                PlayerUtil.sendTitleToPlayer(p, new TranslationTextComponent("challenge.pvz.round", this.currentWave + 1).withStyle(TextFormatting.DARK_RED));
            }
            PlayerUtil.playClientSound(p, this.raidComponent.getStartWaveSound());
        });
    }

    /**
     * check can start next wave or not.
     */
    public boolean canNextWave() {
        return this.raiders.isEmpty();
    }

    /**
     * {@link #tick()}
     */
    protected void checkNextWave() {
        this.tick = 0;
        if(this.canNextWave()) {
            this.currentSpawn = 0;
            if(++ this.currentWave >= this.raidComponent.getTotalWaveCount()) {
                this.status = Status.VICTORY;
            } else {
                this.status = Status.PREPARE;
            }
        } else {
            this.status = Status.LOSS;
        }
    }

    /**
     * run when raid is not defeated.
     */
    protected void onLoss() {
        this.tick = 0;
        this.getPlayers().forEach(p -> PlayerUtil.playClientSound(p, this.raidComponent.getLossSound()));
        MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidLossEvent(this));
    }

    /**
     * run when raid is defeated.
     */
    protected void onVictory() {
        this.tick = 0;
        this.getPlayers().forEach(p -> {
            PlayerUtil.playClientSound(p, this.raidComponent.getWinSound());
            ChallengeTrigger.INSTANCE.trigger(p, this.raidLocation.toString());
        });
        if(! MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidWinEvent(this))) {
            this.getPlayers().forEach(p -> {
                this.raidComponent.getRewards().forEach(r -> r.reward(p));
            });
            this.raidComponent.getRewards().forEach(r -> r.rewardGlobally(this));
        }
    }

    public void remove() {
        this.status = Status.REMOVING;
        this.challengeBar.removeAllPlayers();
        this.raiders.forEach(e -> e.remove());
    }

    public int getId() {
        return this.id;
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public boolean isRaider(Entity raider) {
        return this.raiders.contains(raider);
    }

    public boolean isStopping() {
        return this.stopTick > 0;
    }

    public boolean isPreparing() {
        return this.status == Status.PREPARE;
    }

    public boolean isRunning() {
        return this.status == Status.RUNNING;
    }

    public boolean isRemoving() {
        return this.status == Status.REMOVING;
    }

    public boolean isLoss() {
        return this.status == Status.LOSS;
    }

    public boolean isVictory() {
        return this.status == Status.VICTORY;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * get raid component by resource.
     */
    public IChallengeComponent getRaidComponent() {
        return this.raidComponent != null ? this.raidComponent : (this.raidComponent = ChallengeManager.getChallengeByResource(this.raidLocation));
    }

    /**
     * get tracked players by raid bar.
     */
    public List<ServerPlayerEntity> getPlayers(){
        return this.challengeBar.getPlayers().stream().collect(Collectors.toList());
    }

    public boolean hasTag(String tag) {
        return this.raidComponent.hasTag(tag);
    }

    public List<String> getAuthors(){
        return this.raidComponent.getAuthors();
    }

    public Set<Entity> getRaiders(){
        return this.raiders;
    }

    public ServerWorld getServerLevel() {
        return serverLevel;
    }

    public enum Status {
        PREPARE,
        RUNNING,
        VICTORY,
        LOSS,
        REMOVING;
    }

}