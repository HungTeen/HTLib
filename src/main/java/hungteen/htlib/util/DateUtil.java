package hungteen.htlib.util;

import com.ibm.icu.util.ChineseCalendar;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:08
 **/
public class DateUtil {

    public static boolean isHalloween(){
        return getMonth() == 10 && getDay() == 31;
    }

    public static boolean isNewYear(){
        return getMonth() == 1 && getDay() == 1;
    }

    public static boolean isChristmasDay(){
        return getMonth() == 12 && getDay() == 25;
    }

    private static int getDay(){
        return getDate().get(ChronoField.DAY_OF_MONTH);
    }

    private static int getMonth(){
        return getDate().get(ChronoField.MONTH_OF_YEAR);
    }

    private static int getYear(){
        return getDate().get(ChronoField.YEAR);
    }

    private static LocalDate getDate(){
        return LocalDate.now();
    }

//    public static void getLunar(){
//        ChineseCalendar calendar = new ChineseCalendar(Locale.CHINA);
//        System.out.println(calendar.get(Calendar.YEAR) + " - " + calendar.get(Calendar.MONTH));
//    }

}
