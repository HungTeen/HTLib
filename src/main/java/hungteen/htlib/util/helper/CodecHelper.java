package hungteen.htlib.util.helper;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapLike;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-04-05 08:44
 **/
public class CodecHelper {

    private static final Codec<MutableComponent> COMPONENT_CODEC = Codec.STRING.xmap(Component.Serializer::fromJson, Component.Serializer::toJson);

    public static final MapLike<Object> EMPTY = new MapLike<>() {
        @Nullable
        @Override
        public Object get(final Object key) {
            return null;
        }

        @Nullable
        @Override
        public Object get(final String key) {
            return null;
        }

        @Override
        public Stream<Pair<Object, Object>> entries() {
            return Stream.empty();
        }

        @Override
        public String toString() {
            return "EmptyMapLike";
        }
    };

    public static <T> DataResult<Tag> encodeNbt(Codec<T> codec, T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value);
    }

    public static <T> DataResult<JsonElement> encodeJson(Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value);
    }

    public static <T> DataResult<T> parse(Codec<T> codec, Tag tag) {
        return codec.parse(NbtOps.INSTANCE, tag);
    }

    public static <T> DataResult<T> parse(Codec<T> codec, JsonElement element) {
        return codec.parse(JsonOps.INSTANCE, element);
    }

    public static Codec<MutableComponent> componentCodec(){
        return COMPONENT_CODEC;
    }

    @SuppressWarnings("unchecked")
    public static <T> MapLike<T> emptyMapLike() {
        return (MapLike<T>) EMPTY;
    }

}
