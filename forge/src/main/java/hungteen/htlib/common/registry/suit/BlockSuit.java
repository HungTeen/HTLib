package hungteen.htlib.common.registry.suit;

import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.util.helper.JavaHelper;
import hungteen.htlib.util.helper.registry.BlockHelper;
import hungteen.htlib.util.helper.registry.ItemHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 16:01
 */
public abstract class BlockSuit<T extends Enum<T>> implements SimpleEntry {

    private final ResourceLocation registryName;

    public BlockSuit(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public void fillSuits(BuildCreativeModeTabContentsEvent event){
    }

    protected void fillInTab(BuildCreativeModeTabContentsEvent event, ResourceKey<CreativeModeTab> tab, CreativeModeTab.TabVisibility visibility){
        if(event.getTabKey().equals(tab)){
            getBlocks().forEach(block -> {
                event.accept(new ItemStack(block), visibility);
            });
        }
    }

    /**
     * Register block and items.
     */
    public void register(RegisterEvent event) {
        if(ItemHelper.get().matchEvent(event) || BlockHelper.get().matchEvent(event)) {
            getSettingMap().forEach((type, setting) -> {
                BlockHelper.get().register(event, setting.getName(getLocation()), () -> {
                    final Block block = setting.getSupplier().get();
                    getBlockMap().put(type, block);
                    return block;
                });
                if (setting.hasItem()) {
                    ItemHelper.get().register(event, setting.getName(getLocation()), () -> {
                        final Optional<Block> block = getBlockOpt(type);
                        assert block.isPresent() : "Why cause block item registry failed ? No block of %s : %s found.".formatted(getLocation().toString(), type.toString());
                        return setting.getItemFunction().apply(block.get(), setting.getItemProperties());
                    });
                }
            });
        }
    }

    protected void updateBlock(T type, Consumer<BlockBehaviour.Properties> consumer) {
        this.getBlockSetting(type).ifPresent(setting -> setting.blockProperties(consumer));
    }

    protected void updateItem(T type, Consumer<Item.Properties> consumer) {
        this.getBlockSetting(type).ifPresent(setting -> setting.itemProperties(consumer));
    }

    protected void setBlockFunction(T type, @NotNull Function<BlockBehaviour.Properties, Block> function) {
        this.getBlockSetting(type).ifPresent(setting -> setting.setBlockFunction(function));
    }

    protected abstract Map<T, HTBlockSetting> getSettingMap();

    protected abstract Map<T, Block> getBlockMap();

    /**
     * When set up completed, clear no use data.
     */
    public void clear() {
        getSettingMap().clear();
    }

    public boolean hasBlock(T type){
        return this.getBlockMap().containsKey(type);
    }

    public boolean hasItem(T type){
        return hasBlock(type);
    }

    public Collection<Block> getBlocks(){
        return this.getBlockMap().values();
    }

    public Collection<Item> getItems(){
        return getBlocks().stream().map(ItemStack::new).filter(JavaHelper.not(ItemStack::isEmpty)).map(ItemStack::getItem).toList();
    }

    public List<Map.Entry<T, Block>> getWoodBlocks() {
        return this.getBlockMap().entrySet().stream().toList();
    }

    public Optional<HTBlockSetting> getBlockSetting(T type){
        return JavaHelper.getOpt(this.getSettingMap(), type);
    }

    public Optional<Block> getBlockOpt(T type){
        return JavaHelper.getOpt(this.getBlockMap(), type);
    }

    public void banBlocks(T ... types){
        Arrays.stream(types).forEach(this.getSettingMap()::remove);
    }

    public void banItems(T ... types){
        Arrays.stream(types).forEach(type -> {
            getSettingMap().computeIfPresent(type, (k, v) -> {
                v.setHasItem(false);
                return v;
            });
        });
    }

    @NotNull
    public Block getBlock(T type) {
        return getBlockOpt(type).orElseThrow();
    }

    @Override
    public String getName() {
        return this.registryName.getPath();
    }

    @Override
    public String getModID() {
        return this.registryName.getNamespace();
    }
}
