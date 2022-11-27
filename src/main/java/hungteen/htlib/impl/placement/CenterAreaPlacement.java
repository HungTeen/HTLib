package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.interfaces.IPlaceComponentType;
import hungteen.htlib.util.helper.WorldHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:38
 */
public class CenterAreaPlacement extends BaseAreaPlacement {

    /**
     * getCenterOffset 中心点偏移，默认为不偏移。offset to the origin point, default to (0, 0, 0).
     * excludeRadius 排除半径，此半径之内不考虑。points in the circle with this radius will be excluded to place.
     * radius 放置半径。points in the circle with this radius but not in excludeRadius can be placed.
     * onSurface 是否放置在地表。whether to place on the surface.
     * getHeightOffset 不放置在地表，则使原坐标高度偏移。place at the specific height offset.
     * isCircle 默认是圆心，否则是方形。default is circle, or it will be square.
     */
    public static final Codec<CenterAreaPlacement> CODEC = RecordCodecBuilder.<CenterAreaPlacement>mapCodec(instance -> instance.group(
            Vec3.CODEC.optionalFieldOf("center_offset", Vec3.ZERO).forGetter(CenterAreaPlacement::getCenterOffset),
            Codec.DOUBLE.optionalFieldOf("exclude_radius", 0D).forGetter(CenterAreaPlacement::getExcludeRadius),
            Codec.DOUBLE.fieldOf("radius").forGetter(CenterAreaPlacement::getRadius),
            Codec.BOOL.fieldOf("on_surface").forGetter(CenterAreaPlacement::onSurface),
            Codec.DOUBLE.optionalFieldOf("height_offset", 0D).forGetter(CenterAreaPlacement::getHeightOffset),
            Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(CenterAreaPlacement::isCircle)
    ).apply(instance, CenterAreaPlacement::new)).codec();
    private final Vec3 centerOffset;
    private final boolean onSurface;
    private final double heightOffset;

    public CenterAreaPlacement(Vec3 centerOffset, double excludeRadius, double radius, boolean onSurface, double heightOffset, boolean isCircle){
        super(excludeRadius, radius, isCircle);
        this.centerOffset = centerOffset;
        this.onSurface = onSurface;
        this.heightOffset = heightOffset;
    }

    @Override
    public Vec3 getPlacePosition(ServerLevel world, Vec3 origin) {
        if(this.canSpawn()){
            final Vec3 offset = this.getOffset(world.getRandom());
            final double placeHeight = this.onSurface() ?
                    WorldHelper.getSurfaceHeight(world, origin.x() + offset.x(), origin.z() + offset.z()) :
                    origin.y + this.getHeightOffset();
            return new Vec3(origin.x() + offset.x(), placeHeight, origin.z() + offset.z());
        }
        return origin;
    }

    @Override
    public IPlaceComponentType<?> getType() {
        return HTPlacements.CENTER_AREA_TYPE;
    }

    public Vec3 getCenterOffset() {
        return centerOffset;
    }

    public boolean onSurface() {
        return onSurface;
    }

    public double getHeightOffset() {
        return heightOffset;
    }

}
