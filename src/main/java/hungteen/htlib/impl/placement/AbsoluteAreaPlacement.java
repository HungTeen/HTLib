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
 */
public final class AbsoluteAreaPlacement extends SpawnPlacement {

    public static final Codec<AbsoluteAreaPlacement> CODEC = AbsoluteAreaPlacementSetting.CODEC.xmap(AbsoluteAreaPlacement::new, AbsoluteAreaPlacement::getSetting);
    private final AbsoluteAreaPlacementSetting setting;

    public AbsoluteAreaPlacement(AbsoluteAreaPlacementSetting setting){
        this.setting = setting;
    }

    public AbsoluteAreaPlacementSetting getSetting() {
        return setting;
    }

    @Override
    public Vec3 getPlacePosition(ServerLevel world, Vec3 origin) {
        if(canSpawn()){
            final Vec3 offset = this.getSetting().isCircle() ?
                    RandomHelper.circleAreaVec(world.getRandom(), this.getSetting().excludeRadius(), this.getSetting().radius()) :
                    RandomHelper.squareAreaVec(world.getRandom(), this.getSetting().excludeRadius(), this.getSetting().radius());
            return this.getSetting().position().add(offset);
        }
        return origin;
    }

    public boolean canSpawn(){
        return this.getSetting().radius() >= this.getSetting().excludeRadius();
    }

    @Override
    public ISpawnPlacementType<?> getType() {
        return HTPlacements.ABSOLUTE_AREA_TYPE.get();
    }

    /**
     * @param position 绝对坐标。absolute coordinates.
     * @param excludeRadius 排除半径，此半径之内不考虑。points in the circle with this radius will be excluded to place.
     * @param radius 放置半径。points in the circle with this radius but not in excludeRadius can be placed.
     * @param isCircle 默认是圆心，否则是方形。default is circle, or it will be square.
     */
    public record AbsoluteAreaPlacementSetting(Vec3 position, double excludeRadius, double radius, boolean isCircle){

        public static final Codec<AbsoluteAreaPlacementSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Vec3.CODEC.fieldOf("position").forGetter(AbsoluteAreaPlacementSetting::position),
                Codec.doubleRange(0D, Double.MAX_VALUE).optionalFieldOf("exclude_radius", 0D).forGetter(AbsoluteAreaPlacementSetting::excludeRadius),
                Codec.doubleRange(0D, Double.MAX_VALUE).optionalFieldOf("radius", 0D).forGetter(AbsoluteAreaPlacementSetting::radius),
                Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(AbsoluteAreaPlacementSetting::isCircle)
        ).apply(instance, AbsoluteAreaPlacementSetting::new));

    }
}
