package hungteen.htlib.common.impl.position;

import hungteen.htlib.api.interfaces.raid.IPositionComponent;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.util.RandomSource;
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
