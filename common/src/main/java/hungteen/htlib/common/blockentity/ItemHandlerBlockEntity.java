package hungteen.htlib.common.blockentity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-04 08:54
 **/
public abstract class ItemHandlerBlockEntity {
//        extends HTNameableBlockEntity {
//
//    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
//
//    public ItemHandlerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState state) {
//        super(blockEntityType, blockPos, state);
//    }
//
//    public abstract ItemStackHandler getItemHandler();
//
//    @Nullable
//    @Override
//    public Packet<ClientGamePacketListener> getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }
//
//    @Override
//    public void onLoad() {
//        super.onLoad();
//        this.lazyItemHandler = LazyOptional.of(this::getItemHandler);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        super.invalidateCaps();
//        this.lazyItemHandler.invalidate();
//    }
//
//    @Override
//    protected void saveAdditional(CompoundTag tag) {
//        super.saveAdditional(tag);
//        tag.put("ItemHandler", this.getItemHandler().serializeNBT());
//    }
//
//    @Override
//    public void load(CompoundTag tag) {
//        super.load(tag);
//        if(tag.contains("ItemHandler")){
//            this.getItemHandler().deserializeNBT(tag.getCompound("ItemHandler"));
//        }
//    }
//
//    @Override
//    protected Component getDefaultName() {
//        return Component.literal("GrassCarp");
//    }
}
