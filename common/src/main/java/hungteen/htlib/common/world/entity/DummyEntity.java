package hungteen.htlib.common.world.entity;

import hungteen.htlib.platform.HTLibPlatformAPI;
import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.packet.DummyEntityPlayPacket;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 22:44
 **/
public abstract class DummyEntity {

    private final DummyEntityType<?> entityType;
    private final Level level;
    private final int entityID;
    protected Vec3 position;
    private boolean isRemoved = false;

    public DummyEntity(DummyEntityType<?> entityType, ServerLevel level, Vec3 position) {
        this.entityType = entityType;
        this.level = level;
        this.entityID = DummyEntityManager.get(level).getUniqueId();
        this.position = position;
    }

    public DummyEntity(DummyEntityType<?> entityType, Level level, CompoundTag tag){
        this.entityType = entityType;
        this.level = level;
        this.entityID = tag.getInt("DummyEntityID");
    }

    /**
     * Use to construct instance and packet sync. <br>
     * {@link DummyEntityType#create(Level, CompoundTag)} and {@link DummyEntityPlayPacket#process(ClientPacketContext)}.
     * @param tag contains entity data.
     */
    public void load(CompoundTag tag) {
        if(tag.contains("Position")){
            Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get("Position"))
                    .result().ifPresent(vec -> this.position = vec);
        }
        if(tag.contains("Removed")){
            this.isRemoved = tag.getBoolean("isRemoved");
        }
    }

    /**
     * Use to save data.
     * @param tag contains entity data.
     * @return new saved tag.
     */
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
     * Use to update entity, 更新实体。
     */
    public void tick(){

    }

    /**
     * sync data to client side.
     */
    public void sync(CompoundTag tag){
        if(this.level instanceof ServerLevel serverLevel) {
            PlayerHelper.getServerPlayers(serverLevel).forEach(player -> {
                HTLibPlatformAPI.get().sendToClient(player, new DummyEntityPlayPacket(this, tag));
            });
        }
    }

    /**
     * {@link hungteen.htlib.mixin.MixinEntity}
     */
    public void collideWith(Entity entity){
//        if(entity instanceof Projectile){
//            ((Projectile) entity).onHit(new BlockHitResult(entity.position(), entity.getDirection(), entity.blockPosition(), false));
//            entity.discard();
//        }
    }

    /**
     * {@link hungteen.htlib.mixin.MixinEntity}
     */
    public boolean hasCollision(){
        return true;
    }

    /**
     * 是否渲染边界。
     */
    public boolean renderBorder(){
        return true;
    }

    public int getBorderColor(){
        return ColorHelper.RED.rgb();
    }

    /**
     * 阻挡外面向里面的事。
     * @return
     */
    public boolean blockOutsideStuffs(){
        return true;
    }

    /**
     * 阻挡里面向外的事。
     * @return
     */
    public boolean blockInsideStuffs(){
        return true;
    }

    /**
     * 不考虑此类实体的碰撞。
     */
    public boolean ignoreEntity(Entity entity){
        return false;
    }

    public boolean requireBlockProjectile(Entity entity, AABB aabb){
        if(! this.ignoreEntity(entity) && this.isCloseToBorder(entity, aabb) && this.getDistanceToBorder(entity.position()) < 1){
            if(this.blockInsideStuffs() && this.isWithinBounds(entity.position(), entity.getBbWidth() / 2 + 1)){
                return true;
            }
            if(this.blockOutsideStuffs() && this.isOutOfBounds(entity.position(), entity.getBbWidth() / 2 + 1)){
                return true;
            }
        }
        return false;
    }

    public boolean requireBlock(Entity entity, AABB aabb){
        if(! this.ignoreEntity(entity)){
            if(this.blockInsideStuffs()){
                if(this.isWithinBounds(entity.position(), 0) && ! isWithinBounds(aabb)){
                    return true;
                }
            }
            if(this.blockOutsideStuffs()){
                if(this.isOutOfBounds(entity.position(), 0) && isWithinBounds(aabb)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean requireBlock(BlockPos blockPos, AABB aabb){
        if(this.blockInsideStuffs()){
            if(this.isWithinBounds(MathHelper.toVec3(blockPos), 0) && ! isWithinBounds(aabb)){
                return true;
            }
        }
        if(this.blockOutsideStuffs()){
            if(this.isOutOfBounds(MathHelper.toVec3(blockPos), 0) && isWithinBounds(aabb)){
                return true;
            }
        }
        return false;
    }

    public boolean requireBlock(Entity entity, BlockPos blockPos){
        if(! this.ignoreEntity(entity)){
            if(this.blockInsideStuffs()){
                if(this.isWithinBounds(entity.position(), 0) && this.isOutOfBounds(MathHelper.toVec3(blockPos), 0)){
                    return true;
                }
            }
            if(this.blockOutsideStuffs()){
                if(this.isOutOfBounds(entity.position(), 0) && this.isWithinBounds(MathHelper.toVec3(blockPos), 0)){
                    return true;
                }
            }
        }
        return false;
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
    public Optional<VoxelShape> getCollisionShapes(Entity entity) {
        if (this.blockInsideStuffs() && this.isWithinBounds(entity.position(), 0)) {
            return Optional.of(Shapes.join(Shapes.INFINITY, getEntityShape(), BooleanOp.ONLY_FIRST));
        } else if (this.blockOutsideStuffs() && this.isOutOfBounds(entity.position(), 0)) {
            return Optional.ofNullable(getEntityShape());
        }
        return Optional.empty();
    }

    protected double leastDistance(AABB aabb) {
        return Math.max(Mth.absMax(aabb.getXsize(), aabb.getZsize()), 1.0D);
    }

    public boolean isWithinBounds(AABB aabb) {
        return aabb.maxX > this.getMinX() && aabb.minX < this.getMaxX() && aabb.maxZ > this.getMinZ() && aabb.minZ < this.getMaxZ();
    }

    protected boolean isWithinBounds(Vec3 position, double enoughDistance) {
        return isWithinBounds(position.x(), position.y(), position.z(), enoughDistance);
    }

    protected boolean isOutOfBounds(Vec3 position, double enoughDistance) {
        return isOutOfBounds(position.x(), position.y(), position.z(), enoughDistance);
    }

    /**
     * @param enoughDistance 给予更多的距离，使得更容易满足条件。
     */
    protected boolean isWithinBounds(double x, double y, double z, double enoughDistance) {
        return x >= this.getMinX() - enoughDistance && x <= this.getMaxX() + enoughDistance &&
                z >= this.getMinZ() - enoughDistance && z <= this.getMaxZ() + enoughDistance;
    }

    /**
     * @param enoughDistance 给予更多的距离，使得更容易满足条件。
     */
    protected boolean isOutOfBounds(double x, double y, double z, double enoughDistance) {
        return x < this.getMinX() + enoughDistance || x > this.getMaxX() - enoughDistance ||
                z < this.getMinZ() + enoughDistance || z > this.getMaxZ() - enoughDistance;
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

    /**
     * Used to determine whether remove ths entity, 决定是否移除此实体。
     * @return true to remove, false to keep alive.
     */
    public boolean isRemoved() {
        return isRemoved;
    }

    public void remove(){
        this.setRemoved();
    }

    /**
     * Use this method to save data, 用这个方法来保存数据。
     */
    public void setDirty(){
        if(this.getLevel() instanceof ServerLevel){
            DummyEntityManager.setDirty((ServerLevel)this.getLevel());
        }
    }

    public BlockPos getBlockPosition(){
        return MathHelper.toBlockPos(getPosition());
    }

    /**
     * Where this entity is located.
     * @return The location.
     */
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

    public Level getLevel() {
        return level;
    }

    /**
     * Get specify ID.
     * @return ID.
     */
    public int getEntityID() {
        return entityID;
    }

    public DummyEntityType<?> getEntityType() {
        return entityType;
    }
}
