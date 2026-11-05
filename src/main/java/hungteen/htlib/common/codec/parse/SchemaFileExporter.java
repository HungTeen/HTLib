package hungteen.htlib.common.codec.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Exports a schema beneath schema_output, including registries with nested paths. */
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
        Path current = root;
        for (Path part : root.relativize(file.getParent())) {
            current = current.resolve(part);
            if (!Files.exists(current, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectory(current);
            }
            if (!Files.isDirectory(current, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("Schema output parent is not a directory");
            }
            checkLocation(root, current);
        }
        if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("Schema output is not a regular file");
            }
            checkLocation(root, file);
        }
        Files.writeString(file, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS);
        return file;
    }

    private static void checkLocation(Path root, Path path) throws IOException {
        if (Files.isSymbolicLink(path) || !path.toRealPath().startsWith(root)) {
            throw new IOException("Schema output must not follow linked paths outside schema_output");
        }
    }
}
