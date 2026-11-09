package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 解析网络 Schema 的本地引用。同一定义的子结构在客户端共享，只读使用。 */
public final class SchemaReferences {

    private static final String PREFIX = "#/$defs/";
    private static final int MAX_DEPTH = 256;
    private static final List<String> CHILD_KEYS = List.of("element", "key", "value", "typeKey");

    private final JsonObject definitions;
    private final Map<String, JsonObject> resolved = new HashMap<>();

    private SchemaReferences(JsonObject definitions) {
        this.definitions = definitions;
    }

    public static JsonObject expand(JsonObject document) {
        if (!document.has("$defs") && !document.has("$ref")) {
            return document;
        }
        if (!document.has("$ref") || document.size() != 2) {
            throw new IllegalArgumentException("Expected a schema $ref and $defs document");
        }
        JsonObject definitions = object(document.get("$defs"), "$defs");
        SchemaReferences graph = new SchemaReferences(definitions);
        for (String id : definitions.keySet()) {
            if (!id.matches("[A-Za-z0-9_-]+")) {
                throw new IllegalArgumentException("Invalid schema definition: " + id);
            }
            graph.definition(id, 0);
        }
        return graph.definition(referenceId(document.get("$ref")), 0);
    }

    private static String referenceId(JsonElement ref) {
        if (!ref.isJsonPrimitive() || !ref.getAsJsonPrimitive().isString() || !ref.getAsString().startsWith(PREFIX)) {
            throw new IllegalArgumentException("Only local schema references are supported");
        }
        return ref.getAsString().substring(PREFIX.length());
    }

    private JsonObject definition(String id, int depth) {
        if (resolved.containsKey(id)) {
            JsonObject cached = resolved.get(id);
            if (cached == null) {
                throw new IllegalArgumentException("Cyclic schema reference: " + id);
            }
            return cached;
        }
        if (!definitions.has(id)) {
            throw new IllegalArgumentException("Unknown schema reference: " + id);
        }
        resolved.put(id, null); // 此定义尚在当前递归路径中。
        JsonObject value = node(object(definitions.get(id), id), depth + 1);
        resolved.put(id, value);
        return value;
    }

    private JsonObject node(JsonObject source, int depth) {
        if (depth > MAX_DEPTH || source.has("$defs")) {
            throw new IllegalArgumentException("Invalid schema nesting");
        }
        if (source.has("$ref")) {
            JsonObject base = definition(referenceId(source.get("$ref")), depth + 1);
            if (source.size() == 1) {
                return base;
            }
            JsonObject overlay = shallowCopy(base);
            for (var entry : source.entrySet()) {
                String key = entry.getKey();
                if (key.equals("$ref")) {
                    continue;
                }
                JsonElement value = entry.getValue();
                boolean valid = switch (key) {
                    case "name" -> value.isJsonPrimitive() && value.getAsJsonPrimitive().isString();
                    case "required" -> value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
                    case "default" -> true;
                    default -> false;
                };
                if (!valid) {
                    throw new IllegalArgumentException("Invalid schema reference override: " + key);
                }
                overlay.add(key, value);
            }
            return overlay;
        }

        // 只解析 Schema 子节点，default 等字段里的 JSON 是普通数据。
        JsonObject target = shallowCopy(source);
        for (String key : CHILD_KEYS) {
            if (source.has(key)) {
                target.add(key, node(object(source.get(key), key), depth + 1));
            }
        }
        if (source.has("fields")) {
            JsonArray fields = new JsonArray();
            for (JsonElement entry : array(source.get("fields"), "fields")) {
                JsonObject field = shallowCopy(object(entry, "field"));
                field.add("schema", node(object(field.get("schema"), "field schema"), depth + 1));
                fields.add(field);
            }
            target.add("fields", fields);
        }
        if (source.has("variants")) {
            JsonArray variants = new JsonArray();
            for (JsonElement variant : array(source.get("variants"), "variants")) {
                variants.add(node(object(variant, "variant"), depth + 1));
            }
            target.add("variants", variants);
        }
        return target;
    }

    private static JsonObject shallowCopy(JsonObject object) {
        JsonObject copy = new JsonObject();
        object.entrySet().forEach(entry -> copy.add(entry.getKey(), entry.getValue()));
        return copy;
    }

    private static JsonObject object(JsonElement element, String label) {
        if (element == null || !element.isJsonObject()) {
            throw new IllegalArgumentException("Expected object: " + label);
        }
        return element.getAsJsonObject();
    }

    private static JsonArray array(JsonElement element, String label) {
        if (element == null || !element.isJsonArray()) {
            throw new IllegalArgumentException("Expected array: " + label);
        }
        return element.getAsJsonArray();
    }
}
