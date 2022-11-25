package hungteen.htlib;

import hungteen.htlib.api.CustomRaid;
import hungteen.htlib.api.RaidWave;
import hungteen.htlib.api.WaveSpawn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 8:50
 */
public class HTRegister {

    public static final DeferredRegister<CustomRaid> RAIDS = DeferredRegister.create(HTLib.prefix("custom_raid"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<CustomRaid>> RAID_REGISTRY = RAIDS.makeRegistry(RegistryBuilder::new);
    public static final DeferredRegister<RaidWave> WAVES = DeferredRegister.create(HTLib.prefix("raid_wave"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<RaidWave>> WAVE_REGISTRY = WAVES.makeRegistry(RegistryBuilder::new);
    public static final DeferredRegister<WaveSpawn> SPAWNS = DeferredRegister.create(HTLib.prefix("wave_spawn"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<WaveSpawn>> SPAWN_REGISTRY = SPAWNS.makeRegistry(RegistryBuilder::new);

    public static void register(IEventBus bus){
        RAIDS.register(bus);
        WAVES.register(bus);
        SPAWNS.register(bus);
    }


}
