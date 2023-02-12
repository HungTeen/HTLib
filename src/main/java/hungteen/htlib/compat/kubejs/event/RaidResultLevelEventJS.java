package hungteen.htlib.compat.kubejs.event;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import hungteen.htlib.common.event.events.RaidEvent;
import hungteen.htlib.common.world.raid.AbstractRaid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-12 22:34
 **/
public class RaidResultLevelEventJS extends LevelEventJS {

    private final RaidEvent.RaidResultLevelEvent event;

    public RaidResultLevelEventJS(RaidEvent.RaidResultLevelEvent event) {
        this.event = event;
    }

    @Override
    public Level getLevel() {
        return event.getLevel();
    }

    public int getTick(){
        return event.getTick();
    }

    public ResourceLocation getId(){
        return event.getId();
    }

    public AbstractRaid getRaid(){
        return event.getRaid();
    }
}
