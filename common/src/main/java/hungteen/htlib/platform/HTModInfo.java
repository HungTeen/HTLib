package hungteen.htlib.platform;

/**
 * 兼容不同 mod 加载器的 mod 容器，不同平台可通过装饰器包装。
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 15:40
 **/
public interface HTModInfo {

    /**
     * @return 模组 id。
     */
    String getModId();

}
