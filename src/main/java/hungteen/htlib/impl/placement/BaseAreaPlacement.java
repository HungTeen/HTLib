package hungteen.htlib.impl.placement;

import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:22
 **/
public abstract class BaseAreaPlacement extends PlaceComponent {

    private final double excludeRadius;
    private final double radius;
    private final boolean isCircle;

    public BaseAreaPlacement(double excludeRadius, double radius, boolean isCircle){
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
