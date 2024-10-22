package hungteen.htlib.compat.kubejs.event;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import hungteen.htlib.common.event.events.RaidEvent;
import hungteen.htlib.common.world.raid.AbstractRaid;
import net.minecraft.world.level.Level;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 22:57
 **/
public class RaidDefeatedEventJS extends LevelEventJS {

    private final RaidEvent.RaidDefeatedEvent event;

    public RaidDefeatedEventJS(RaidEvent.RaidDefeatedEvent event) {
        this.event = event;
    }

    @Override
    public Level getLevel() {
        return event.getLevel();
    }

    public AbstractRaid getRaid(){
        return event.getRaid();
    }
}
