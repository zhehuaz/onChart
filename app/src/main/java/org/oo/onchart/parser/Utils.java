package org.oo.onchart.parser;

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


}
