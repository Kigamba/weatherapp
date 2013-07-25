/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

/**
 * Weather describes a weather forecast for a single day.
 */
public class Weather {

    /**
     * Icon url
     */
    public String iconUrl = "";
    /**
     * Humidity
     */
    public String humidity = "0";
    /**
     * Temperature in Celsius
     */
    public String temperatureC = "0";
    /**
     * Temperature in Fahrenheit
     */
    public String temperatureF = "0";
    /**
     * Temperature in Celsius
     */
    public String minTempC = "0";
    /**
     * Temperature in Fahrenheit
     */
    public String minTempF = "0";
    /**
     * Temperature in Celsius
     */
    public String maxTempC = "0";
    /**
     * Temperature in Fahrenheit
     */
    public String maxTempF = "0";
    /**
     * Wind speed in kmph
     */
    public String windSpeedKmph = "0";
    /**
     * Wind speed in mph
     */
    public String windSpeedMph = "0";
    /**
     * Wind direction in degrees
     */
    public String windDirectionDegrees = "0";
    /**
     * Wind direction in compass points
     */
    public String windDirectionPoints = "N";
    /**
     * Weather description
     */
    public String description = "";
}
