package hungteen.htlib.platform;

import net.minecraftforge.fml.ModContainer;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 15:45
 **/
public class HTForgeModContainer implements HTModContainer {

    private final ModContainer modContainer;

    public HTForgeModContainer(ModContainer modContainer) {
        this.modContainer = modContainer;
    }

    @Override
    public String getModId() {
        return modContainer.getModId();
    }

    @Override
    public String getName() {
        return modContainer.getModInfo().getDisplayName();
    }

    @Override
    public Path getPath(String s) {
        return modContainer.getModInfo().getOwningFile().getFile().findResource(s);
    }

    @Override
    public List<Path> getRootPaths() {
        return Collections.singletonList(modContainer.getModInfo().getOwningFile().getFile().getSecureJar().getRootPath());
    }
}
