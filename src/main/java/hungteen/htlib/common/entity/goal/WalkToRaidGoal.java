package hungteen.htlib.common.entity.goal;

import hungteen.htlib.common.capability.raid.IRaidCapability;
import hungteen.htlib.common.capability.raid.RaidCapability;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/5 10:40
 **/
public class WalkToRaidGoal extends Goal {

    private final PathfinderMob mob;
    private double wantedX;
    private double wantedY;
    private double wantedZ;
    private final double speedModifier;
    private final double dangerousRate;
    private final double safeRate;

    public WalkToRaidGoal(PathfinderMob mob, double speedModifier) {
        this(mob, speedModifier, 0.7, 0.2);
    }

    public WalkToRaidGoal(PathfinderMob mob, double speedModifier, double dangerousRate, double safeRate) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.dangerousRate = dangerousRate;
        this.safeRate = safeRate;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Optional<IRaidCapability> raidOpt = RaidCapability.getRaid(this.mob);
        if(raidOpt.isPresent() && this.mob.getTarget() == null){
            Vec3 raidCenter = raidOpt.get().getRaid().getPosition();
            double raidWidth = raidOpt.get().getRaid().getWidth();
            double distanceToRaidCenterSqr = Math.sqrt(this.mob.distanceToSqr(raidCenter));
            // 袭击者距离中心太远。
            if(distanceToRaidCenterSqr > raidWidth / 2 * this.dangerousRate) {
                Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 32, 9, raidCenter, (float) Math.PI / 4F);
                if (vec3 != null) {
                    this.wantedX = vec3.x;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

}
