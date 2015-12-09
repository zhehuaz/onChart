package me.zchang.onchart.parser;

import me.zchang.onchart.R;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

    public static String timeFromPeriod(int i) {
        switch (i) {
            case 1:
                return "08:00";
            case 2:
                return "08:50";
            case 3:
                return "09:50";
            case 4:
                return "10:40";
            case 5:
                return "11:30";
            case 6:
                return "13:20";
            case 7:
                return "14:10";
            case 8:
                return "15:10";
            case 9:
                return "16:10";
            case 10:
                return "16:50";
            case 11:
                return "18:30";
            case 12:
                return "19:20";
            case 13:
                return "20:10";
            default:
                return "";
        }
    }

}
