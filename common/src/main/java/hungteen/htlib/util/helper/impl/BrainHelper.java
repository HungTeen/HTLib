package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:59
 */
public interface BrainHelper {

    HTVanillaRegistryHelper<Activity> ACTIVITY_HELPER = () -> BuiltInRegistries.ACTIVITY;

    HTVanillaRegistryHelper<Schedule> SCHEDULE_HELPER = () -> BuiltInRegistries.SCHEDULE;


    HTVanillaRegistryHelper<SensorType<?>> SENSOR_TYPE_HELPER = () -> BuiltInRegistries.SENSOR_TYPE;


    HTVanillaRegistryHelper<MemoryModuleType<?>> MEMORY_HELPER = () -> BuiltInRegistries.MEMORY_MODULE_TYPE;

    /* Common Methods */

    static HTVanillaRegistryHelper<Activity> activity(){
        return ACTIVITY_HELPER;
    }

    static HTVanillaRegistryHelper<Schedule> schedule(){
        return SCHEDULE_HELPER;
    }

    static HTVanillaRegistryHelper<SensorType<?>> sensor(){
        return SENSOR_TYPE_HELPER;
    }

    static HTVanillaRegistryHelper<MemoryModuleType<?>> memory(){
        return MEMORY_HELPER;
    }

}
