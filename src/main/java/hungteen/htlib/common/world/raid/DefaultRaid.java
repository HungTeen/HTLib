package hungteen.htlib.common.world.raid;

import hungteen.htlib.common.world.entity.DummyEntityType;
import hungteen.htlib.common.world.entity.HTDummyEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 21:43
 **/
public class DefaultRaid extends AbstractRaid{

    public DefaultRaid(ServerLevel serverLevel, int id, ResourceLocation location, Vec3 position) {
        super(HTDummyEntities.DEFAULT_RAID, serverLevel, id, location, position);
    }

    public DefaultRaid(DummyEntityType<?> dummyEntityType, Level level, CompoundTag tag) {
        super(dummyEntityType, level, tag);
    }

}
