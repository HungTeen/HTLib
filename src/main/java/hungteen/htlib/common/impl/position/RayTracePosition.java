package hungteen.htlib.common.impl.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IPositionType;
import hungteen.htlib.api.interfaces.raid.IRaid;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/26 8:25
 **/
public class RayTracePosition extends PlaceComponent {

    /**
     * 射线检测失败后，每刻最多尝试的次数。max ray trace attempts per tick when candidates are not full.
     */
    private static final int ATTEMPTS_PER_TICK = 4;
    /**
     * 从候选位置生成时的最大偏移。max offset when placing entity on a candidate.
     */
    private static final int REFRESH_TRIES = 3;
    /**
     * 初始射线的俯角范围（度），正值朝下。pitch range of the initial ray, positive means downward.
     */
    private static final double MIN_PITCH = -45D;
    private static final double MAX_PITCH = 45D;

    /**
     * center_offset 中心点偏移，射线起点相对袭击中心的水平偏移。
     * exclude_radius 排除半径，此半径之内命中不作为候选位置。
     * radius 放置半径，同时也是射线单段的最大长度。
     * height_offset 高度偏移，射线起点的高度，建议为正值（从空中向下检测）。
     * is_circle 默认是圆形范围，否则是方形。
     * reflect_times 最大弹射次数。max reflect times before the ray trace gives up.
     * position_queue_size 候选位置数量上限，达到后仅定期刷新。max amount of cached candidates.
     * refresh_interval 候选位置刷新间隔（刻）。interval of refreshing the full candidate queue.
     */
    public static final Codec<RayTracePosition> CODEC = RecordCodecBuilder.<RayTracePosition>mapCodec(instance -> instance.group(
        Vec3.CODEC.optionalFieldOf("center_offset", Vec3.ZERO).forGetter(RayTracePosition::getCenterOffset),
        Codec.DOUBLE.optionalFieldOf("exclude_radius", 0D).forGetter(RayTracePosition::getExcludeRadius),
        Codec.DOUBLE.fieldOf("radius").forGetter(RayTracePosition::getRadius),
        Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(RayTracePosition::isCircle),
        Codec.INT.optionalFieldOf("reflect_times", 3).forGetter(RayTracePosition::getReflectTimes),
        Codec.intRange(1, 32).optionalFieldOf("position_queue_size", 16).forGetter(RayTracePosition::getPositionQueueSize),
        Codec.intRange(1, 72000).optionalFieldOf("refresh_interval", 200).forGetter(RayTracePosition::getRefreshInterval)
    ).apply(instance, RayTracePosition::new)).codec();

    private final Vec3 centerOffset;
    private final Integer reflectTimes;
    private final Integer positionQueueSize;
    private final Integer refreshInterval;

    public RayTracePosition(Vec3 centerOffset, double excludeRadius, double radius, boolean isCircle,
        Integer reflectTimes, Integer positionQueueSize, Integer refreshInterval){
        super(excludeRadius, radius, isCircle);
        this.centerOffset = centerOffset;
        this.reflectTimes = reflectTimes;
        this.positionQueueSize = positionQueueSize;
        this.refreshInterval = refreshInterval;
    }

    /**
     * 补充袭击的候选位置，候选位置存放在 {@link IRaid} 上且不保存。
     * 候选位置未满时每刻尝试若干次，已满时仅按刷新间隔清空重建。
     */
    @Override
    public void tickPosition(IRaid raid) {
        if (!this.canSpawn() || !(raid.getLevel() instanceof ServerLevel level)) {
            return;
        }
        final List<BlockPos> candidates = raid.getCandidatePositions();
        final Vec3 center = raid.getPosition().add(this.getCenterOffset());
        if (candidates.size() >= this.getPositionQueueSize()) {
            if (level.getGameTime() % this.getRefreshInterval() != 0) {
                return;
            }
            for (int i = 0; i < REFRESH_TRIES; ++i) {
                this.rayTrace(level, center).ifPresent(newPos -> {
                    int randomReplaceIndex = level.getRandom().nextInt(candidates.size());
                    candidates.set(randomReplaceIndex, newPos);
                });
            }
        }
        for (int i = 0; i < ATTEMPTS_PER_TICK && candidates.size() < this.getPositionQueueSize(); ++i) {
            this.rayTrace(level, center).ifPresent(candidates::add);
        }
    }

    @Override
    public Vec3 getPlacePosition(ServerLevel world, Vec3 origin) {
        return Vec3.ZERO;
    }

    /**
     * 优先从袭击缓存的候选位置中随机取一个，并做小范围偏移。
     * 若候选位置已经失效，则将其移除。
     */
    @Override
    public Vec3 getPlacePosition(IRaid raid, ServerLevel world, Vec3 origin) {
        final List<BlockPos> candidates = raid.getCandidatePositions();
        final RandomSource random = world.getRandom();
        final int attempts = candidates.size();
        for (int i = 0; i < attempts && !candidates.isEmpty(); ++i) {
            final BlockPos pos = candidates.get(random.nextInt(candidates.size()));
            if (this.canPlace(world, pos)) {
                return this.toSpawnPosition(pos);
            }
            candidates.remove(pos);
        }
        return this.rayTrace(world, origin.add(this.getCenterOffset()))
            .map(this::toSpawnPosition)
            .orElse(origin);
    }

    @Override
    public IPositionType<?> getType() {
        return HTPositionTypes.RAY_TRACE;
    }

    /**
     * 从起点向随机方向射线检测，命中方块顶面且在范围内时返回可站立的位置，
     * 否则随机弹射并继续检测，弹射次数超过 {@link #getReflectTimes()} 则失败。
     *
     * @param level 现世界。
     * @param center 放置区域中心。
     * @return 命中的可站立位置。
     */
    protected Optional<BlockPos> rayTrace(ServerLevel level, Vec3 center) {
        final RandomSource random = level.getRandom();
        Vec3 from = center;
        Vec3 direction = this.randomDirection(random);
        int reflect = 0;
        while (true) {
            final BlockHitResult result = level.clip(new ClipContext(from, from.add(direction.scale(this.getRadius())),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if (result.getType() == HitResult.Type.MISS) {
                return Optional.empty();
            }
            if (result.getDirection() == Direction.UP) {
                final BlockPos placePos = result.getBlockPos().above();
                if (this.isInArea(center, MathHelper.toVec3(placePos)) && this.canPlace(level, placePos)) {
                    return Optional.of(placePos);
                }
            }
            if (++reflect > this.getReflectTimes()) {
                return Optional.empty();
            }
            direction = this.bounce(random, direction, result.getDirection());
            from = result.getLocation().add(direction.scale(1.0E-4D));
        }
    }

    /**
     * 随机水平方向，带俯角的初始射线方向。
     */
    protected Vec3 randomDirection(RandomSource random) {
        final double yaw = random.nextDouble() * 360D;
        final double pitch = RandomHelper.getMinMax(random, MIN_PITCH, MAX_PITCH);
        return Vec3.directionFromRotation((float) pitch, (float) yaw);
    }

    /**
     * 命中面的法线方向上随机弹射，保证弹射方向离开方块表面。
     */
    protected Vec3 bounce(RandomSource random, Vec3 direction, Direction face) {
        final Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
        Vec3 bounced = direction.add(RandomHelper.vec3Range(random, 0.6D));
        if (bounced.lengthSqr() < 1.0E-4D) {
            bounced = normal;
        } else {
            bounced = bounced.normalize();
            final double dot = bounced.dot(normal);
            if (dot < 0) {
                // 去除进入方块的分量。
                bounced = bounced.subtract(normal.scale(dot));
            }
            // 保证沿法线离开方块。
            bounced = bounced.add(normal.scale(0.2D));
        }
        return bounced.normalize();
    }

    /**
     * 判断该位置是否可以生成实体：下方为完整的支撑面，自身与头部无碰撞且无流体。
     */
    protected boolean canPlace(ServerLevel level, BlockPos pos) {
        final BlockState ground = level.getBlockState(pos.below());
        final BlockState body = level.getBlockState(pos);
        final BlockState head = level.getBlockState(pos.above());
        return Level.isInSpawnableBounds(pos)
            && ground.isFaceSturdy(level, pos.below(), Direction.UP)
            && body.getCollisionShape(level, pos).isEmpty() && body.getFluidState().isEmpty()
            && head.getCollisionShape(level, pos.above()).isEmpty();
    }

    /**
     * 在方块上方做小范围随机偏移。
     */
    protected Vec3 toSpawnPosition(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    }

    public Vec3 getCenterOffset() {
        return centerOffset;
    }

    public Integer getReflectTimes() {
        return reflectTimes;
    }

    public Integer getPositionQueueSize() {
        return positionQueueSize;
    }

    public Integer getRefreshInterval() {
        return refreshInterval;
    }
}
