package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.raid.IPlaceComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:38
 */
public class AbsoluteAreaPlacement extends PlaceComponent {

    /**
     * getPosition: 绝对坐标。absolute coordinates.
     * excludeRadius: 排除半径，此半径之内不考虑。points in the circle with this radius will be excluded to place.
     * radius: 放置半径。points in the circle with this radius but not in excludeRadius can be placed.
     * isCircle: 默认是圆心，否则是方形。default is circle, or it will be square.
     */
    public static final Codec<AbsoluteAreaPlacement> CODEC = RecordCodecBuilder.<AbsoluteAreaPlacement>mapCodec(instance -> instance.group(
            Vec3.CODEC.fieldOf("getPosition").forGetter(AbsoluteAreaPlacement::getPosition),
            Codec.doubleRange(0D, Double.MAX_VALUE).optionalFieldOf("exclude_radius", 0D).forGetter(AbsoluteAreaPlacement::getExcludeRadius),
            Codec.doubleRange(0D, Double.MAX_VALUE).optionalFieldOf("radius", 0D).forGetter(AbsoluteAreaPlacement::getRadius),
            Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(AbsoluteAreaPlacement::isCircle)
    ).apply(instance, AbsoluteAreaPlacement::new)).codec();
    private final Vec3 position;

    public AbsoluteAreaPlacement(Vec3 position, double excludeRadius, double radius, boolean isCircle) {
        super(excludeRadius, radius, isCircle);
        this.position = position;
    }

    @Override
    public Vec3 getPlacePosition(ServerLevel world, Vec3 origin) {
        if (this.canSpawn()) {
            final Vec3 offset = this.getOffset(world.getRandom());
            return this.getPosition().add(offset);
        }
        return origin;
    }

    @Override
    public IPlaceComponentType<?> getType() {
        return HTPlaceComponents.ABSOLUTE_AREA_TYPE;
    }

    public Vec3 getPosition() {
        return position;
    }

}
