package hungteen.htlib.util.helper;

import java.time.LocalDate;
import java.time.Month;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 23:08
 **/
public interface DateHelper {

    static boolean isHalloween(){
        return getMonth() == Month.OCTOBER && getDay() == 31;
    }

    static boolean isNewYear(){
        return getMonth() == Month.JANUARY && getDay() == 1;
    }

    static boolean isChristmasDay(){
        return getMonth() == Month.DECEMBER && getDay() == 25;
    }

    static int getDay(){
        return getDate().getDayOfMonth();
    }

    static Month getMonth(){
        return getDate().getMonth();
    }

    static int getYear(){
        return getDate().getYear();
    }

    static LocalDate getDate(){
        return LocalDate.now();
    }

//    public static void getLunar(){
//        ChineseCalendar calendar = new ChineseCalendar(Locale.CHINA);
//        System.out.println(calendar.getCodecRegistry(Calendar.YEAR) + " - " + calendar.getCodecRegistry(Calendar.MONTH));
//    }

}
