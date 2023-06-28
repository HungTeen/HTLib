package hungteen.htlib.common.world.raid;

import com.google.common.collect.Sets;
import hungteen.htlib.HTLib;
import hungteen.htlib.api.interfaces.raid.*;
import hungteen.htlib.common.capability.raid.RaidCapability;
import hungteen.htlib.common.event.events.RaidEvent;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.common.world.entity.DummyEntityType;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.PlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 21:57
 * <p>
 * Look at {@link net.minecraft.world.entity.raid.Raid}
 **/
public abstract class AbstractRaid extends DummyEntity implements IRaid {

    public static final MutableComponent RAID_TITLE = Component.translatable("raid.htlib.title");
    public static final MutableComponent RAID_VICTORY_TITLE = Component.translatable("raid.htlib.victory_title");
    public static final MutableComponent RAID_LOSS_TITLE = Component.translatable("raid.htlib.loss_title");
    public static final MutableComponent RAID_WARN = Component.translatable("raid.htlib.too_far_away").withStyle(ChatFormatting.RED);
    private final ServerBossEvent progressBar = new ServerBossEvent(RAID_TITLE, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    public final ResourceLocation raidLocation;
    protected IRaidComponent raidComponent;
    protected IWaveComponent waveComponent;
    protected List<ISpawnComponent> spawnComponents;
    protected Status status = Status.PREPARE;
    protected final Set<Entity> raiderSet = Sets.newHashSet();
    protected int tick = 0;
    protected int stopTick = 0;
    protected int invalidTick = 0;
    protected int currentWave = 0;
    private boolean firstTick = false;

    public AbstractRaid(DummyEntityType<?> dummyEntityType, ServerLevel serverLevel, int id, ResourceLocation location, Vec3 position) {
        super(dummyEntityType, serverLevel, id, position);
        this.raidLocation = location;
    }

    public AbstractRaid(DummyEntityType<?> dummyEntityType, Level level, CompoundTag tag) {
        super(dummyEntityType, level, tag);
        this.raidLocation = new ResourceLocation(tag.getString("RaidLocation"));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("WaveComponent")) {
            HTWaveComponents.getCodec().parse(NbtOps.INSTANCE, tag.get("WaveComponent"))
                    .result().ifPresent(wave -> this.waveComponent = wave);
        }
        if (tag.contains("SpawnComponents")) {
            HTSpawnComponents.getCodec().listOf().parse(NbtOps.INSTANCE, tag.get("SpawnComponents"))
                    .result().ifPresent(spawns -> this.spawnComponents = spawns);
        }
        if (tag.contains("Position")) {
            Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get("Position"))
                    .result().ifPresent(position -> this.position = position);
        }
        if (tag.contains("RaidTick")) {
            this.tick = tag.getInt("RaidTick");
        }
        if (tag.contains("StopTick")) {
            this.stopTick = tag.getInt("StopTick");
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
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("RaidLocation", this.raidLocation.toString());
        if(this.getRaidComponent() != null){
            HTWaveComponents.getCodec().encodeStart(NbtOps.INSTANCE, this.getCurrentWave())
                    .result().ifPresent(compoundTag -> tag.put("WaveComponent", compoundTag));
            HTSpawnComponents.getCodec().listOf().encodeStart(NbtOps.INSTANCE, this.getSpawnComponents())
                    .result().ifPresent(compoundTag -> tag.put("SpawnComponents", compoundTag));
        }
        Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.position)
                .result().ifPresent(compoundTag -> tag.put("Position", compoundTag));
        tag.putInt("RaidTick", this.tick);
        tag.putInt("StopTick", this.stopTick);
        tag.putInt("CurrentWave", this.currentWave);
        tag.putInt("RaidStatus", this.status.ordinal());
        tag.putBoolean("FirstTick", this.firstTick);
        return super.save(tag);
    }

    /**
     * Check if the specified raid can continue ticking.
     *
     * @return true if the raid can tick.
     */
    public boolean canTick() {
        return this.getLevel().hasChunkAt(MathHelper.toBlockPos(this.position));
    }

    /**
     * {@link DummyEntityManager#tick()}
     */
    @Override
    public void tick() {
        if (!this.isRemoved() && this.canTick()) {
            /* Skip Tick */
            if (this.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
                this.remove();
                return;
            }
            if (this.getRaidComponent() == null) {
                if (++this.invalidTick >= 100) {
                    HTLib.getLogger().warn("CustomRaid removing : Missing raid component {} ?", this.raidLocation);
                    this.remove();
                }
                return;
            } else {
                this.invalidTick = 0;
            }

            if (this.tick % 20 == 0 || this.stopTick % 10 == 5) {
                this.updatePlayers();
                this.updateRaiders();
            }
            this.tickProgressBar();

            if (this.isStopping()) {
                // TODO 配置文件
                if (++this.stopTick >= 200) {
                    this.remove();
                }
                return;
            }

            if (!this.firstTick) {
                this.firstTick = true;
                this.getPlayers().forEach(p -> {
                    this.getRaidComponent().getRaidStartSound().ifPresent(sound -> PlayerHelper.playClientSound(p, sound));
                });
            }

            switch (this.getStatus()) {
                case PREPARE -> {
                    if (++ this.tick >= this.getCurrentWave().getPrepareDuration()) {
                        this.waveStart();
                    }
                }
                case RUNNING -> {
                    this.checkNextWave();
                    this.checkSpawn();
                    ++ this.tick;
                }
                case LOSS -> {
                    this.getRaidComponent().getResultComponents().stream().forEach(this::tickResult);
                    if (++ this.tick >= this.getRaidComponent().getLossDuration()) {
                        this.remove();
                    }
                }
                case VICTORY -> {
                    this.getRaidComponent().getResultComponents().stream().forEach(this::tickResult);
                    if (++ this.tick >= this.getRaidComponent().getVictoryDuration()) {
                        this.remove();
                    }
                }
            }
        }
    }

    /**
     * Spawn raiders when time met.
     * {@link #tick()}
     */
    protected void checkSpawn() {
        if (this.getLevel() instanceof ServerLevel) {
            this.getSpawnComponents().forEach(spawnComponent -> {
                spawnComponent.getSpawnEntities((ServerLevel) this.getLevel(), this, tick).forEach(raider -> {
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
        switch (this.getStatus()) {
            case PREPARE -> {
                this.progressBar.setName(this.getRaidComponent().getRaidTitle());
                this.progressBar.setProgress(this.tick * 1.0F / this.getCurrentWave().getPrepareDuration());
            }
            case RUNNING -> {
                this.progressBar.setName(this.getRaidComponent().getRaidTitle().copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", this.getTotalRaidersAlive())));
                if (this.getCurrentWave().getWaveDuration() == 0) {
                    this.progressBar.setProgress(Mth.clamp(this.getProgressPercent(), 0.0F, 1.0F));
                } else {
                    this.progressBar.setProgress(Mth.clamp(1 - this.tick * 1.0F / this.getCurrentWave().getWaveDuration(), 0.0F, 1.0F));
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
            if (!this.isStopping()) {
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
                if (raider.isAlive() && raider.level.dimension() == this.getLevel().dimension()) {
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
    protected void waveStart() {
        this.tick = 0;
        this.setStatus(Status.RUNNING);
        this.getPlayers().forEach(p -> {
            if (Objects.requireNonNull(this.getRaidComponent()).showRoundTitle()) {
                PlayerHelper.sendTitleToPlayer(p, Component.translatable("raid.htlib.round", this.currentWave + 1).withStyle(ChatFormatting.DARK_RED));
            }
            if (this.getCurrentWave().getWaveStartSound().isPresent()) {
                PlayerHelper.playClientSound(p, this.getCurrentWave().getWaveStartSound().get());
            } else if (this.getRaidComponent().getWaveStartSound().isPresent()) {
                PlayerHelper.playClientSound(p, this.getRaidComponent().getWaveStartSound().get());
            }
        });
        MinecraftForge.EVENT_BUS.post(new RaidEvent.RaidWaveStartEvent(this.getLevel(), this, this.currentWave));
        this.setDirty();
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

    protected void nextWave() {
        if (++this.currentWave >= Objects.requireNonNull(this.getRaidComponent()).getWaveCount(this)) {
            this.onVictory();
        } else {
            this.tick = 0;
            this.updateWaveAndSpawn();
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
            this.getDefenders().stream().forEach(entity -> {
                result.applyToDefender(this, entity, tick);
            });
            this.getRaiders().stream().forEach(entity -> {
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
//        this.raiders.forEach(Entity::discard);
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

    public boolean isStopping() {
        return this.stopTick > 0;
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

    /**
     * Get raid component by resource, use cache to speed up.
     */
    @Nullable
    public IRaidComponent getRaidComponent() {
        return this.raidComponent != null ? this.raidComponent : (this.raidComponent = HTRaidComponents.getRaidComponent(this.raidLocation));
    }

    @NotNull
    public IWaveComponent getCurrentWave() {
        return this.waveComponent != null ? this.waveComponent : (this.waveComponent = Objects.requireNonNull(this.getRaidComponent()).getCurrentWave(this, this.currentWave));
    }

    @NotNull
    public void updateWaveAndSpawn() {
        this.waveComponent = Objects.requireNonNull(this.getRaidComponent()).getCurrentWave(this, this.currentWave);
        this.spawnComponents = this.getCurrentWave().getWaveSpawns(this, this.currentWave);
        this.setDirty();
    }

    @NotNull
    public List<ISpawnComponent> getSpawnComponents() {
        return this.spawnComponents != null ? this.spawnComponents : (this.spawnComponents = this.getCurrentWave().getWaveSpawns(this, this.currentWave));
    }

    @Override
    public Function<ISpawnComponent, IPositionComponent> getPlaceComponent() {
        return spawnComponent -> {
            return spawnComponent.getSpawnPlacement().isPresent() ? spawnComponent.getSpawnPlacement().get() :
                    getCurrentWave().getSpawnPlacement().isPresent() ? getCurrentWave().getSpawnPlacement().get() :
                            Objects.requireNonNull(getRaidComponent()).getSpawnPlacement();
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
