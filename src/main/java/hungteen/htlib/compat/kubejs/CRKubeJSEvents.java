package hungteen.htlib.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import hungteen.htlib.common.event.events.RaidEvent;
import hungteen.htlib.compat.kubejs.event.*;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 22:38
 **/
public interface CRKubeJSEvents {

    EventGroup GROUP = EventGroup.of("CRaidEvents");

    EventHandler RAID_DEFEATED = GROUP.server("raidDefeated", () -> RaidDefeatedEventJS.class);
    EventHandler RAID_LOST = GROUP.server("raidLost", () -> RaidLostEventJS.class);
    EventHandler WAVE_START = GROUP.server("waveStart", () -> RaidWaveStartEventJS.class);
    EventHandler WAVE_FINISH = GROUP.server("waveFinish", () -> RaidWaveFinishEventJS.class);
    EventHandler RESULT_LEVEL = GROUP.server("resultLevel", () -> RaidResultLevelEventJS.class);
    EventHandler RESULT_DEFENDER = GROUP.server("resultDefender", () -> RaidResultDefenderEventJS.class);
    EventHandler RESULT_RAIDER = GROUP.server("resultRaider", () -> RaidResultRaiderEventJS.class);
}
