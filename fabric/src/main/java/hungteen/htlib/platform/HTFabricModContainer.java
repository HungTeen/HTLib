package hungteen.htlib.platform;

import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.List;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 15:45
 **/
public class HTFabricModContainer implements HTModContainer {

    private final ModContainer modContainer;

    public HTFabricModContainer(ModContainer modContainer) {
        this.modContainer = modContainer;
    }

    @Override
    public String getModId() {
        return modContainer.getMetadata().getId();
    }

    @Override
    public String getName() {
        return modContainer.getMetadata().getName();
    }

    @Override
    public Path getPath(String s) {
        return modContainer.getPath(s);
    }

    @Override
    public List<Path> getRootPaths() {
        return modContainer.getRootPaths();
    }
}
