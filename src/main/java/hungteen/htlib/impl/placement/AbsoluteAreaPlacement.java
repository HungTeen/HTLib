package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.SpawnPlacement;
import hungteen.htlib.api.interfaces.ISpawnPlacementType;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:38
 *
 * position: 绝对坐标。absolute coordinates.
 * excludeRadius: 排除半径，此半径之内不考虑。points in the circle with this radius will be excluded to place.
 * radius: 放置半径。points in the circle with this radius but not in excludeRadius can be placed.
 * isCircle: 默认是圆心，否则是方形。default is circle, or it will be square.
 */
public class AbsoluteAreaPlacement extends SpawnPlacement {

    public static final Codec<AbsoluteAreaPlacement> CODEC = RecordCodecBuilder.<AbsoluteAreaPlacement>mapCodec(instance -> instance.group(
            Vec3.CODEC.fieldOf("position").forGetter(AbsoluteAreaPlacement::position),
            Codec.doubleRange(0D, Double.MAX_VALUE).optionalFieldOf("exclude_radius", 0D).forGetter(AbsoluteAreaPlacement::excludeRadius),
            Codec.doubleRange(0D, Double.MAX_VALUE).optionalFieldOf("radius", 0D).forGetter(AbsoluteAreaPlacement::radius),
            Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(AbsoluteAreaPlacement::isCircle)
    ).apply(instance, AbsoluteAreaPlacement::new)).codec();
    private final Vec3 position;
    private final double excludeRadius;
    private final double radius;
    private final boolean isCircle;

    public AbsoluteAreaPlacement(Vec3 position, double excludeRadius, double radius, boolean isCircle){
        this.position = position;
        this.excludeRadius = excludeRadius;
        this.radius = radius;
        this.isCircle = isCircle;
    }

    @Override
    public Vec3 getPlacePosition(ServerLevel world, Vec3 origin) {
        if(canSpawn()){
            final Vec3 offset = this.isCircle() ?
                    RandomHelper.circleAreaVec(world.getRandom(), this.excludeRadius(), this.radius()) :
                    RandomHelper.squareAreaVec(world.getRandom(), this.excludeRadius(), this.radius());
            return this.position().add(offset);
        }
        return origin;
    }

    public boolean canSpawn(){
        return this.radius() >= this.excludeRadius();
    }

    @Override
    public ISpawnPlacementType<?> getType() {
        return HTPlacements.ABSOLUTE_AREA_TYPE;
    }

    public Vec3 position() {
        return position;
    }

    public double excludeRadius() {
        return excludeRadius;
    }

    public double radius() {
        return radius;
    }

    public boolean isCircle() {
        return isCircle;
    }
}
