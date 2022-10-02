package hungteen.htlib.util;

import hungteen.htlib.network.NetworkHandler;
import hungteen.htlib.network.SpawnParticlePacket;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:33
 **/
public class ParticleUtil {

    public static void spawnParticles(Level level, ParticleType<?> particleType, Vec3 vec){
        NetworkHandler.sendToNearByClient(level, vec, 50, new SpawnParticlePacket(particleType.getRegistryName().toString(), vec.x, vec.y, vec.z));
    }

    public static void spawnParticles(Level level, ParticleType<?> particleType, Vec3 vec3, Vec3 speed){
        NetworkHandler.sendToNearByClient(level, vec3, 50, new SpawnParticlePacket(particleType.getRegistryName().toString(), speed.x, speed.y, speed.z));
    }
}
