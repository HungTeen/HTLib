package hungteen.htlib.common.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-08 19:45
 **/
public class HTChestBoat extends HTBoat implements HasCustomInventoryScreen, ContainerEntity {

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public HTChestBoat(EntityType<? extends HTBoat> type, Level level) {
        super(type, level);
    }

    public HTChestBoat(Level level, double x, double y, double z) {
        this(HTEntities.CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    protected float getSinglePassengerXOffset() {
        return 0.15F;
    }

    protected int getMaxPassengers() {
        return 1;
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addChestVehicleSaveData(tag);
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readChestVehicleSaveData(tag);
    }

    public void destroy(DamageSource source) {
        super.destroy(source);
        this.chestVehicleDestroyed(source, this.level, this);
    }

    public void remove(Entity.RemovalReason reason) {
        if (!this.level.isClientSide && reason.shouldDestroy()) {
            Containers.dropContents(this.level, this, this);
        }

        super.remove(reason);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        return this.canAddPassenger(player) && !player.isSecondaryUseActive() ? super.interact(player, hand) : this.interactWithChestVehicle(this::gameEvent, player);
    }

    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
        if (!player.level.isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinAi.angerNearbyPiglins(player, true);
        }

    }

    public Item getDropItem() {
        return getHTBoatType().getChestBoatItem();
    }

    public void clearContent() {
        this.clearChestVehicleContent();
    }

    public int getContainerSize() {
        return 27;
    }

    public ItemStack getItem(int id) {
        return this.getChestVehicleItem(id);
    }

    public ItemStack removeItem(int id, int count) {
        return this.removeChestVehicleItem(id, count);
    }

    public ItemStack removeItemNoUpdate(int id) {
        return this.removeChestVehicleItemNoUpdate(id);
    }

    public void setItem(int id, ItemStack stack) {
        this.setChestVehicleItem(id, stack);
    }

    public SlotAccess getSlot(int id) {
        return this.getChestVehicleSlot(id);
    }

    public void setChanged() {
    }

    public boolean stillValid(Player player) {
        return this.isChestVehicleStillValid(player);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (this.lootTable != null && player.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(inventory.player);
            return ChestMenu.threeRows(id, inventory, this);
        }
    }

    public void unpackLootTable(@Nullable Player player) {
        this.unpackChestVehicleLootTable(player);
    }

    @Nullable
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(@Nullable ResourceLocation p_219890_) {
        this.lootTable = p_219890_;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER)
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
    }

}
