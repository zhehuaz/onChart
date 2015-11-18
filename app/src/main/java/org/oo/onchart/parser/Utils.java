package org.oo.onchart.parser;

import org.oo.onchart.R;

/**
 * Created by langley on 11/17/15.
 */
public class Utils {
    public static int parseIndexFromWeekday(char c) {
        switch (c) {
            case '一' :
                return 0;
            case '二' :
                return 1;
            case '三' :
                return 2;
            case '四':
                return 3;
            case '五':
                return 4;
            case '六':
                return 5;
            case '日':
                return 6;
            default:
                return -1;
        }
    }

    public final static int weekdayFromIndex[] = {
            R.string.weekday_Mon,
            R.string.weekday_Tue,
            R.string.weekday_Wed,
            R.string.weekday_Thi,
            R.string.weekday_Fri,
            R.string.weekday_Sat,
            R.string.weekday_Sun
    };
}
