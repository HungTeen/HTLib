package hungteen.htlib.common.codec.parse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SchemaFileExporterTest {

    @TempDir
    Path directory;

    @Test
    void exportsNestedRegistryPathsAndKeepsUtf8Content() throws IOException {
        String json = "{\"type\":\"RECORD\",\"description\":\"结构\"}";
        Path output = SchemaFileExporter.write(directory, "minecraft", "worldgen/configured_feature", json);
        assertEquals(directory.toRealPath().resolve("minecraft_worldgen/configured_feature.json"), output);
        assertEquals(json, Files.readString(output));
    }

    @Test
    void replacesAnExistingSchemaWithoutRetainingOldSuffix() throws IOException {
        SchemaFileExporter.write(directory, "minecraft", "damage_type", "{\"fields\":[1,2,3]}");
        Path output = SchemaFileExporter.write(directory, "minecraft", "damage_type", "{}");
        assertEquals("{}", Files.readString(output));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../outside", "worldgen/../../outside", "/absolute", "worldgen//feature", "worldgen/./feature", "worldgen/"})
    void rejectsTraversalAndEmptySegmentsBeforeCreatingOutput(String path) throws IOException {
        Path output = directory.resolve("schema_output");
        assertThrows(IllegalArgumentException.class,
            () -> SchemaFileExporter.write(output, "minecraft", path, "{}"));
        assertFalse(Files.exists(output));
    }

    @Test
    void rejectsParentFilesWithoutChangingThem() throws IOException {
        Path parent = directory.resolve("minecraft_worldgen");
        Files.writeString(parent, "preserve");
        assertThrows(IOException.class,
            () -> SchemaFileExporter.write(directory, "minecraft", "worldgen/configured_feature", "{}"));
        assertEquals("preserve", Files.readString(parent));
    }
}
