package hungteen.htlib.common.registry.suit;

import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.util.NeoHelper;
import hungteen.htlib.util.helper.JavaHelper;
import hungteen.htlib.util.helper.impl.BlockHelper;
import hungteen.htlib.util.helper.impl.ItemHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 方块系列指的是管理一整套同类方块的变体的类，例如石头系列包含石头、石台阶、石半砖等等。<br>
 *
 * @param <T> 方块变体的枚举类型。
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 16:01
 */
public abstract class HTBlockSuit<T extends Enum<T>> implements SimpleEntry {

    private final ResourceLocation registryName;

    /**
     * @param registryName 传递方块系列最基本的名字。
     */
    public HTBlockSuit(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public void fillTabs(BuildCreativeModeTabContentsEvent event){
    }

    protected void fillInTab(BuildCreativeModeTabContentsEvent event, ResourceKey<CreativeModeTab> tab, CreativeModeTab.TabVisibility visibility){
        if(event.getTabKey().equals(tab)){
            getBlocks().forEach(block -> {
                event.accept(new ItemStack(block), visibility);
            });
        }
    }

    /**
     * Register all variants of blocks and items in once.
     */
    public void register(RegisterEvent event) {
        getSettingMap().forEach((type, setting) -> {
            NeoHelper.register(event, BlockHelper.get(), setting.getName(getLocation()), () -> {
                final Block block = setting.getSupplier().get();
                getBlockMap().put(type, block);
                return block;
            });
            if (setting.hasItem()) {
                final Optional<Block> block = getBlockOpt(type);
                assert block.isPresent() : "Why cause block item registry failed ? No block of %s : %s found.".formatted(getLocation().toString(), type.toString());
                NeoHelper.register(event, ItemHelper.get(), setting.getName(getLocation()), setting.getItemSupplier(block.get()));
            }
        });
    }

    protected void updateBlock(T type, Consumer<BlockBehaviour.Properties> consumer) {
        this.getSettingOpt(type).ifPresent(setting -> setting.blockProperties(consumer));
    }

    protected void updateItem(T type, Consumer<Item.Properties> consumer) {
        this.getSettingOpt(type).ifPresent(setting -> setting.itemProperties(consumer));
    }

    protected void setBlockFunction(T type, @NotNull Function<BlockBehaviour.Properties, Block> function) {
        this.getSettingOpt(type).ifPresent(setting -> setting.setBlockFunc(function));
    }

    /**
     * 每个变体都会对应一个方块的设置。
     *
     * @return 返回一个枚举类型到方块设置的映射。
     */
    protected abstract Map<T, HTBlockSetting> getSettingMap();

    /**
     * 每个变体都会对应一个真正的方块（会被注册）。
     *
     * @return 返回一个枚举类型到方块的映射。
     */
    protected abstract Map<T, Block> getBlockMap();

    /**
     * When set up completed, clear no use data.
     */
    public void clear() {
        getSettingMap().clear();
    }

    /**
     * 检查是否有某个变体的方块。
     *
     * @param type 方块变体。
     * @return 是否有这个方块。
     */
    public boolean hasBlock(T type) {
        return this.getBlockMap().containsKey(type);
    }

    /**
     * 检查是否有某个变体的物品，默认有方块就有物品。
     *
     * @param type 方块变体。
     * @return 是否有这个物品。
     */
    public boolean hasItem(T type) {
        return hasBlock(type);
    }

    /**
     * 获取所有的方块。
     *
     * @return 所有的方块。
     */
    public Collection<Block> getBlocks() {
        return this.getBlockMap().values();
    }

    /**
     * 获取所有的物品。
     *
     * @return 所有的物品。
     */
    public Collection<Item> getItems() {
        return getBlocks().stream().map(ItemStack::new).filter(JavaHelper.not(ItemStack::isEmpty)).map(ItemStack::getItem).toList();
    }

    /**
     * 获取所有的方块（含变体信息）。
     *
     * @return 所有的方块。
     */
    public Collection<Map.Entry<T, Block>> entryBlocks() {
        return this.getBlockMap().entrySet();
    }

    /**
     * 获取方块设置。
     *
     * @param type 方块变体。
     * @return 方块设置。
     */
    public Optional<HTBlockSetting> getSettingOpt(T type) {
        return JavaHelper.getOpt(this.getSettingMap(), type);
    }

    public Optional<Block> getBlockOpt(T type) {
        return JavaHelper.getOpt(this.getBlockMap(), type);
    }

    /**
     * 禁用一些方块。
     *
     * @param types 被禁用的方块变体。
     */
    protected void banBlocks(T... types) {
        Arrays.stream(types).forEach(this.getSettingMap()::remove);
    }

    /**
     * 禁用一些物品。
     *
     * @param types 被禁用的物品。
     */
    protected void banItems(T... types) {
        Arrays.stream(types).forEach(type -> {
            getSettingMap().computeIfPresent(type, (k, v) -> {
                v.setHasItem(false);
                return v;
            });
        });
    }

    @NotNull
    public Block getBlock(T type) {
        return getBlockOpt(type).orElseThrow(() -> new IllegalStateException("No block of %s : %s found.".formatted(this.registryName.toString(), type.toString())));
    }

    @Override
    public String name() {
        return this.registryName.getPath();
    }

    @Override
    public String getModID() {
        return this.registryName.getNamespace();
    }
}
