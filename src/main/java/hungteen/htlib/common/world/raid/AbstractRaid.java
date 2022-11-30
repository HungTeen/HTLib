package hungteen.htlib.common.world.raid;

import com.google.common.collect.Sets;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.DummyEntityType;
import hungteen.htlib.impl.raid.HTRaidComponents;
import hungteen.htlib.impl.spawn.HTSpawnComponents;
import hungteen.htlib.impl.wave.HTWaveComponents;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.htlib.util.interfaces.IRaid;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 21:57
 *
 * Look at {@link net.minecraft.world.entity.raid.Raid}
 **/
public abstract class AbstractRaid extends DummyEntity implements IRaid {

    private static final MutableComponent CHALLENGE_NAME_COMPONENT = Component.translatable("raid.htlib.name");
    private static final MutableComponent CHALLENGE_WARN = Component.translatable("raid.htlib.too_far_away").withStyle(ChatFormatting.RED);
    private final ServerBossEvent progressBar = new ServerBossEvent(CHALLENGE_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    public final ResourceLocation raidLocation;
    protected RaidComponent raidComponent;
    protected WaveComponent waveComponent;
    protected List<SpawnComponent> spawnComponents;
    protected DefaultRaid.Status status = DefaultRaid.Status.PREPARE;
    protected int tick = 0;
    protected int stopTick = 0;
    protected int invalidTick = 0;
    protected int currentWave = 0;
    private boolean firstTick = false;
    protected Set<Entity> raiders = new HashSet<>();
    protected Set<UUID> heroes = new HashSet<>();

//    public static DefaultRaid createRaid(ServerLevel level, ResourceLocation location, Vec3 position){
//        return DummyEntityManager.createEntity(level, (id) -> new DefaultRaid(level, id, location, position));
//    }

    public AbstractRaid(DummyEntityType<?> dummyEntityType, ServerLevel serverLevel, int id, ResourceLocation location, Vec3 position) {
        super(dummyEntityType, serverLevel, id, position);
        this.raidLocation = location;
    }

    public AbstractRaid(DummyEntityType<?> dummyEntityType, Level level, CompoundTag tag) {
        super(dummyEntityType, level, tag);
        this.raidLocation = new ResourceLocation(tag.getString("RaidLocation"));
        if(tag.contains("WaveComponent")){
            HTWaveComponents.getCodec().parse(NbtOps.INSTANCE, tag.get("WaveComponent"))
                    .result().ifPresent(wave -> this.waveComponent = wave);
        }
        if(tag.contains("SpawnComponents")){
            HTSpawnComponents.getCodec().listOf().parse(NbtOps.INSTANCE, tag.get("SpawnComponents"))
                    .result().ifPresent(spawns -> this.spawnComponents = spawns);
        }
        if(tag.contains("Position")){
            Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get("Position"))
                    .result().ifPresent(position -> this.position = position);
        }
        if(tag.contains("RaidTick")){
            this.tick = tag.getInt("RaidTick");
        }
        if(tag.contains("StopTick")){
            this.stopTick = tag.getInt("StopTick");
        }
        if(tag.contains("CurrentWave")){
            this.currentWave = tag.getInt("CurrentWave");
        }
        if(tag.contains("RaidStatus")){
            this.status = DefaultRaid.Status.values()[tag.getInt("RaidStatus")];
        }
        if(tag.contains("FirstTick")){
            this.firstTick = tag.getBoolean("FirstTick");
        }
        {
            this.heroes.clear();
            if (tag.contains("HeroesOfTheVillage", 9)) {
                ListTag listTag = tag.getList("HeroesOfTheVillage", 11);
                for(int i = 0; i < listTag.size(); ++i) {
                    this.heroes.add(NbtUtils.loadUUID(listTag.get(i)));
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putString("RaidLocation", this.raidLocation.toString());
        HTWaveComponents.getCodec().encodeStart(NbtOps.INSTANCE, this.getCurrentWave())
                .result().ifPresent(compoundTag -> tag.put("WaveComponent", compoundTag));
        HTSpawnComponents.getCodec().listOf().encodeStart(NbtOps.INSTANCE, this.getSpawnComponents())
                .result().ifPresent(compoundTag -> tag.put("SpawnComponents", compoundTag));
        Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.position)
                .result().ifPresent(compoundTag -> tag.put("Position", compoundTag));
        tag.putInt("RaidTick", this.tick);
        tag.putInt("StopTick", this.stopTick);
        tag.putInt("CurrentWave", this.currentWave);
        tag.putInt("RaidStatus", this.status.ordinal());
        tag.putBoolean("FirstTick", this.firstTick);
        {
            ListTag listTag = new ListTag();
            for(UUID uuid : this.heroes) {
                listTag.add(NbtUtils.createUUID(uuid));
            }
            tag.put("HeroesOfTheVillage", listTag);
        }
        return tag;
    }

    /**
     * {@link DummyEntityManager#tick()}
     */
    @Override
    public void tick() {
        /* Skip Tick */
        if(this.getRaidComponent() == null){
            if(! this.isRemoving() && ++ this.invalidTick >= 100){
                HTLib.getLogger().warn("CustomRaid removing : Missing raid component {} ?", this.raidLocation);
                this.remove();
            }
        } else{
            this.invalidTick = 0;
            if(this.isRemoving() || ! this.getRaidComponent().canTick(this)){
                return;
            }
        }

        /* Exist Check */
//        if(this.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
//            this.remove();
//            return ;
//        }

        if(this.tick % 10 == 5 && ! this.getLevel().players().isEmpty()) {
            this.updatePlayers();
        }
        this.tickProgressBar();

        if(this.isStopping()) {
            // TODO 配置文件
            if(++ this.stopTick >= 100) {
                this.remove();
            }
        }

        switch (this.status){
            case PREPARE -> {
                if(this.tick >= this.getCurrentWave().getPrepareDuration()) {
                    this.waveStart();
                }
            }
            case RUNNING -> {
                this.checkNextWave();
                this.tickWave();
            }
            case LOSS -> {
                if(this.tick >= this.getRaidComponent().getLossDuration()) {
                    this.remove();
                }
            }
            case VICTORY -> {
                if(this.tick >= this.raidComponent.getVictoryDuration()) {
                    this.remove();
                }
            }
            default -> {
                ++ this.tick;
            }
        }

        if(! this.firstTick){
            this.firstTick = true;
            this.getPlayers().forEach(p -> PlayerHelper.playClientSound(p, this.getRaidComponent().getRaidStartSound()));
        }
    }

    /**
     * {@link #tick()}
     */
    protected void tickWave() {
        if(this.getLevel() instanceof ServerLevel){
            this.getSpawnComponents().forEach(spawnComponent -> {
                raiders.addAll(spawnComponent.spawn((ServerLevel) this.getLevel(), this, tick));
            });
        }


//        /* check spawn entities */
//        final List<ISpawnComponent> spawns = this.raidComponent.getSpawns(this.currentWave);
//        while(this.currentSpawn < spawns.size() && this.tick >= spawns.get(this.currentSpawn).getSpawnTick()) {
//            this.spawnEntities(spawns.get(this.currentSpawn++));
//        }

        /* update raiders list */
        Iterator<Entity> it = this.raiders.iterator();
        while(it.hasNext()) {
            Entity entity = it.next();
            if(! entity.isAlive()) {
                it.remove();
            }
        }
    }

    /**
     * {@link #tick()}
     */
    protected void tickProgressBar() {
        switch (this.getStatus()){
            case PREPARE -> {
                this.progressBar.setName(this.raidComponent.getRaidTitle());
                this.progressBar.setProgress(this.tick * 1.0F / this.getCurrentWave().getPrepareDuration());
            }
            case RUNNING -> {
                this.progressBar.setName(this.raidComponent.getRaidTitle().copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", this.raiders.size())));
                if(this.getCurrentWave().getWaveDuration() == 0){
                    this.progressBar.setProgress(1F);
                } else{
                    this.progressBar.setProgress(1 - this.tick * 1.0F / this.getCurrentWave().getWaveDuration());
                }
            }
            case VICTORY -> {
                this.progressBar.setName(this.raidComponent.getRaidTitle().copy().append(" - ").append(this.raidComponent.getVictoryTitle()));
                this.progressBar.setProgress(1F);
            }
            case LOSS -> {
                this.progressBar.setName(this.raidComponent.getRaidTitle().copy().append(" - ").append(this.raidComponent.getLossTitle()));
                this.progressBar.setProgress(1F);
            }
            default -> {
                this.progressBar.setColor(this.raidComponent.getRaidColor());
            }
        }
    }

    /**
     * player who is alive and in suitable range can be tracked.
     */
    private Predicate<ServerPlayer> validPlayer() {
        return (player) -> {
            final double range = Objects.requireNonNull(this.getRaidComponent()).getRaidRange();
            return player.isAlive() && Math.abs(player.getX() - this.position.x) < range
                    && Math.abs(player.getZ() - this.position.z) < range;
        };
    }

    /**
     * {@link #tickProgressBar()}
     */
    protected void updatePlayers() {
        /* Update Progress Bar */
        if(this.getLevel() instanceof ServerLevel){
            final Set<ServerPlayer> oldPlayers = Sets.newHashSet(this.progressBar.getPlayers());
            final Set<ServerPlayer> newPlayers = Sets.newHashSet(((ServerLevel) this.getLevel()).getPlayers(this.validPlayer()));
            /* add new join players */
            newPlayers.forEach(p -> {
                if(! oldPlayers.contains(p)) {
                    this.progressBar.addPlayer(p);
                }
            });

            /* remove offline players */
            oldPlayers.forEach(p -> {
                if(! newPlayers.contains(p)) {
                    this.progressBar.removePlayer(p);
                }
            });
        }

        /* add heroes */
        this.progressBar.getPlayers().forEach(p -> {
            this.heroes.add(p.getUUID());
        });

        if(this.progressBar.getPlayers().isEmpty()){
            if(! this.isStopping()) {
                ++ this.stopTick;
                this.heroes.forEach(uuid -> {
                    Player player = this.getLevel().getPlayerByUUID(uuid);
                    if(player != null) {
                        PlayerHelper.sendMsgTo(player, CHALLENGE_WARN);
                    }
                });
            }
        } else{
            this.stopTick = 0;
        }
    }

    /**
     * run when prepare time is finished.
     */
    protected void waveStart() {
        this.tick = 0;
        this.setStatus(Status.RUNNING);
        this.getPlayers().forEach(p -> {
            if(Objects.requireNonNull(this.getRaidComponent()).showRoundTitle()){
                PlayerHelper.sendTitleToPlayer(p, Component.translatable("challenge.pvz.round", this.currentWave + 1).withStyle(ChatFormatting.DARK_RED));
            }
            PlayerHelper.playClientSound(p, this.getRaidComponent().getWaveStartSound());
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
        if (this.getCurrentWave().getWaveDuration() == 0 || this.tick >= this.getCurrentWave().getWaveDuration()) {
            if (this.canNextWave()) {
                this.nextWave();
            } else {
                this.onLoss();
            }
        } else {
            if (this.getCurrentWave().canSkip() && this.canNextWave() && this.getSpawnComponents().stream().allMatch(spawnComponent -> spawnComponent.finishedSpawn(this.tick))) {
                this.nextWave();
            }
        }
    }

    protected void nextWave(){
        if(++ this.currentWave >= Objects.requireNonNull(this.getRaidComponent()).getWaveCount(this)) {
            this.onVictory();
        } else {
            this.tick = 0;
            this.setStatus(Status.PREPARE);
        }
    }

    /**
     * run when raid is not defeated.
     */
    protected void onLoss() {
        this.tick = 0;
        this.setStatus(Status.LOSS);
//        this.getPlayers().forEach(p -> PlayerUtil.playClientSound(p, this.raidComponent.getLossSound()));
//        MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidLossEvent(this));
    }

    /**
     * run when raid is defeated.
     */
    protected void onVictory() {
        this.tick = 0;
        this.setStatus(Status.VICTORY);
//        this.getPlayers().forEach(p -> {
//            PlayerUtil.playClientSound(p, this.raidComponent.getWinSound());
//            ChallengeTrigger.INSTANCE.trigger(p, this.raidLocation.toString());
//        });
//        if(! MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidWinEvent(this))) {
//            this.getPlayers().forEach(p -> {
//                this.raidComponent.getRewards().forEach(r -> r.reward(p));
//            });
//            this.raidComponent.getRewards().forEach(r -> r.rewardGlobally(this));
//        }
    }

    public void remove() {
        this.setStatus(Status.REMOVING);
        this.progressBar.removeAllPlayers();
        this.raiders.forEach(Entity::discard);
    }

    public boolean isRaider(Entity raider) {
        return this.raiders.contains(raider);
    }

    public boolean isStopping() {
        return this.stopTick > 0;
    }

    public boolean isRemoving(){
        return this.getStatus() == Status.REMOVING;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    /**
     * Get raid component by resource, use cache to speed up.
     */
    @Nullable
    public RaidComponent getRaidComponent() {
        return this.raidComponent != null ? this.raidComponent : (this.raidComponent = HTRaidComponents.getRaidComponent(this.raidLocation));
    }

    @NotNull
    public WaveComponent getCurrentWave(){
        return this.waveComponent != null ? this.waveComponent : (this.waveComponent = Objects.requireNonNull(this.getRaidComponent()).getCurrentWave(this, this.currentWave));
    }

    @NotNull
    public List<SpawnComponent> getSpawnComponents(){
        return this.spawnComponents != null ? this.spawnComponents : (this.spawnComponents = this.getCurrentWave().getWaveSpawns(this, this.currentWave));
    }

    @Override
    public Function<SpawnComponent, PlaceComponent> getPlaceComponent(){
        return spawnComponent -> {
            return spawnComponent.getSpawnPlacement() != null ? spawnComponent.getSpawnPlacement() :
                    getCurrentWave().getSpawnPlacement() != null ? getCurrentWave().getSpawnPlacement() :
                            Objects.requireNonNull(getRaidComponent()).getSpawnPlacement();
        };
    }

    /**
     * Get tracked players by raid bar.
     */
    public List<ServerPlayer> getPlayers(){
        return this.progressBar.getPlayers().stream().toList();
    }

    public Set<Entity> getRaiders(){
        return this.raiders;
    }

    public enum Status {
        PREPARE,
        RUNNING,
        VICTORY,
        LOSS,
        REMOVING;
    }
}
