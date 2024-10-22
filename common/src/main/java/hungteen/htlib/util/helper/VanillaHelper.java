package hungteen.htlib.util.helper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 15:04
 */
public class VanillaHelper {

    private static final IModIDHelper HELPER = () -> IModIDHelper.MC;

    public static IModIDHelper get(){
        return HELPER;
    }
}
