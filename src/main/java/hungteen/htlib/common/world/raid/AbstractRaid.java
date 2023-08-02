package hungteen.htlib.common.world.raid;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.raid.*;
import hungteen.htlib.common.capability.raid.RaidCapability;
import hungteen.htlib.common.event.events.RaidEvent;
import hungteen.htlib.common.impl.position.HTPositionComponents;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityType;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 21:57
 * <p>
 * Look at {@link net.minecraft.world.entity.raid.Raid}
 **/
public abstract class AbstractRaid extends DummyEntity implements IRaid {

    public static final String RAID_TAG = "RaidTag";
    public static final MutableComponent RAID_TITLE = Component.translatable("raid.htlib.title");
    public static final MutableComponent RAID_VICTORY_TITLE = Component.translatable("raid.htlib.victory_title");
    public static final MutableComponent RAID_LOSS_TITLE = Component.translatable("raid.htlib.loss_title");
    public static final MutableComponent RAID_WARN = Component.translatable("raid.htlib.too_far_away").withStyle(ChatFormatting.RED);
    private final ServerBossEvent progressBar = new ServerBossEvent(RAID_TITLE, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    protected CompoundTag raidTag = new CompoundTag();
    protected IRaidComponent raidComponent;
    protected IWaveComponent waveComponent;
    protected List<Pair<Integer, ISpawnComponent>> spawnComponents = List.of();
    protected Status status = Status.PREPARE;
    protected final Set<Entity> raiderSet = Sets.newHashSet();
    protected int tick = 0;
    protected int invalidTick = 0;
    protected int currentWave = 0;
    private boolean firstTick = false;
    protected boolean stopped = false;
    protected int stopTick = 0;

    public AbstractRaid(DummyEntityType<?> dummyEntityType, ServerLevel serverLevel, Vec3 position, IRaidComponent raidComponent) {
        super(dummyEntityType, serverLevel, position);
        CodecHelper.encodeNbt(HTRaidComponents.getDirectCodec(), raidComponent)
                .result().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).ifPresent(tag -> this.raidTag = tag);
    }

    public AbstractRaid(DummyEntityType<?> dummyEntityType, Level level, CompoundTag raidTag) {
        super(dummyEntityType, level, raidTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains(RAID_TAG)){
            this.raidTag = tag.getCompound(RAID_TAG);
        }
        if (tag.contains("WaveComponent")) {
            HTWaveComponents.getDirectCodec().parse(NbtOps.INSTANCE, tag.get("WaveComponent"))
                    .result().ifPresent(wave -> this.waveComponent = wave);
        }
        if (tag.contains("SpawnComponents")) {
            HTSpawnComponents.pairDirectCodec().listOf().parse(NbtOps.INSTANCE, tag.get("SpawnComponents"))
                    .result().ifPresent(spawns -> this.spawnComponents = spawns);
        }
        if (tag.contains("Position")) {
            Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get("Position"))
                    .result().ifPresent(position -> this.position = position);
        }
        if (tag.contains("RaidTick")) {
            this.tick = tag.getInt("RaidTick");
        }
        if (tag.contains("CurrentWave")) {
            this.currentWave = tag.getInt("CurrentWave");
        }
        if (tag.contains("RaidStatus")) {
            this.status = Status.values()[tag.getInt("RaidStatus")];
        }
        if (tag.contains("FirstTick")) {
            this.firstTick = tag.getBoolean("FirstTick");
        }
        if (tag.contains("Stopped")) {
            this.stopped = tag.getBoolean("Stopped");
        }
        if (tag.contains("StopTick")) {
            this.stopTick = tag.getInt("StopTick");
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put(RAID_TAG, this.raidTag);
        this.getCurrentWave().flatMap(wave -> CodecHelper.encodeNbt(HTWaveComponents.getDirectCodec(), wave)
                .result()).ifPresent(compoundTag -> tag.put("WaveComponent", compoundTag));
        HTSpawnComponents.pairDirectCodec().listOf().encodeStart(NbtOps.INSTANCE, this.getCurrentSpawns())
                .result().ifPresent(compoundTag -> tag.put("SpawnComponents", compoundTag));
        Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.position)
                .result().ifPresent(compoundTag -> tag.put("Position", compoundTag));
        tag.putInt("RaidTick", this.tick);
        tag.putInt("CurrentWave", this.currentWave);
        tag.putInt("RaidStatus", this.status.ordinal());
        tag.putBoolean("FirstTick", this.firstTick);
        tag.putBoolean("Stopped", this.stopped);
        tag.putInt("StopTick", this.stopTick);
        return super.save(tag);
    }

    /**
     * Check if the specified raid can continue ticking.
     * @return true if the raid can tick.
     */
    public boolean canTick() {
        return this.getLevel().hasChunkAt(MathHelper.toBlockPos(this.position));
    }

    @Override
    public void tick() {
        if (!this.isRemoved() && this.canTick()) {
            // Skip Tick.
            if (this.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
                this.remove();
                return;
            }
            // Assert Raid Component.
            if(this.checkValidAndRun(() -> this.getRaidComponent() == null)) return;
            // Init Wave.
            if(this.getCurrentWave().isEmpty() && this.currentWave == 0){
                this.updateWave(false);
            }
            // Assert Wave Component.
            if(this.checkValidAndRun(this.getCurrentWave()::isEmpty)) return;
            final IWaveComponent wave = this.getCurrentWave().get();
            if (this.tick % 20 == 0 || this.stopTick % 10 == 5) {
                this.updatePlayers();
                this.updateRaiders();
            }
            this.tickProgressBar();

            if (this.stopped()) {
                // TODO 配置文件
                if (++ this.stopTick >= 200) {
                    this.remove();
                }
                return;
            }

            if (!this.firstTick) {
                this.firstTick = true;
                this.getPlayers().forEach(p -> {
                    Objects.requireNonNull(this.getRaidComponent()).getRaidStartSound().ifPresent(sound -> PlayerHelper.playClientSound(p, sound));
                });
            }

            switch (this.getStatus()) {
                case PREPARE -> {
                    if (++ this.tick >= wave.getPrepareDuration()) {
                        this.waveStart(wave);
                    }
                }
                case RUNNING -> {
                    this.checkNextWave(wave);
                    this.checkSpawn();
                    ++ this.tick;
                }
                case LOSS -> {
                    Objects.requireNonNull(this.getRaidComponent()).getResultComponents().forEach(this::tickResult);
                    if (++ this.tick >= this.getRaidComponent().getLossDuration()) {
                        this.remove();
                    }
                }
                case VICTORY -> {
                    Objects.requireNonNull(this.getRaidComponent()).getResultComponents().forEach(this::tickResult);
                    if (++ this.tick >= this.getRaidComponent().getVictoryDuration()) {
                        this.remove();
                    }
                }
            }
        }
    }

    /**
     * 此袭击无效，表现在袭击组件非法。
     */
    protected boolean checkValidAndRun(Supplier<Boolean> supplier){
        final boolean ans = supplier.get();
        if (ans) {
            if (++ this.invalidTick >= 100) {
                HTLib.getLogger().warn("Custom Raid Removing : Missing raid component !");
                this.remove();
            }
        } else {
            this.invalidTick = 0;
        }
        return ans;
    }

    /**
     * Spawn raiders when time met.
     * {@link #tick()}
     */
    protected void checkSpawn() {
        if (this.getLevel() instanceof ServerLevel) {
            this.getCurrentSpawns().forEach(pair -> {
                pair.getSecond().getSpawnEntities((ServerLevel) this.getLevel(), this, tick, pair.getFirst()).forEach(raider -> {
                    joinRaid(this.currentWave, raider);
                });
            });
        }
    }

    public void joinRaid(int wave, Entity raider) {
        boolean success = this.addRaider(raider);
        if (success) {
            RaidCapability.getRaid(raider).ifPresent(raidCapability -> {
                raidCapability.setRaid(this);
                raidCapability.setWave(wave);
                raider.setOnGround(true);
            });
        }
    }

    /**
     * {@link #tick()}
     */
    protected void tickProgressBar() {
        if(this.getRaidComponent() == null) return;
        this.getCurrentWave().ifPresent(wave -> {
            switch (this.getStatus()) {
                case PREPARE -> {
                    this.progressBar.setName(this.getRaidComponent().getRaidTitle());
                    this.progressBar.setProgress(this.tick * 1.0F / wave.getPrepareDuration());
                }
                case RUNNING -> {
                    this.progressBar.setName(this.getRunningTitle(this.getRaidComponent().getRaidTitle().copy()));
                    if (wave.getWaveDuration() == 0) {
                        this.progressBar.setProgress(Mth.clamp(this.getProgressPercent(), 0.0F, 1.0F));
                    } else {
                        this.progressBar.setProgress(Mth.clamp(1 - this.tick * 1.0F / wave.getWaveDuration(), 0.0F, 1.0F));
                    }
                }
                case VICTORY -> {
                    this.progressBar.setName(this.getRaidComponent().getRaidTitle().copy().append(" - ").append(this.getRaidComponent().getVictoryTitle()));
                    this.progressBar.setProgress(1F);
                }
                case LOSS -> {
                    this.progressBar.setName(this.getRaidComponent().getRaidTitle().copy().append(" - ").append(this.getRaidComponent().getLossTitle()));
                    this.progressBar.setProgress(1F);
                }
            }
            this.progressBar.setColor(this.getRaidComponent().getBarColor());
        });
    }

    protected MutableComponent getRunningTitle(MutableComponent title){
        return title.append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", this.getTotalRaidersAlive()));
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
        if (this.getLevel() instanceof ServerLevel) {
            final Set<ServerPlayer> oldPlayers = Sets.newHashSet(this.progressBar.getPlayers());
            final Set<ServerPlayer> newPlayers = Sets.newHashSet(((ServerLevel) this.getLevel()).getPlayers(this.validPlayer()));
            /* add new join players */
            newPlayers.forEach(p -> {
                if (!oldPlayers.contains(p)) {
                    this.progressBar.addPlayer(p);
                }
            });

            /* remove offline players */
            oldPlayers.forEach(p -> {
                if (!newPlayers.contains(p)) {
                    this.progressBar.removePlayer(p);
                }
            });
        }

        /* No player nearby */
        if (this.progressBar.getPlayers().isEmpty()) {
            if (!this.stopped()) {
                ++this.stopTick;
                this.getDefenders().stream().filter(Player.class::isInstance).map(Player.class::cast).forEach(player -> {
                    PlayerHelper.sendMsgTo(player, RAID_WARN);
                });
            }
        } else {
            this.stopTick = 0;
        }
    }

    protected void updateRaiders() {
        if (this.getLevel() instanceof ServerLevel) {
            Iterator<Entity> iterator = this.raiderSet.iterator();
            Set<Entity> set = Sets.newHashSet();

            while (iterator.hasNext()) {
                Entity raider = iterator.next();
                if (raider.isAlive() && EntityHelper.inDimension(raider, this.getLevel().dimension())) {
                    if (raider.tickCount > 600) {
                        if (((ServerLevel) this.getLevel()).getEntity(raider.getUUID()) == null) {
                            set.add(raider);
                        }
                    }
                } else {
                    set.add(raider);
                }
            }

            for (Entity raider1 : set) {
                this.removeFromRaid(raider1);
            }
        }
    }

    @Override
    public boolean addRaider(Entity raider) {
        Entity dupRaider = null;

        for (Entity raider1 : this.raiderSet) {
            if (raider1.getUUID().equals(raider.getUUID())) {
                dupRaider = raider1;
                break;
            }
        }
        /* Replace Old */
        if (dupRaider != null) {
            this.raiderSet.remove(dupRaider);
            this.raiderSet.add(raider);
        }

        this.raiderSet.add(raider);

        this.updateProgress();
        this.setDirty();
        return true;
    }

    public void updateProgress() {
        tickProgressBar();
    }

    public float getProgressPercent() {
        float health = 0.0F;
        float maxHealth = 0.0F;

        for (Entity raider : this.raiderSet) {
            if (raider instanceof LivingEntity) {
                health += ((LivingEntity) raider).getHealth();
                maxHealth += ((LivingEntity) raider).getMaxHealth();
            }
        }

        return maxHealth == 0 ? 0 : health / maxHealth;
    }

    /**
     * run when prepare time is finished.
     */
    protected void waveStart(IWaveComponent wave) {
        this.tick = 0;
        this.setStatus(Status.RUNNING);
        this.getPlayers().forEach(p -> {
            if (Objects.requireNonNull(this.getRaidComponent()).showRoundTitle()) {
                this.sendWaveTitle(p);
            }
            if (wave.getWaveStartSound().isPresent()) {
                PlayerHelper.playClientSound(p, wave.getWaveStartSound().get());
            } else if (this.getRaidComponent().getWaveStartSound().isPresent()) {
                PlayerHelper.playClientSound(p, this.getRaidComponent().getWaveStartSound().get());
            }
        });
        MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidWaveStartEvent(this.getLevel(), this, this.currentWave));
        this.setDirty();
    }

    protected void sendWaveTitle(Player player){
        PlayerHelper.sendTitleToPlayer(player, Component.translatable("raid.htlib.round", this.currentWave + 1).withStyle(ChatFormatting.DARK_RED));
    }

    /**
     * check can start next wave or not.
     */
    public boolean canNextWave() {
        return this.getTotalRaidersAlive() == 0;
    }

    public int getTotalRaidersAlive() {
        return this.raiderSet.size();
    }

    /**
     * {@link #tick()}
     */
    protected void checkNextWave(IWaveComponent wave) {
        if (wave.getWaveDuration() == 0 || this.tick >= wave.getWaveDuration()) {
            if (this.canNextWave()) {
                this.nextWave();
            } else {
                this.onLoss();
            }
        } else {
            if (wave.canSkip() && this.canNextWave() && this.getCurrentSpawns().stream().allMatch(pair -> {
                return pair.getSecond().finishedSpawn(this.tick, pair.getFirst());
            })) {
                this.nextWave();
            }
        }
    }

    protected void nextWave() {
        if (++this.currentWave >= Objects.requireNonNull(this.getRaidComponent()).getWaveCount(this)) {
            this.onVictory();
        } else {
            this.tick = 0;
            this.updateWave(true);
            this.setStatus(Status.PREPARE);
            MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidWaveFinishEvent(this.getLevel(), this, this.currentWave));
        }
        this.setDirty();
    }

    /**
     * {@link #tick()}
     */
    protected void tickResult(IResultComponent result){
        if(this.getLevel() instanceof ServerLevel){
            result.apply(this, (ServerLevel) this.getLevel(), tick);
            this.getDefenders().forEach(entity -> {
                result.applyToDefender(this, entity, tick);
            });
            this.getRaiders().forEach(entity -> {
                result.applyToRaider(this, entity, tick);
            });
        }
    }

    /**
     * run when raid is not defeated.
     */
    protected void onLoss() {
        this.tick = -1;
        this.setStatus(Status.LOSS);
        this.setDirty();
        this.getPlayers().forEach(p -> {
            Objects.requireNonNull(this.getRaidComponent()).getLossSound().ifPresent(sound -> PlayerHelper.playClientSound(p, sound));
        });
        MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidLostEvent(this.getLevel(), this));
    }

    /**
     * run when raid is defeated.
     */
    protected void onVictory() {
        this.tick = -1;
        this.setStatus(Status.VICTORY);
        this.setDirty();
        this.getPlayers().forEach(p -> {
            Objects.requireNonNull(this.getRaidComponent()).getVictorySound().ifPresent(sound -> PlayerHelper.playClientSound(p, sound));
        });
        this.getDefenders().stream().filter(ServerPlayer.class::isInstance).filter(Predicate.not(Entity::isSpectator)).map(ServerPlayer.class::cast).forEach(serverPlayer -> {
                serverPlayer.awardStat(Stats.RAID_WIN);
                CriteriaTriggers.RAID_WIN.trigger(serverPlayer);
                //TODO 自己的Trigger
//            ChallengeTrigger.INSTANCE.trigger(p, this.raidLocation.toString());
        });
        MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidDefeatedEvent(this.getLevel(), this));
    }

    public void removeFromRaid(Entity raider) {
        boolean flag = this.raiderSet.remove(raider);
        if (flag) {
            RaidCapability.getRaid(raider).ifPresent(raidCapability -> {
                raidCapability.setRaid(null);
            });
            this.updateProgress();
            this.setDirty();
        }
    }

    /**
     * Remove from world.
     */
    public void remove() {
        this.setRemoved();
        this.progressBar.removeAllPlayers();
    }

    @Override
    public boolean blockInsideStuffs() {
        return this.getRaidComponent() != null && this.getRaidComponent().blockInside();
    }

    @Override
    public boolean blockOutsideStuffs() {
        return this.getRaidComponent() != null && this.getRaidComponent().blockOutside();
    }

    @Override
    public boolean renderBorder() {
        return this.getRaidComponent() != null && this.getRaidComponent().renderBorder();
    }

    @Override
    public int getBorderColor() {
        return this.getRaidComponent() != null ? this.getRaidComponent().getBorderColor() : super.getBorderColor();
    }

    @Override
    public double getWidth() {
        return this.getRaidComponent() == null ? super.getWidth() : this.getRaidComponent().getRaidRange() * 2;
    }

    public int getTick(){
        return this.tick;
    }

    public int getRound(){
        return this.currentWave;
    }

    public int getTotalRound(){
        return this.getRaidComponent() == null ? 0 : this.getRaidComponent().getWaveCount(this);
    }

    public boolean stopped() {
        return this.stopped;
    }

    public boolean isRunning() {
        return this.getStatus() == Status.RUNNING;
    }

    public boolean isPreparing(){
        return this.getStatus() == Status.PREPARE;
    }

    public boolean isLost(){
        return this.getStatus() == Status.LOSS;
    }

    public boolean isDefeated(){
        return this.getStatus() == Status.VICTORY;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void updateWave(boolean overwrite) {
        if(this.getRaidComponent() != null){
            if(overwrite || this.getCurrentWave().isEmpty()){
                this.setCurrentWave(this.getRaidComponent().getCurrentWave(this, this.currentWave));
            }
            this.setDirty();
        }
    }

    /**
     * Get raid component by resource, use cache to speed up.
     */
    @Nullable
    public IRaidComponent getRaidComponent() {
        if(this.raidComponent == null){
            CodecHelper.parse(HTRaidComponents.getDirectCodec(), this.raidTag)
                    .result().ifPresent(c -> this.raidComponent = c);
        }
        return this.raidComponent;
    }

    @NotNull
    public Optional<IWaveComponent> getCurrentWave() {
        return Optional.ofNullable(this.waveComponent);
    }

    public void setCurrentWave(@NotNull IWaveComponent wave) {
        this.waveComponent = wave;
        this.spawnComponents = wave.getWaveSpawns(this, this.currentWave, this.getLevel().getRandom());
    }

    @NotNull
    public List<Pair<Integer, ISpawnComponent>> getCurrentSpawns() {
        return this.spawnComponents;
    }

    public void setCurrentSpawns(List<Pair<Integer, ISpawnComponent>> spawns) {
        this.spawnComponents = spawns;
    }

    @Override
    public Function<ISpawnComponent, IPositionComponent> getPlaceComponent() {
        return spawnComponent -> {
            if(spawnComponent.getSpawnPlacement().isPresent()) {
                return spawnComponent.getSpawnPlacement().get();
            } else if(getCurrentWave().isPresent()){
                if(getCurrentWave().get().getSpawnPlacement().isPresent()) {
                    return getCurrentWave().get().getSpawnPlacement().get();
                } else if(getRaidComponent() != null && getRaidComponent().getSpawnPlacement().isPresent()){
                    return getRaidComponent().getSpawnPlacement().get();
                }
            }
            return HTPositionComponents.DEFAULT;
        };
    }

    /**
     * Get tracked players by raid bar.
     */
    public List<ServerPlayer> getPlayers() {
        return this.progressBar.getPlayers().stream().toList();
    }

    public List<Entity> getDefenders(){
        return this.getPlayers().stream().map(Entity.class::cast).toList();
    }

    public List<Entity> getRaiders(){
        return this.raiderSet.stream().toList();
    }

    public enum Status {
        PREPARE,
        RUNNING,
        VICTORY,
        LOSS;
    }
}
