package hungteen.htlib.util.helper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 15:07
 */
public class ForgeHelper {

    private static final IModIDHelper HELPER = () -> IModIDHelper.FORGE;

    public static IModIDHelper get(){
        return HELPER;
    }

}
