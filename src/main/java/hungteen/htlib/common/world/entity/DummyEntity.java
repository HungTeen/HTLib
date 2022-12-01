package hungteen.htlib.common.world.entity;

import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.interfaces.IDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
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
    private boolean isRemoved = false;

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
        if(tag.contains("Position")){
            Vec3.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Position"))
                    .result().ifPresent(vec -> this.position = vec);
        }
        if(tag.contains("Removed")){
            this.isRemoved = tag.getBoolean("isRemoved");
        }
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putInt("DummyEntityID", this.entityID);
        if(this.position != null){
            Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.position)
                    .result().ifPresent(nbt -> tag.put("Position", nbt));
        }
        tag.putBoolean("Removed", this.isRemoved);
        return tag;
    }

    public void tick(){

    }

    public void setRemoved(){
        setRemoved(true);
    }

    public void setRemoved(boolean removed){
        this.isRemoved = removed;
    }

    @Override
    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public void setDirty(){
        if(this.getLevel() instanceof ServerLevel){
            DummyEntityManager.setDirty((ServerLevel)this.getLevel());
        }
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

    @Override
    public int getEntityID() {
        return entityID;
    }

    public DummyEntityType<?> getEntityType() {
        return entityType;
    }
}
