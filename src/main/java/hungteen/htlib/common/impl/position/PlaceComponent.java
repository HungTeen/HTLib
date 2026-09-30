package hungteen.htlib.common.impl.position;

import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:22
 **/
public abstract class PlaceComponent implements IPositionComponent {

    private final double excludeRadius;
    private final double radius;
    private final boolean isCircle;

    public PlaceComponent(double excludeRadius, double radius, boolean isCircle){
        this.excludeRadius = excludeRadius;
        this.radius = radius;
        this.isCircle = isCircle;
    }

    public Vec3 getOffset(RandomSource randomSource){
        return this.isCircle() ?
                RandomHelper.circleAreaVec(randomSource, this.getExcludeRadius(), this.getRadius()) :
                RandomHelper.squareAreaVec(randomSource, this.getExcludeRadius(), this.getRadius());
    }

    public boolean canSpawn(){
        return this.getRadius() >= this.getExcludeRadius();
    }

    /**
     * 判断该位置是否可以生成实体：下方为完整的支撑面，自身与头部无碰撞且无流体。
     *
     * @param level 现世界。
     * @param pos 待检查的位置（实体脚部所在方块）。
     * @return true 表示可以生成。
     */
    public boolean canPlace(ServerLevel level, BlockPos pos) {
        final BlockState ground = level.getBlockState(pos.below());
        final BlockState body = level.getBlockState(pos);
        final BlockState head = level.getBlockState(pos.above());
        return Level.isInSpawnableBounds(pos)
            && ground.isFaceSturdy(level, pos.below(), Direction.UP)
            && body.getCollisionShape(level, pos).isEmpty() && body.getFluidState().isEmpty()
            && head.getCollisionShape(level, pos.above()).isEmpty();
    }

    /**
     * Check if the position is placed in the area, y axis will be ignored.
     * @param center Center of the area.
     * @param position Position to check.
     * @return True if the position is in the area.
     */
    public boolean isInArea(Vec3 center, Vec3 position) {
        final double dx = position.x() - center.x();
        final double dz = position.z() - center.z();
        final double distance = this.isCircle() ? Math.sqrt(dx * dx + dz * dz) : Math.max(Math.abs(dx), Math.abs(dz));
        return distance >= this.getExcludeRadius() && distance <= this.getRadius();
    }

    public double getExcludeRadius() {
        return excludeRadius;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isCircle() {
        return isCircle;
    }
}
