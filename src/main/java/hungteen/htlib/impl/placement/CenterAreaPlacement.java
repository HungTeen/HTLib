package hungteen.htlib.impl.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.SpawnPlacement;
import hungteen.htlib.api.interfaces.ISpawnPlacementType;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.htlib.util.helper.WorldHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:38
 */
public class CenterAreaPlacement extends SpawnPlacement {

    public static final Codec<CenterAreaPlacement> CODEC = CenterAreaPlacementSetting.CODEC.xmap(CenterAreaPlacement::new, CenterAreaPlacement::getSetting);
    private final CenterAreaPlacementSetting setting;

    public CenterAreaPlacement(CenterAreaPlacementSetting setting){
        this.setting = setting;
    }

    public CenterAreaPlacementSetting getSetting() {
        return setting;
    }

    @Override
    public Vec3 getPlacePosition(ServerLevel world, Vec3 origin) {
        final Vec3 offset = this.getSetting().isCircle() ?
                RandomHelper.circleAreaVec(world.getRandom(), this.getSetting().excludeRadius(), this.getSetting().radius()) :
                RandomHelper.squareAreaVec(world.getRandom(), this.getSetting().excludeRadius(), this.getSetting().radius());
        final double placeHeight = this.getSetting().onSurface() ?
                WorldHelper.getSurfaceHeight(world, origin.x() + offset.x(), origin.z() + offset.z()) :
                origin.y + this.getSetting().heightOffset();
        return new Vec3(origin.x() + offset.x(), placeHeight, origin.z() + offset.z());
    }

    @Override
    public ISpawnPlacementType<?> getType() {
        return HTPlacements.CENTER_AREA_TYPE.get();
    }

    /**
     * @param centerOffset 中心点偏移，默认为不偏移。offset to the origin point, default to (0, 0, 0).
     * @param excludeRadius 排除半径，此半径之内不考虑。points in the circle with this radius will be excluded to place.
     * @param radius 放置半径。points in the circle with this radius but not in excludeRadius can be placed.
     * @param onSurface 是否放置在地表。whether to place on the surface.
     * @param heightOffset 不放置在地表，则使原坐标高度偏移。place at the specific height offset.
     * @param isCircle 默认是圆心，否则是方形。default is circle, or it will be square.
     */
    public record CenterAreaPlacementSetting(Vec3 centerOffset, double excludeRadius, double radius, boolean onSurface, double heightOffset, boolean isCircle){
        public static final Codec<CenterAreaPlacementSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Vec3.CODEC.optionalFieldOf("center_offset", Vec3.ZERO).forGetter(CenterAreaPlacementSetting::centerOffset),
                Codec.DOUBLE.optionalFieldOf("exclude_radius", 0D).forGetter(CenterAreaPlacementSetting::excludeRadius),
                Codec.DOUBLE.fieldOf("radius").forGetter(CenterAreaPlacementSetting::radius),
                Codec.BOOL.fieldOf("on_surface").forGetter(CenterAreaPlacementSetting::onSurface),
                Codec.DOUBLE.optionalFieldOf("height_offset", 0D).forGetter(CenterAreaPlacementSetting::heightOffset),
                Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(CenterAreaPlacementSetting::isCircle)
        ).apply(instance, CenterAreaPlacementSetting::new));
    }
}
