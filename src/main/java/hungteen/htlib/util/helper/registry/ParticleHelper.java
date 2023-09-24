package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import hungteen.htlib.util.helper.RandomHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:33
 **/
public class ParticleHelper extends RegistryHelper<ParticleType<?>> {

    private static final ParticleHelper HELPER = new ParticleHelper();

    public static void spawnLineMovingParticle(Level world, ParticleOptions type, Vec3 origin, Vec3 target, int particleCountEach, double offsetScale, double speedScale) {
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
    public static void spawnLineMovingParticle(Level world, ParticleOptions type, Vec3 origin, Vec3 target, float particleRatio, int particleCountEach, double offsetScale, double speedScale) {
        final double distance = origin.distanceTo(target);
        final int particleNum = Mth.ceil(distance * particleRatio);
        for (int i = 0; i < particleNum; ++i) {
            final Vec3 pos = origin.add(target.subtract(origin).normalize().scale(Math.max(1, distance - 2) / particleNum * (i + 1) / particleRatio)).add(RandomHelper.vec3Range(world.getRandom(), offsetScale));
            final Vec3 speed = target.subtract(origin).normalize().scale(speedScale);
            spawnParticles(world, type, pos, particleCountEach, speed);
        }
    }

    public static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, Vec3 speed) {
        spawnParticles(level, particle, pos.x, pos.y, pos.z, amount, 0, 0, speed.x, speed.y, speed.z);
    }

    public static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, double dst, double speed) {
        spawnParticles(level, particle, pos.x, pos.y, pos.z, amount, dst, dst, speed, speed);
    }

    public static void spawnParticles(Level level, ParticleOptions particle, Vec3 pos, int amount, double dstXZ, double dstY, double speed) {
        spawnParticles(level, particle, pos.x, pos.y, pos.z, amount, dstXZ, dstY, speed);
    }

    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dst, double speed) {
        spawnParticles(level, particle, x, y, z, amount, dst, dst, speed, speed);
    }

    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dstXZ, double dstY, double speedXZ, double speedY) {
        spawnParticles(level, particle, x, y, z, amount, dstXZ, dstY, speedXZ, speedY, speedXZ);
    }

    /**
     * Particle with speed.
     */
    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dstXZ, double dstY, double speedX, double speedY, double speedZ) {
        if (level.isClientSide) {
            for (int i = 0; i < amount; ++i) {
                level.addParticle(particle, x + level.getRandom().nextGaussian() * dstXZ, y + level.getRandom().nextGaussian() * dstY, z + level.getRandom().nextGaussian() * dstXZ, level.getRandom().nextGaussian() * speedX, level.getRandom().nextGaussian() * speedY, level.getRandom().nextGaussian() * speedZ);
            }
        }
    }

    public static void spawnParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z, int amount, double speed) {
        level.sendParticles(particle, x, y, z, amount, 0, 0, 0, speed);
    }

    public static void spawnParticles(Level level, ParticleOptions particle, double x, double y, double z, int amount, double dstXZ, double dstY, double speed) {
        if(level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(particle, x, y, z, amount, dstXZ, dstY, dstXZ, speed);
        } else{
            spawnParticles(level, particle, x, y, z, amount, dstXZ, dstY, speed, speed, speed);
        }
    }

    public static <T extends ParticleOptions> T readParticle(FriendlyByteBuf buf, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buf);
    }

    public static <T extends ParticleOptions> void writeParticle(FriendlyByteBuf buf, T type) {
        type.writeToNetwork(buf);
    }

    /* Common Methods */

    public static ParticleHelper get() {
        return HELPER;
    }

    @Override
    public Either<IForgeRegistry<ParticleType<?>>, Registry<ParticleType<?>>> getRegistry() {
        return Either.left(ForgeRegistries.PARTICLE_TYPES);
    }

}
