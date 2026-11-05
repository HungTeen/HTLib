package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.common.codec.parse.SchemaType;

/** Keep invalid numeric drafts editable; never silently coerce them to zero or truncate them. */
final class ScalarInput {
    private ScalarInput() {
    }

    static boolean isNumeric(SchemaType type) {
        return switch (type) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> true;
            default -> false;
        };
    }

    static JsonElement parse(String text, SchemaType type) {
        String number = text.trim();
        try {
            return switch (type) {
                case BYTE -> new JsonPrimitive(Byte.parseByte(number));
                case SHORT -> new JsonPrimitive(Short.parseShort(number));
                case INT -> new JsonPrimitive(Integer.parseInt(number));
                case LONG -> new JsonPrimitive(Long.parseLong(number));
                case FLOAT -> {
                    float value = Float.parseFloat(number);
                    yield Float.isFinite(value) ? new JsonPrimitive(value) : new JsonPrimitive(text);
                }
                case DOUBLE -> {
                    double value = Double.parseDouble(number);
                    yield Double.isFinite(value) ? new JsonPrimitive(value) : new JsonPrimitive(text);
                }
                default -> new JsonPrimitive(text);
            };
        } catch (NumberFormatException e) {
            return new JsonPrimitive(text);
        }
    }

    static boolean isValidNumber(JsonElement value, SchemaType type) {
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()
            && parse(value.getAsString(), type).getAsJsonPrimitive().isNumber();
    }
}
