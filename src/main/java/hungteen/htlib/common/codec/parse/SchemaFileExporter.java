package hungteen.htlib.common.codec.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** 将注册表结构导出到 schema_output，保留 worldgen/... 等嵌套路径。 */
public final class SchemaFileExporter {

    private SchemaFileExporter() {
    }

    public static Path write(Path directory, String namespace, String registryPath, String json) throws IOException {
        if (namespace == null || !namespace.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid registry namespace");
        }
        if (registryPath == null || !registryPath.matches("[a-z0-9/._-]+")) {
            throw new IllegalArgumentException("Invalid registry path");
        }
        for (String part : registryPath.split("/", -1)) {
            if (part.isEmpty() || part.equals(".") || part.equals("..")) {
                throw new IllegalArgumentException("Registry paths must not contain empty, '.' or '..' segments");
            }
        }
        Files.createDirectories(directory);
        Path root = directory.toRealPath();
        Path file = root.resolve(namespace + "_" + registryPath + ".json").normalize();
        if (!file.startsWith(root)) {
            throw new IllegalArgumentException("Schema output must remain inside schema_output");
        }
        DatapackFileStorage.createDirectories(root, file.getParent());
        DatapackFileStorage.checkFile(root, file);
        Files.writeString(file, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS);
        return file;
    }
}
