package hungteen.htlib.api.util;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/25 17:06
 **/
public enum Platform {

    MINECRAFT("minecraft"),

    NEOFORGE("neoforge"),

    FORGE("forge"),

    FABRIC("fabric"),

    UNIFORM("c"),

    ;

    private final String namespace;

    Platform(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

}
