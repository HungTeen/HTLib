package hungteen.htlib.platform;

import java.nio.file.Path;
import java.util.List;

/**
 * 兼容不同 mod 加载器的 mod 容器，不同平台可通过装饰器包装。
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 15:40
 **/
public interface HTModContainer {

    /**
     * @return 模组 id。
     */
    String getModId();

    /**
     * @return 模组名称。
     */
    String getName();

    /**
     * @return 资源路径。
     */
    Path getPath(String s);

    /**
     * @return 根路径。
     */
    List<Path> getRootPaths();
}
