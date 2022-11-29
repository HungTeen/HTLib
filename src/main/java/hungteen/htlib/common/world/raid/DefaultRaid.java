package hungteen.htlib.common.world.raid;

import hungteen.htlib.common.world.entity.DummyEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 21:02
 **/
public class DefaultRaid extends AbstractRaid {


    public DefaultRaid(DummyEntityType<?> dummyEntityType, ServerLevel serverLevel, int id, ResourceLocation location, Vec3 position) {
        super(dummyEntityType, serverLevel, id, location, position);
    }

    public DefaultRaid(DummyEntityType<?> dummyEntityType, Level level, CompoundTag nbt) {
        super(dummyEntityType, level, nbt);
    }
}