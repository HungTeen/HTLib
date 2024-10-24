package hungteen.htlib.util.helper;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-30 20:58
 **/
public interface FileHelper {

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

}
