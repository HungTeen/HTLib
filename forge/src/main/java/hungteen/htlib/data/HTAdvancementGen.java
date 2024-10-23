package hungteen.htlib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:07
 **/
public abstract class HTAdvancementGen extends ForgeAdvancementProvider {

    private final String modId;

    public HTAdvancementGen(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelperIn, List<AdvancementGenerator> subProviders) {
        super(output, registries, fileHelperIn, subProviders);
        this.modId = modId;
    }

//    public static abstract class HTAdvancementBuilder implements AdvancementGenerator {
//
//        private final String modId;
//
//        protected HTAdvancementBuilder(String modId) {
//            this.modId = modId;
//        }
//
//        protected Advancement.Builder task(AdvancementHolder parent, ItemLike displayItem, String title) {
//            return advancement(parent, displayItem, title, FrameType.TASK, true);
//        }
//
//        protected Advancement.Builder challenge(AdvancementHolder parent, ItemLike displayItem, String title) {
//            return advancement(parent, displayItem, title, FrameType.CHALLENGE, true);
//        }
//
//        protected Advancement.Builder goal(AdvancementHolder parent, ItemLike displayItem, String title) {
//            return advancement(parent, displayItem, title, FrameType.GOAL, true);
//        }
//
//        protected Advancement.Builder root(ItemLike displayItem, String title, ResourceLocation location) {
//            return advancement(null, displayItem, title, location, FrameType.TASK, true);
//        }
//
//        protected Advancement.Builder advancement(AdvancementHolder parent, ItemLike displayItem, String title, FrameType type, boolean displayChat) {
//            return advancement(parent, displayItem, title, null, type, displayChat);
//        }
//
//        protected Advancement.Builder advancement(AdvancementHolder parent, ItemLike displayItem, String title, ResourceLocation location, FrameType type, boolean displayChat) {
//            return advancement(parent, displayItem, title, location, type, true, displayChat, false);
//        }
//
//        protected Advancement.Builder advancement(AdvancementHolder parent, ItemLike displayItem, String title, ResourceLocation location, FrameType type, boolean showToast, boolean displayChat, boolean hidden) {
//            return Advancement.Builder.advancement().parent(parent).display(displayItem, title(title), desc(title), location, type, showToast, displayChat, hidden);
//        }
//
//        protected MutableComponent title(String name) {
//            return Component.translatable("advancements." + this.getModId() + "." + name + ".title");
//        }
//
//        protected MutableComponent desc(String name) {
//            return Component.translatable("advancements." + this.getModId() + "." + name + ".desc");
//        }
//
//        protected String getModId() {
//            return this.modId;
//        }
//    }
}
