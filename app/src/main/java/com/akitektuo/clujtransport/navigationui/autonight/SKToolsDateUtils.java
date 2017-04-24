package com.akitektuo.clujtransport.navigationui.autonight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class used to manipulate date and time values
 */
final class SKToolsDateUtils {

    /**
     * the sunset hour (in 24h format) used for auto day / night mode option
     */
    protected static int AUTO_NIGHT_SUNSET_HOUR;

    /**
     * the sunrise minute (in 24h format) used for auto day / night mode option
     */
    protected static int AUTO_NIGHT_SUNRISE_MINUTE;

    /**
     * the sunset minute (in 24h format) used for auto day / night mode option
     */
    protected static int AUTO_NIGHT_SUNSET_MINUTE;

    /**
     * the sunrise hour (in 24h format) used for auto day / night mode option
     */
    protected static int AUTO_NIGHT_SUNRISE_HOUR;

    private SKToolsDateUtils() {}

    /**
     * Returns the current hour of the day as set on the device.
     * @return
     */
    public static int getHourOfDay() {
        SimpleDateFormat format = new SimpleDateFormat("H");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Byte.parseByte(format.format(new Date()));
    }

    /**
     * Returns the current hour of the day as set on the device.
     * @return
     */
    public static int getMinuteOfDay() {
        SimpleDateFormat format = new SimpleDateFormat("m");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Byte.parseByte(format.format(new Date()));
    }

}
