package hungteen.htlib.client.util;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2024/12/2 23:03
 **/
public enum ModelLayerType {

    MAIN("main"),

    INNER_ARMOR("inner_armor"),

    OUTER_ARMOR("outer_armor"),

    MAIN_SLIM("main"),

    INNER_ARMOR_SLIM("inner_armor"),

    OUTER_ARMOR_SLIM("outer_armor")

    ;

    private final String type;

    ModelLayerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
