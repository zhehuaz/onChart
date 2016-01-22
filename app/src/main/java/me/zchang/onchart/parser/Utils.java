package me.zchang.onchart.parser;

import java.sql.Time;

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

    public final static int MILLISECONDS_IN_ONE_HOUR = 1000 * 3600;
    public final static int MILLISECONDS_IN_ONE_MINUTE = 1000 * 60;

    public final static Time NOON_TIME = new Time(12 * MILLISECONDS_IN_ONE_HOUR);
    public final static Time EVENING_TIME = new Time(18 * MILLISECONDS_IN_ONE_HOUR);

    /**
     *
     * @param i
     * @return
     */
    public static Time periodToTime(int i) {
        switch (i) {
            case 1:
                return new Time(8 * MILLISECONDS_IN_ONE_HOUR);
            case 2:
                return new Time(8 * MILLISECONDS_IN_ONE_HOUR + 50 * MILLISECONDS_IN_ONE_MINUTE);
            case 3:
                return new Time(9 * MILLISECONDS_IN_ONE_HOUR + 50 * MILLISECONDS_IN_ONE_MINUTE);
            case 4:
                return new Time(10 * MILLISECONDS_IN_ONE_HOUR + 40 * MILLISECONDS_IN_ONE_MINUTE);
            case 5:
                return new Time(11 * MILLISECONDS_IN_ONE_HOUR + 30 * MILLISECONDS_IN_ONE_MINUTE);
            case 6:
                return new Time(13 * MILLISECONDS_IN_ONE_HOUR + 20 * MILLISECONDS_IN_ONE_MINUTE);
            case 7:
                return new Time(14 * MILLISECONDS_IN_ONE_HOUR + 10 * MILLISECONDS_IN_ONE_MINUTE);
            case 8:
                return new Time(15 * MILLISECONDS_IN_ONE_HOUR + 40 * MILLISECONDS_IN_ONE_MINUTE);
            case 9:
                return new Time(16 * MILLISECONDS_IN_ONE_HOUR + 10 * MILLISECONDS_IN_ONE_MINUTE);
            case 10:
                return new Time(16 * MILLISECONDS_IN_ONE_HOUR + 50 * MILLISECONDS_IN_ONE_MINUTE);
            case 11:
                return new Time(18 * MILLISECONDS_IN_ONE_HOUR + 30 * MILLISECONDS_IN_ONE_MINUTE);
            case 12:
                return new Time(19 * MILLISECONDS_IN_ONE_HOUR + 30 * MILLISECONDS_IN_ONE_MINUTE);
            case 13:
                return new Time(20 * MILLISECONDS_IN_ONE_HOUR + 30 * MILLISECONDS_IN_ONE_MINUTE);
            default:
                return new Time(0);
        }
    }

}
