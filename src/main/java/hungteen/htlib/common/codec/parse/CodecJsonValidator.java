package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.io.IOException;
import java.io.StringReader;

/** 保存前使用原始 Codec 校验，不能只依赖推导的 schema。 */
final class CodecJsonValidator {

    private CodecJsonValidator() {
    }

    static JsonElement parseAndValidate(Codec<?> codec, DynamicOps<JsonElement> ops, String json) {
        JsonElement value;
        try (JsonReader reader = new JsonReader(new StringReader(json))) {
            reader.setLenient(false);
            // JsonParser.parseString 会启用宽松解析，这里只接受严格 JSON。
            value = Streams.parse(reader);
            if (value.isJsonNull() || reader.peek() != JsonToken.END_DOCUMENT) {
                throw new IllegalArgumentException("Expected one non-null JSON value");
            }
        } catch (IOException | JsonParseException ex) {
            throw new IllegalArgumentException("Invalid JSON: " + ex.getMessage(), ex);
        }
        DataResult<?> result = codec.parse(ops, value);
        if (result.error().isPresent()) {
            throw new IllegalArgumentException(result.error().get().message());
        }
        if (result.result().isEmpty()) {
            throw new IllegalArgumentException("The selected Codec could not decode this value");
        }
        return value;
    }
}
