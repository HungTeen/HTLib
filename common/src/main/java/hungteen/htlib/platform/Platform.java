package hungteen.htlib.platform;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/25 17:06
 **/
public enum Platform {

    NEOFORGE("neoforge"),

    FORGE("forge"),

    FABRIC("fabric"),

    ;

    private final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
