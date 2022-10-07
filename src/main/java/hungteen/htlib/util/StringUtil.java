package hungteen.htlib.util;

import hungteen.htlib.HTLib;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:42
 **/
public class StringUtil {

    private static final List<String> ROMAN_NUMBERS = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");

    public static String getRomanString(int num){
        if(num > 0 && num <= 10){
            return ROMAN_NUMBERS.get(num - 1);
        }
        HTLib.getLogger().warn("Invalid number {}", num);
        return "";
    }

}
