package hungteen.htlib.util;

import hungteen.htlib.network.NetworkHandler;
import hungteen.htlib.network.SpawnParticlePacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:33
 **/
public class ParticleUtil {

    /**
     * Spawn Particle at pos. <br>
     */
    public static void spawnParticle(Level world, ParticleOptions type, Vec3 pos) {
        spawnRandomSpeedParticle(world, type, pos, 0);
    }

    /**
     * Spawn Random speed Particle at pos.
     */
    public static void spawnRandomSpeedParticle(Level world, ParticleOptions type, Vec3 pos, float speed) {
        spawnRandomSpeedParticle(world, type, pos, speed, speed);
    }

    /**
     * Spawn Random speed Particle at pos.
     */
    public static void spawnRandomSpeedParticle(Level world, ParticleOptions type, Vec3 pos, float horizontalSpeed, float verticalSpeed) {
        final float speedX = (world.random.nextFloat() - 0.5F) * horizontalSpeed * 2;
        final float speedY = (world.random.nextFloat() - 0.5F) * verticalSpeed * 2;
        final float speedZ = (world.random.nextFloat() - 0.5F) * horizontalSpeed * 2;
        spawnParticles(world, type, pos, speedX, speedY, speedZ);
    }

    /**
     * Spawn Random speed floating up Particle at pos.
     */
    public static void spawnRandomMoveUpParticle(Level world, ParticleOptions type, Vec3 pos, float horizontalSpeed, float verticalSpeed) {
        final float speedX = (world.random.nextFloat() - 0.5F) * horizontalSpeed * 2;
        final float speedY = world.random.nextFloat() * verticalSpeed;
        final float speedZ = (world.random.nextFloat() - 0.5F) * horizontalSpeed * 2;
        spawnParticles(world, type, pos, speedX, speedY, speedZ);
    }

    public static void spawnLineMovingParticle(Level world, ParticleOptions type, Vec3 origin, Vec3 target, int particleCountEach, double offsetScale, double speedScale) {
        spawnLineMovingParticle(world, type, origin, target, 1, particleCountEach, offsetScale, speedScale);
    }

    /**
     * Spawn Line Moving Particle from origin to target.
     * @param particleRatio How many spawn point for particle.
     * @param particleCountEach How many particles to spawn at each point.
     * @param offsetScale offset surround the spawn point.
     * @param speedScale speed of particle.
     */
    public static void spawnLineMovingParticle(Level world, ParticleOptions type, Vec3 origin, Vec3 target, float particleRatio, int particleCountEach, double offsetScale, double speedScale) {
        final double distance = origin.distanceTo(target);
        final int particleNum = Mth.ceil(distance * particleRatio);
        for(int i = 0; i < particleNum; ++ i){
            for(int j = 0; j < particleCountEach; ++ j){
                final Vec3 pos = origin.add(target.subtract(origin).normalize().scale(Math.max(1, distance - 2) / particleNum * (i + 1) / particleRatio)).add(MathUtil.getRandomVec3(world.getRandom(), offsetScale));
                final Vec3 speed = target.subtract(origin).normalize().scale(speedScale);
                spawnParticles(world, type, pos, speed.x, speed.y, speed.z);
            }
        }
    }

    /**
     * No Speed Particle.
     */
    public static void spawnParticles(Level level, ParticleOptions particleType, Vec3 pos){
        spawnParticles(level, particleType, pos, 0, 0, 0);
    }

    public static void spawnParticles(Level level, ParticleOptions particleType, Vec3 pos, double speedX, double speedY, double speedZ){
        spawnParticles(level, particleType, pos.x, pos.y, pos.z, speedX, speedY, speedZ);
    }

    /**
     * No Speed Particle.
     */
    public static void spawnParticles(Level level, ParticleOptions particleType, double x, double y, double z){
        spawnParticles(level, particleType, x, y, z, 0, 0, 0);
    }

    /**
     * Particle with speed.
     */
    public static void spawnParticles(Level level, ParticleOptions particleType, double x, double y, double z, double speedX, double speedY, double speedZ){
        if(level.isClientSide){
            level.addParticle(particleType, x, y, z, speedX, speedY, speedZ);
        } else {
            NetworkHandler.sendToNearByClient(level, new Vec3(x, y, z), 50, new SpawnParticlePacket(particleType.getType().getRegistryName().toString(), x, y, z, speedX, speedY, speedZ));
        }
    }

}
