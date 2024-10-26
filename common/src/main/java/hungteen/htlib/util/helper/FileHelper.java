package hungteen.htlib.util.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-30 20:58
 **/
public interface FileHelper {

    /**
     * Get all filtered files in the folder.
     * @param root the root folder.
     * @param filter the filter.
     * @return the list of files.
     */
    static List<File> getAllFiles(File root, FileFilter filter) {
        final List<File> files = new ArrayList<>();
        // getCodecRegistry the folder list
        final File[] array = root.listFiles();
        if(array != null){
            for (File file : array) {
                if (file.isDirectory()) {
                    files.addAll(getAllFiles(file, filter));
                } else if(filter.accept(file)){
                    files.add(file);
                }
            }
        }
        return files;
    }

    static void findFiles(List<Path> paths, String base, Predicate<Path> rootFilter,
                          BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles) {
        findFiles(paths, base, rootFilter, processor, visitAllFiles, Integer.MAX_VALUE);
    }

    /**
     * Find files in the paths.
     * @param paths a list of paths.
     * @param base the base path.
     * @param rootFilter the root filter.
     * @param processor the processor function, return keep finding or not.
     * @param visitAllFiles whether to visit all files.
     * @param maxDepth the max depth to visit files.
     */
    static void findFiles(List<Path> paths, String base, Predicate<Path> rootFilter,
                          BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth) {
        try {
            for (var root : paths) {
                walk(root.resolve(base), rootFilter, processor, visitAllFiles, maxDepth);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor,
                             boolean visitAllFiles, int maxDepth) throws IOException {
        if (root == null || !Files.exists(root) || !rootFilter.test(root)) {
            return;
        }

        if (processor != null) {
            try (var stream = Files.walk(root, maxDepth)) {
                Iterator<Path> itr = stream.iterator();

                while (itr.hasNext()) {
                    boolean keepGoing = processor.apply(root, itr.next());

                    if (!visitAllFiles && !keepGoing) {
                        return;
                    }
                }
            }
        }
    }

}
