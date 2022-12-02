package hungteen.htlib.common.world.entity;

import hungteen.htlib.common.network.DummyEntityPacket;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.htlib.util.interfaces.IDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

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
    }

    public void load(CompoundTag tag) {
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

    /**
     * sync data to client side.
     */
    public void sync(CompoundTag tag){
        PlayerHelper.getServerPlayers(this.level).forEach(player -> {
            NetworkHandler.sendToClient(player, new DummyEntityPacket(this, tag));
        });
    }

    public void tick(){

    }

    /**
     * {@link hungteen.htlib.mixin.MixinEntity}
     */
    public boolean hasCollision(){
        return true;
    }


    public boolean renderBorder(){
        return true;
    }

    /**
     * 先保证这个实体离碰撞体不远，再用距离。<br>
     * 靠近边界的时候要把碰撞体加上。{@link hungteen.htlib.mixin.MixinEntity}
     */
    public boolean isCloseToBorder(Entity entity, AABB aabb) {
        final double leastDistance = this.leastDistance(aabb) * 2;
        return this.isWithinBounds(entity.position(), leastDistance + 2) && this.getDistanceToBorder(entity.position()) < leastDistance;
    }

    /**
     * 计算碰撞体。{@link hungteen.htlib.mixin.MixinEntity}
     */
    public List<VoxelShape> getCollisionShapes(Entity entity) {
        return List.of(getEntityShape());
    }

    protected double leastDistance(AABB aabb) {
        return Math.max(Mth.absMax(aabb.getXsize(), aabb.getZsize()), 1.0D);
    }

    /**
     * @param enoughDistance 给予更多的距离，使得更容易满足条件。
     */
    protected boolean isWithinBounds(Vec3 position, double enoughDistance) {
        return position.x >= this.getMinX() - enoughDistance && position.x <= this.getMaxX() + enoughDistance &&
                position.z >= this.getMinZ() - enoughDistance && position.z <= this.getMaxZ() + enoughDistance;
    }

    /**
     * @param enoughDistance 给予更多的距离，使得更容易满足条件。
     */
    protected boolean isOutOfBounds(Vec3 position, double enoughDistance) {
        return position.x < this.getMinX() + enoughDistance || position.x > this.getMaxX() - enoughDistance ||
                position.z < this.getMinZ() + enoughDistance || position.z > this.getMaxZ() - enoughDistance;
    }

    /**
     * Distance to border, ignore y dimension. <br>
     * 这种算法一般用于计算边界之内的位置距离，在外面很远会失效（有bug），所以别乱用。<br>
     * {@link #isCloseToBorder(Entity, AABB)}
     */
    protected double getDistanceToBorder(Vec3 position) {
        return Math.min(
                Math.min(Math.abs(position.z - this.getMinZ()), Math.abs(position.z - this.getMaxZ())),
                Math.min(Math.abs(position.x - this.getMinX()), Math.abs(position.x - this.getMaxX()))
        );
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

    public VoxelShape getEntityShape(){
        return Shapes.box(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public double getHeight(){
        return this.getLevel().getHeight();
    }

    public double getWidth(){
        return 2;
    }

    public double getMinX() {
        return this.getX() - getWidth() / 2;
    }

    public double getMinZ() {
        return this.getZ() - getWidth() / 2;
    }

    public double getMaxX() {
        return this.getX() + getWidth() / 2;
    }

    public double getMaxZ() {
        return this.getZ() + getWidth() / 2;
    }

    public double getMinY() {
        return Double.NEGATIVE_INFINITY;
    }

    public double getMaxY() {
        return Double.POSITIVE_INFINITY;
    }

    public double getX(){
        return getPosition().x();
    }

    public double getY(){
        return getPosition().y();
    }

    public double getZ() {
        return getPosition().z();
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
