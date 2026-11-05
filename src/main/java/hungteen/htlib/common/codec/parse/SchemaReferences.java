package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Resolves the editor's local schema references into a shared, read-only JSON DAG. */
public final class SchemaReferences {
    private static final String PREFIX = "#/$defs/";
    private static final int MAX_DEPTH = 256;
    private static final Set<String> OVERRIDES = Set.of("name", "required", "default");

    private SchemaReferences() {
    }

    /** Legacy trees are returned unchanged; compact documents never expand shared subtrees repeatedly. */
    public static JsonObject expand(JsonObject document) {
        if (!document.has("$defs") && !document.has("$ref")) return document;
        if (!document.has("$defs") || !document.get("$defs").isJsonObject()
            || !document.has("$ref") || document.size() != 2) {
            throw new IllegalArgumentException("Expected a schema $ref and $defs document");
        }
        Resolver resolver = new Resolver(document.getAsJsonObject("$defs"));
        // Validate unused definitions as well, so malformed/cyclic references cannot hide in the document.
        for (String id : resolver.definitions.keySet()) resolver.definition(id, 0);
        JsonObject root = new JsonObject();
        root.add("$ref", document.get("$ref"));
        return resolver.node(root, 0);
    }

    private static final class Resolver {
        private final JsonObject definitions;
        private final Map<String, JsonObject> resolved = new HashMap<>();
        private final Set<String> active = new HashSet<>();

        private Resolver(JsonObject definitions) {
            this.definitions = definitions;
            for (var entry : definitions.entrySet()) {
                if (!entry.getKey().matches("[A-Za-z0-9_-]+") || !entry.getValue().isJsonObject()) {
                    throw new IllegalArgumentException("Invalid schema definition: " + entry.getKey());
                }
            }
        }

        private JsonObject definition(String id, int depth) {
            JsonObject cached = resolved.get(id);
            if (cached != null) return cached;
            if (!definitions.has(id)) throw new IllegalArgumentException("Unknown schema reference: " + id);
            if (!active.add(id)) throw new IllegalArgumentException("Cyclic schema reference: " + id);
            try {
                JsonObject value = node(definitions.getAsJsonObject(id), depth + 1);
                resolved.put(id, value);
                return value;
            } finally {
                active.remove(id);
            }
        }

        private JsonObject node(JsonObject source, int depth) {
            if (depth > MAX_DEPTH) throw new IllegalArgumentException("Schema references are too deeply nested");
            if (source.has("$defs")) throw new IllegalArgumentException("Nested schema definitions are not supported");
            if (source.has("$ref")) {
                JsonElement ref = source.get("$ref");
                if (!ref.isJsonPrimitive() || !ref.getAsJsonPrimitive().isString()
                    || !ref.getAsString().startsWith(PREFIX)) {
                    throw new IllegalArgumentException("Only local schema references are supported");
                }
                String id = ref.getAsString().substring(PREFIX.length());
                if (!id.matches("[A-Za-z0-9_-]+")) throw new IllegalArgumentException("Invalid schema reference: " + id);
                for (String key : source.keySet()) {
                    if (!key.equals("$ref") && !OVERRIDES.contains(key)) {
                        throw new IllegalArgumentException("Unsupported schema reference override: " + key);
                    }
                }
                if (source.has("name") && (!source.get("name").isJsonPrimitive()
                    || !source.getAsJsonPrimitive("name").isString())) {
                    throw new IllegalArgumentException("Schema variant name must be a string");
                }
                if (source.has("required") && (!source.get("required").isJsonPrimitive()
                    || !source.getAsJsonPrimitive("required").isBoolean())) {
                    throw new IllegalArgumentException("Schema required flag must be boolean");
                }
                JsonObject base = definition(id, depth + 1);
                if (source.size() == 1) return base;
                JsonObject overlay = shallowCopy(base);
                for (String key : OVERRIDES) if (source.has(key)) overlay.add(key, source.get(key));
                return overlay;
            }

            JsonObject target = shallowCopy(source);
            for (String key : Set.of("element", "key", "value", "typeKey")) {
                if (source.has(key)) target.add(key, node(object(source.get(key), key), depth + 1));
            }
            if (source.has("fields")) {
                JsonArray fields = new JsonArray();
                for (JsonElement entry : array(source.get("fields"), "fields")) {
                    JsonObject field = object(entry, "field");
                    JsonObject copy = shallowCopy(field);
                    copy.add("schema", node(object(field.get("schema"), "field schema"), depth + 1));
                    fields.add(copy);
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
    }

    private static JsonObject shallowCopy(JsonObject object) {
        JsonObject copy = new JsonObject();
        object.entrySet().forEach(entry -> copy.add(entry.getKey(), entry.getValue()));
        return copy;
    }

    private static JsonObject object(JsonElement element, String label) {
        if (element == null || !element.isJsonObject()) throw new IllegalArgumentException("Expected object: " + label);
        return element.getAsJsonObject();
    }

    private static JsonArray array(JsonElement element, String label) {
        if (element == null || !element.isJsonArray()) throw new IllegalArgumentException("Expected array: " + label);
        return element.getAsJsonArray();
    }
}
