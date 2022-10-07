package hungteen.htlib.util;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:08
 **/
public class DateUtil {

    public static boolean isHalloween(){
        return getMonth() == 10 && getDay() == 31;
    }

    private static int getDay(){
        return getDate().get(ChronoField.DAY_OF_MONTH);
    }

    private static int getMonth(){
        return getDate().get(ChronoField.MONTH_OF_YEAR);
    }

    private static LocalDate getDate(){
        return LocalDate.now();
    }

}
