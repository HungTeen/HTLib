package hungteen.htlib.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2025/6/4 18:27
 **/
public record RaidItemSetting(Optional<String> name, ResourceLocation model, int maxStackSize,
                              List<Integer> colors,
                              List<String> textComponents) {

    public static final Codec<RaidItemSetting> CODEC = RecordCodecBuilder.<RaidItemSetting>mapCodec(instance -> instance.group(
            Codec.optionalField("name", Codec.STRING).forGetter(RaidItemSetting::name),
            ResourceLocation.CODEC.fieldOf("model").forGetter(RaidItemSetting::model),
            Codec.intRange(0, 1023).optionalFieldOf("max_stack_size", 1).forGetter(RaidItemSetting::maxStackSize),
            Codec.intRange(0, Integer.MAX_VALUE).listOf().optionalFieldOf("colors", List.of()).forGetter(RaidItemSetting::colors),
            Codec.STRING.listOf().optionalFieldOf("texts", new ArrayList<>()).forGetter(RaidItemSetting::textComponents)
    ).apply(instance, RaidItemSetting::new)).codec();

    public static final RaidItemSetting DEFAULT = builder().build();

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String name = null;
        private ResourceLocation model = HTLibHelper.prefix("raid_envelope");
        private int maxStackSize = 1;
        private List<Integer> colors = new ArrayList<>();
        private List<String> textComponents = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder modelNameTip(String name){
            return this.name(HTLibHelper.get().langKey("item", name))
                    .model(HTLibHelper.prefix(name))
                    .texts(java.util.List.of(HTLibHelper.get().langKey("tip", name)));
        }

        public Builder model(ResourceLocation model) {
            this.model = model;
            return this;
        }

        public Builder stack(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public Builder colors(List<Integer> colors) {
            this.colors = colors;
            return this;
        }

        public Builder texts(List<String> textComponents) {
            this.textComponents = textComponents;
            return this;
        }

        public RaidItemSetting build() {
            return new RaidItemSetting(Optional.ofNullable(name), model, maxStackSize, colors, textComponents);
        }
    }
}
