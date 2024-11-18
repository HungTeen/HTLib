package hungteen.htlib.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:07
 **/
public abstract class HTAdvancementGen extends AdvancementProvider {

    private final String modId;

    public HTAdvancementGen(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelperIn, List<AdvancementGenerator> subProviders) {
        super(output, registries, fileHelperIn, subProviders);
        this.modId = modId;
    }

    public static abstract class HTAdvancementBuilder implements AdvancementGenerator {

        private final String modId;

        protected HTAdvancementBuilder(String modId) {
            this.modId = modId;
        }

        protected Advancement.Builder task(AdvancementHolder parent, ItemLike displayItem, String title) {
            return advancement(parent, displayItem, title, AdvancementType.TASK, true);
        }

        protected Advancement.Builder challenge(AdvancementHolder parent, ItemLike displayItem, String title) {
            return advancement(parent, displayItem, title, AdvancementType.CHALLENGE, true);
        }

        protected Advancement.Builder goal(AdvancementHolder parent, ItemLike displayItem, String title) {
            return advancement(parent, displayItem, title, AdvancementType.GOAL, true);
        }

        protected Advancement.Builder root(ItemLike displayItem, String title, ResourceLocation location) {
            return advancement(null, displayItem, title, location, AdvancementType.TASK, true);
        }

        protected Advancement.Builder advancement(AdvancementHolder parent, ItemLike displayItem, String title, AdvancementType dataType, boolean displayChat) {
            return advancement(parent, displayItem, title, null, dataType, displayChat);
        }

        protected Advancement.Builder advancement(AdvancementHolder parent, ItemLike displayItem, String title, ResourceLocation location, AdvancementType dataType, boolean displayChat) {
            return advancement(parent, displayItem, title, location, dataType, true, displayChat, false);
        }

        protected Advancement.Builder advancement(AdvancementHolder parent, ItemLike displayItem, String title, ResourceLocation location, AdvancementType dataType, boolean showToast, boolean displayChat, boolean hidden) {
            return Advancement.Builder.advancement().parent(parent).display(displayItem, title(title), desc(title), location, dataType, showToast, displayChat, hidden);
        }

        protected MutableComponent title(String name) {
            return Component.translatable("advancements." + this.getModId() + "." + name + ".title");
        }

        protected MutableComponent desc(String name) {
            return Component.translatable("advancements." + this.getModId() + "." + name + ".desc");
        }

        protected String getModId() {
            return this.modId;
        }
    }
}
