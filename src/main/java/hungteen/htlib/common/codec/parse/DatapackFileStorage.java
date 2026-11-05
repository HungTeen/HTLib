package hungteen.htlib.common.codec.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/** Writes editor output beneath the world's datapacks directory, never to a client-supplied path. */
public final class DatapackFileStorage {

    private static final String PACK_METADATA = """
        {
          "pack": {
            "pack_format": 15,
            "description": "Created with HTLib Codec Editor"
          }
        }
        """;

    private DatapackFileStorage() {
    }

    /** Forge prefixes non-vanilla registry directories with their namespace. */
    public static Path relativePath(String packName, String registryNamespace, String registryPath,
                                    String resourceNamespace, String resourcePath) {
        requirePackName(packName);
        requireNamespace(registryNamespace);
        requireNamespace(resourceNamespace);
        requireResourcePath(registryPath);
        requireResourcePath(resourcePath);
        Path directory = Path.of(packName, "data", resourceNamespace);
        if (!"minecraft".equals(registryNamespace)) {
            directory = directory.resolve(registryNamespace);
        }
        return directory.resolve(registryPath).resolve(resourcePath + ".json");
    }

    public static Path save(Path datapacksDirectory, String packName, String registryNamespace, String registryPath,
                            String resourceNamespace, String resourcePath, String json) throws IOException {
        Path relative = relativePath(packName, registryNamespace, registryPath, resourceNamespace, resourcePath);
        Files.createDirectories(datapacksDirectory);
        Path root = datapacksDirectory.toRealPath();
        Path target = root.resolve(relative).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("The resource must stay inside the datapacks directory");
        }
        createDirectories(root, target.getParent());
        Path metadata = root.resolve(packName).resolve("pack.mcmeta");
        checkFile(root, metadata);
        checkFile(root, target);
        if (!Files.exists(metadata, LinkOption.NOFOLLOW_LINKS)) {
            // Never replace metadata belonging to an existing pack.
            Files.writeString(metadata, PACK_METADATA, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        }
        Path temporary = Files.createTempFile(target.getParent(), ".ide-", ".tmp");
        try {
            Files.writeString(temporary, json, StandardCharsets.UTF_8);
            checkFile(root, target);
            // Fail safely if atomic replacement is unsupported by the filesystem.
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
        return target;
    }

    private static void createDirectories(Path root, Path directory) throws IOException {
        Path current = root;
        for (Path part : root.relativize(directory)) {
            current = current.resolve(part);
            if (!Files.exists(current, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectory(current);
            }
            checkLocation(root, current);
            if (!Files.isDirectory(current, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("A datapack directory is not a directory: " + part);
            }
        }
    }

    private static void checkFile(Path root, Path file) throws IOException {
        if (Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
            checkLocation(root, file);
            if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("The datapack target is not a regular file");
            }
        }
    }

    private static void checkLocation(Path root, Path path) throws IOException {
        // Real paths also catch Windows junctions, which are not reported as symbolic links.
        if (Files.isSymbolicLink(path) || !path.toRealPath().startsWith(root)) {
            throw new IOException("Linked paths outside the datapack directory are not supported");
        }
    }

    private static void requireNamespace(String namespace) {
        if (namespace == null || !namespace.matches("[a-z0-9_.-]+")
            || namespace.equals(".") || namespace.equals("..")) {
            throw new IllegalArgumentException("Invalid resource namespace");
        }
    }

    /** A pack name is a single directory, independent of the resource namespace. */
    private static void requirePackName(String name) {
        if (name == null || name.isBlank() || !name.equals(name.strip()) || name.endsWith(".")
            || name.equals(".") || name.equals("..") || !name.matches("[^/\\\\:*?\"<>|\\p{Cntrl}]+")) {
            throw new IllegalArgumentException("Invalid datapack name: use one directory name without path separators");
        }
    }

    private static void requireResourcePath(String path) {
        if (path == null || !path.matches("[a-z0-9/._-]+")) {
            throw new IllegalArgumentException("Invalid resource path");
        }
        for (String part : path.split("/", -1)) {
            if (part.isEmpty() || part.equals(".") || part.equals("..")) {
                throw new IllegalArgumentException("Resource paths must not contain empty, '.' or '..' segments");
            }
        }
    }
}
