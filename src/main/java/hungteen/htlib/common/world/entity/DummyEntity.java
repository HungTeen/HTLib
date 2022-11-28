package hungteen.htlib.common.world.entity;

import hungteen.htlib.HTLib;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.interfaces.IDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:44
 **/
public abstract class DummyEntity implements IDummyEntity {

    private final DummyEntityType<?> entityType;
    private final Level level;
    private final int entityID;
    protected Vec3 position;

    public DummyEntity(DummyEntityType<?> entityType, Level level, int entityID, Vec3 position) {
        this.entityType = entityType;
        this.entityID = entityID;
        this.level = level;
        this.position = position;
    }

    public DummyEntity(DummyEntityType<?> entityType, Level level, CompoundTag tag){
        this.entityType = entityType;
        this.level = level;
        this.entityID = tag.getInt("DummyEntityID");
        Vec3.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Position"))
                .resultOrPartial(msg -> HTLib.getLogger().error(msg))
                .ifPresent(vec -> this.position = vec);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putInt("DummyEntityID", this.entityID);
        Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.position)
                .resultOrPartial(msg -> HTLib.getLogger().error(msg))
                .ifPresent(nbt -> tag.put("Position", nbt));
        return tag;
    }

    public void tick(){

    }

    public BlockPos getBlockPosition(){
        return MathHelper.toBlockPos(getPosition());
    }

    @Override
    public Vec3 getPosition() {
        return position;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    public DummyEntityType<?> getEntityType() {
        return entityType;
    }
}
