package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:59
 */
public class BrainHelper {

    private static final RegistryHelper<Activity> ACTIVITY_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<Activity>, Registry<Activity>> getRegistry() {
            return Either.left(ForgeRegistries.ACTIVITIES);
        }
    };

    private static final RegistryHelper<Schedule> SCHEDULE_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<Schedule>, Registry<Schedule>> getRegistry() {
            return Either.left(ForgeRegistries.SCHEDULES);
        }
    };

    private static final RegistryHelper<SensorType<?>> SENSOR_TYPE_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<SensorType<?>>, Registry<SensorType<?>>> getRegistry() {
            return Either.left(ForgeRegistries.SENSOR_TYPES);
        }
    };

    private static final RegistryHelper<MemoryModuleType<?>> MEMORY_HELPER = new RegistryHelper<>() {
        @Override
        public Either<IForgeRegistry<MemoryModuleType<?>>, Registry<MemoryModuleType<?>>> getRegistry() {
            return Either.left(ForgeRegistries.MEMORY_MODULE_TYPES);
        }
    };

    /* Common Methods */

    public static RegistryHelper<Activity> activity(){
        return ACTIVITY_HELPER;
    }

    public static RegistryHelper<Schedule> schedule(){
        return SCHEDULE_HELPER;
    }

    public static RegistryHelper<SensorType<?>> sensor(){
        return SENSOR_TYPE_HELPER;
    }

    public static RegistryHelper<MemoryModuleType<?>> memory(){
        return MEMORY_HELPER;
    }

}
