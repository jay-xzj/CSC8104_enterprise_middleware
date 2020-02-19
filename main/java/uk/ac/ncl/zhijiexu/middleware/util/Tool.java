package uk.ac.ncl.zhijiexu.middleware.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author JayXu
 * @description:
 * @date 2019/11/20 10:24
 */
public class Tool {

    public static void main(String[] args) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.YEAR,2020);
        instance.set(Calendar.MONTH,12);
        instance.set(Calendar.DATE,25);
        Date time = instance.getTime();
        long mili = time.getTime();
        System.out.println(mili);


    }
}
