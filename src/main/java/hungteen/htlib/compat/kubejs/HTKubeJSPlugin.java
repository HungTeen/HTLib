package hungteen.htlib.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import hungteen.htlib.common.event.events.DummyEntityEvent;
import hungteen.htlib.common.event.events.RaidEvent;
import hungteen.htlib.compat.kubejs.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 22:33
 **/
public class HTKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void init() {
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, HTKubeJSPlugin::raidDefeated);
        forgeBus.addListener(EventPriority.NORMAL, HTKubeJSPlugin::raidLost);
        forgeBus.addListener(EventPriority.NORMAL, HTKubeJSPlugin::waveStart);
        forgeBus.addListener(EventPriority.NORMAL, HTKubeJSPlugin::waveFinish);
        forgeBus.addListener(EventPriority.NORMAL, HTKubeJSPlugin::spawnDummyEntity);
    }

    @Override
    public void registerEvents() {
        HTKubeJSEvents.GROUP.register();
        CRKubeJSEvents.GROUP.register();
    }

    private static void raidDefeated(RaidEvent.RaidDefeatedEvent event) {
        CRKubeJSEvents.RAID_DEFEATED.post(new RaidDefeatedEventJS(event));
    }

    private static void raidLost(RaidEvent.RaidLostEvent event) {
        CRKubeJSEvents.RAID_LOST.post(new RaidLostEventJS(event));
    }

    private static void waveStart(RaidEvent.RaidWaveStartEvent event) {
        CRKubeJSEvents.WAVE_START.post(new RaidWaveStartEventJS(event));
    }

    private static void waveFinish(RaidEvent.RaidWaveFinishEvent event) {
        CRKubeJSEvents.WAVE_FINISH.post(new RaidWaveFinishEventJS(event));
    }

    private static void spawnDummyEntity(DummyEntityEvent.DummyEntitySpawnEvent event) {
        if(HTKubeJSEvents.SPAWN_DUMMY_ENTITY.post(new SpawnDummyEntityEventJS(event))){
            event.setCanceled(true);
        }
    }

}
