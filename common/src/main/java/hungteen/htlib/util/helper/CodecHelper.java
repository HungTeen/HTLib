package hungteen.htlib.util.helper;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-04-05 08:44
 **/
public interface CodecHelper {

    Codec<MutableComponent> COMPONENT_CODEC = Codec.STRING.xmap(Component::translatable, MutableComponent::toString);

    static <T> DataResult<Tag> encodeNbt(Codec<T> codec, T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value);
    }

    static <T> DataResult<JsonElement> encodeJson(Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value);
    }

    static <T> DataResult<T> parse(Codec<T> codec, Tag tag) {
        return codec.parse(NbtOps.INSTANCE, tag);
    }

    static <T> DataResult<T> parse(Codec<T> codec, JsonElement element) {
        return codec.parse(JsonOps.INSTANCE, element);
    }

    static Codec<MutableComponent> componentCodec(){
        return COMPONENT_CODEC;
    }

}
