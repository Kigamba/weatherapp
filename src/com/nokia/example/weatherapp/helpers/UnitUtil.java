/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.helpers;

import com.nokia.example.weatherapp.resources.Settings;

/**
 * Handles unit conversions
 */
public class UnitUtil {

    // Factors for velocity conversion
    private final static double[] VELOCITY_FACTORS = {
        1, // m/s (reference unit)
        3.6, // km/h
        2.2369, // mph
        1.9438 // knots
    };
    // Beaufort scale bounds in km/h
    private final static double[] BEAUFORT_LIMITS = {
        1,
        5.5,
        11,
        19,
        28,
        38,
        49,
        61,
        74,
        88,
        102,
        117
    };
    // Beaufort scale descriptions (index corresponds to a Beaufort value)
    private final static String[] BEAUFORT_SCALE = {
        "Calm",
        "Light air",
        "Light breeze",
        "Gentle breeze",
        "Moderate breeze",
        "Fresh breeze",
        "Strong breeze",
        "Moderate gale",
        "Fresh gale",
        "Strong gale",
        "Storm",
        "Violent storm",
        "Hurricane force"
    };

    /**
     * Get temperature unit string
     * @param unit
     * @return Temperature unit in verbose form
     */
    public static String getTemperatureUnit(int unit) {
        switch (unit) {
            case Settings.FAHRENHEIT:
                return "Fahrenheit";
            default:
                return "Celsius";
        }
    }

    /**
     * Get wind speed unit string
     * @param unit
     * @return Wind speed unit in verbose form
     */
    public static String getWindSpeedUnit(int unit) {
        switch (unit) {
            case Settings.KMPH:
                return "km/h";
            case Settings.MPH:
                return "Mph";
            case Settings.KNOTS:
                return "Knots";
            default:
                return "m/s";
        }
    }

    /**
     * Convert velocity from one unit to another
     * @param from Unit of the passed value
     * @param to Unit of the returned value
     * @param value Value to be converted
     * @return Converted value
     */
    public static double convertVelocity(int from, int to, double value) {
        try {
            return 1 / VELOCITY_FACTORS[from] * value * VELOCITY_FACTORS[to];
        }
        catch (IndexOutOfBoundsException ioobe) {
            return Double.NaN;
        }
    }

    /**
     * Get Beaufort value
     * @param kmph Velocity in kilometers per hour
     * @return Beaufort value as integer
     */
    public static int getBeaufort(double kmph) {
        for (int i = BEAUFORT_LIMITS.length - 1; i >= 0; i--) {
            if (kmph > BEAUFORT_LIMITS[i]) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Get Beaufort description
     * @param kmph Velocity in kilometers per hour
     * @return Beaufort in verbose form
     */
    public static String getBeaufortDesc(double kmph) {
        int value = getBeaufort(kmph);
        return ((value >= 0 && value < BEAUFORT_SCALE.length) ? BEAUFORT_SCALE[value] : "");
    }
}
