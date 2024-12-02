package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import hungteen.htlib.common.network.packet.SpawnParticlePacket;
import hungteen.htlib.util.helper.NetworkHelper;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:33
 **/
public interface ParticleHelper extends HTVanillaRegistryHelper<ParticleType<?>> {

    HTVanillaRegistryHelper<ParticleType<?>> HELPER = () -> BuiltInRegistries.PARTICLE_TYPE;

    static void spawnLineMovingParticle(Level world, ParticleOptions type, Vec3 origin, Vec3 target, int particleCountEach, double offsetScale, double speedScale) {
        spawnLineMovingParticle(world, type, origin, target, 1, particleCountEach, offsetScale, speedScale);
    }

    /**
     * Spawn Line Moving Particle from origin to target.
     *
     * @param particleRatio     How many getSpawnEntities point for particle.
     * @param particleCountEach How many particles to getSpawnEntities at each point.
     * @param offsetScale       offset surround the getSpawnEntities point.
     * @param speedScale        speed of particle.
     */
    static void spawnLineMovingParticle(Level world, ParticleOptions type, Vec3 origin, Vec3 target, float particleRatio, int particleCountEach, double offsetScale, double speedScale) {
        final double distance = origin.distanceTo(target);
        final int particleNum = Mth.ceil(distance * particleRatio);
        for (int i = 0; i < particleNum; ++i) {
            final Vec3 pos = origin.add(target.subtract(origin).normalize().scale(Math.max(1, distance - 2) / particleNum * (i + 1) / particleRatio)).add(RandomHelper.vec3Range(world.getRandom(), offsetScale));
            final Vec3 speed = target.subtract(origin).normalize().scale(speedScale);
            spawnParticles(world, type, pos, particleCountEach, speed);
        }
    }

    static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, Vec3 speed) {
        if(level instanceof ServerLevel serverLevel){
            sendParticles(serverLevel, particle, pos, amount, speed);
        } else{
            spawnClientParticles(level, particle, pos, amount, speed);
        }
    }

    static void spawnClientParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, Vec3 speed) {
        if(level.isClientSide()){
            for(int i = 0; i < amount; ++ i) {
                level.addParticle(particle, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
            }
        } else {
            HTLibAPI.logger().warn("Server side cannot spawn particles.");
        }
    }

    static void spawnClientParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, double dst, double speed) {
        spawnClientParticles(level, particle, pos.x, pos.y, pos.z, amount, dst, dst, speed, speed);
    }

    static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, double dstXZ, double dstY, double speed) {
        spawnParticles(level, particle, pos.x, pos.y, pos.z, amount, dstXZ, dstY, speed);
    }

    static void spawnClientParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dst, double speed) {
        spawnClientParticles(level, particle, x, y, z, amount, dst, dst, speed, speed);
    }

    static void spawnClientParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dstXZ, double dstY, double speedXZ, double speedY) {
        spawnClientParticles(level, particle, x, y, z, amount, dstXZ, dstY, speedXZ, speedY, speedXZ);
    }

    static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dstXZ, double dstY, double speed) {
        if(level instanceof ServerLevel serverLevel){
            sendParticles(serverLevel, particle, x, y, z, amount, dstXZ, dstY, dstXZ, speed);
        } else{
            spawnClientParticles(level, particle, x, y, z, amount, dstXZ, dstY, speed, speed, speed);
        }
    }

    /**
     * Particle with speed.
     */
    static void spawnClientParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dstXZ, double dstY, double speedX, double speedY, double speedZ) {
        if (level.isClientSide) {
            for (int i = 0; i < amount; ++i) {
                level.addParticle(particle, x + level.getRandom().nextGaussian() * dstXZ, y + level.getRandom().nextGaussian() * dstY, z + level.getRandom().nextGaussian() * dstXZ, level.getRandom().nextGaussian() * speedX, level.getRandom().nextGaussian() * speedY, level.getRandom().nextGaussian() * speedZ);
            }
        } else {
            HTLibAPI.logger().warn("Server side cannot spawn particles.");
        }
    }

    /**
     * 坐标不偏移的粒子生成方法。
     */
    static void sendParticles(ServerLevel level, ParticleOptions particle, Vec3 pos, int amount, Vec3 speed) {
        NetworkHelper.sendToClient(level, new SpawnParticlePacket(particle, pos, speed, amount));
    }

    /**
     * 坐标不偏移的粒子生成方法。
     */
    static void sendParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z, int amount, Vec3 speed) {
        sendParticles(level, particle, new Vec3(x, y, z), amount, speed);
    }

    /**
     * 坐标不偏移的粒子生成方法。
     */
    static int sendParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z, int amount, double speed) {
       return sendParticles(level, particle, x, y, z, amount, 0D, 0D, 0D, speed);
    }

    /**
     * {@link net.minecraft.client.multiplayer.ClientPacketListener#handleParticleEvent(ClientboundLevelParticlesPacket)}.
     * @param level Server Side only.
     * @param particle Particle Type.
     * @param x 粒子的 X 轴坐标。
     * @param y 粒子的 Y 轴坐标。
     * @param z 粒子的 Z 轴坐标。
     * @param xDist 粒子的最大 X 轴偏移。
     * @param yDist 粒子的最大 Y 轴偏移。
     * @param zDist 粒子的最大 Z 轴偏移。
     * @param amount 粒子数量，如果设置为 0，那么会生成一个粒子，并且位置将不会偏移。
     * @param speed 粒子的最大速度。
     * @return 成功生成的粒子数量。
     */
    static int sendParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z, int amount, double xDist, double yDist, double zDist, double speed) {
        return level.sendParticles(particle, x, y, z, amount, xDist, yDist, zDist, speed);
    }

    @Deprecated(since = "1.1.1", forRemoval = true)
    static int sendParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z, double xDist, double yDist, double zDist, int amount, double speed) {
        return sendParticles(level, particle, x, y, z, amount, xDist, yDist, zDist, speed);
    }

    /* Common Methods */

    static HTVanillaRegistryHelper<ParticleType<?>> get() {
        return HELPER;
    }


}
