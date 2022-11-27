package hungteen.htlib;

import hungteen.htlib.common.world.raid.RaidComponent;
import hungteen.htlib.common.world.raid.WaveComponent;
import hungteen.htlib.common.world.raid.SpawnComponent;
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

    public static final DeferredRegister<RaidComponent> RAIDS = DeferredRegister.create(HTLib.prefix("custom_raid"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<RaidComponent>> RAID_REGISTRY = RAIDS.makeRegistry(RegistryBuilder::new);
    public static final DeferredRegister<WaveComponent> WAVES = DeferredRegister.create(HTLib.prefix("raid_wave"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<WaveComponent>> WAVE_REGISTRY = WAVES.makeRegistry(RegistryBuilder::new);
    public static final DeferredRegister<SpawnComponent> SPAWNS = DeferredRegister.create(HTLib.prefix("wave_spawn"), HTLib.MOD_ID);
    public static final Supplier<IForgeRegistry<SpawnComponent>> SPAWN_REGISTRY = SPAWNS.makeRegistry(RegistryBuilder::new);

    public static void register(IEventBus bus){
        RAIDS.register(bus);
        WAVES.register(bus);
        SPAWNS.register(bus);
    }


}
