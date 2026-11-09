package hungteen.htlib.common.codec.parse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CodecJsonValidatorTest {

    private static final Codec<Integer> COUNT = Codec.intRange(1, 10).fieldOf("count").codec();

    @Test
    void acceptsCodecValidDataWithoutReencodingOrDroppingUnknownFields() {
        var json = CodecJsonValidator.parseAndValidate(COUNT, JsonOps.INSTANCE, "{\"count\":2,\"extra\":\"keep me\"}");
        assertEquals(2, json.getAsJsonObject().get("count").getAsInt());
        assertEquals("keep me", json.getAsJsonObject().get("extra").getAsString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"count\":11}", "{\"count\":\"two\"}", "[]"})
    void rejectsMissingWrongTypeAndOutOfRangeValues(String json) {
        assertThrows(IllegalArgumentException.class,
            () -> CodecJsonValidator.parseAndValidate(COUNT, JsonOps.INSTANCE, json));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "null", "{count:2}", "{'count':2}", "{\"count\":2} {}", "/* comment */ {\"count\":2}", "{\"count\":2,}"})
    void rejectsMalformedOrLenientJson(String json) {
        assertThrows(IllegalArgumentException.class,
            () -> CodecJsonValidator.parseAndValidate(COUNT, JsonOps.INSTANCE, json));
    }

    @Test
    void rejectsPartialCodecResultsInsteadOfSavingInvalidDrafts() {
        Codec<String> partial = Codec.STRING.flatXmap(
            value -> DataResult.error(() -> "Unsupported value", value), DataResult::success);
        var exception = assertThrows(IllegalArgumentException.class,
            () -> CodecJsonValidator.parseAndValidate(partial, JsonOps.INSTANCE, "\"draft\""));
        assertEquals("Unsupported value", exception.getMessage());
    }
}
