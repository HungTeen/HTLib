package hungteen.htlib.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:07
 **/
public abstract class HTAdvancementGen extends AdvancementProvider {

    private final String modId;

    public HTAdvancementGen(DataGenerator generatorIn, String modId, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
        this.modId = modId;
    }

    protected Advancement.Builder task(Advancement parent, ItemLike displayItem, String title) {
        return advancement(parent, displayItem, title, FrameType.TASK, true);
    }

    protected Advancement.Builder challenge(Advancement parent, ItemLike displayItem, String title) {
        return advancement(parent, displayItem, title, FrameType.CHALLENGE, true);
    }

    protected Advancement.Builder goal(Advancement parent, ItemLike displayItem, String title) {
        return advancement(parent, displayItem, title, FrameType.GOAL, true);
    }

    protected Advancement.Builder root(ItemLike displayItem, String title, ResourceLocation location) {
        return advancement(null, displayItem, title, location, FrameType.TASK, true);
    }

    protected Advancement.Builder advancement(Advancement parent, ItemLike displayItem, String title, FrameType type, boolean displayChat) {
        return advancement(parent, displayItem, title, null, type, displayChat);
    }

    protected Advancement.Builder advancement(Advancement parent, ItemLike displayItem, String title, ResourceLocation location, FrameType type, boolean displayChat) {
        return Advancement.Builder.advancement().parent(parent).display(displayItem, title(title), desc(title), location, type, true, displayChat, false);
    }

    protected MutableComponent title(String name) {
        return Component.translatable("advancements." + this.modId + "." + name + ".title");
    }

    protected MutableComponent desc(String name) {
        return Component.translatable("advancements." + this.modId + "." + name + ".desc");
    }
}
