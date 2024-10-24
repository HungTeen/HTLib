package hungteen.htlib.compat.kubejs.event;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import hungteen.htlib.common.event.events.DummyEntityEvent;
import hungteen.htlib.common.world.entity.DummyEntityImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-02-11 23:27
 **/
public class SpawnDummyEntityEventJS extends LevelEventJS {

    private final DummyEntityEvent.DummyEntitySpawnEvent event;

    public SpawnDummyEntityEventJS(DummyEntityEvent.DummyEntitySpawnEvent event) {
        this.event = event;
    }

    public DummyEntityImpl getDummyEntity() {
        return event.getDummyEntity();
    }

    public ResourceLocation getType(){
        return getDummyEntity().getEntityType().getLocation();
    }

    @Override
    public Level getLevel() {
        return event.getLevel();
    }
}
