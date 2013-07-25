/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.location;

/**
 * Abstract class hiding location API dependencies to enable running the
 * application also on devices, that don't support location services.
 */
public abstract class LocationFinder {

    /**
     * Returns a LocationFinder instance, which uses the quickest mehtod the
     * device has to offer
     */
    public static LocationFinder getLocationFinder() {
        LocationFinder provider = null;
        try {
            if (supportsLocationAppendix()) {
                Class c = Class.forName("com.nokia.example.weatherapp.location.CellIdLocationFinderImpl");
                provider = (LocationFinder) (c.newInstance());
            }
            else if (supportsLocation()) {
                Class c = Class.forName("com.nokia.example.weatherapp.location.GpsLocationFinderImpl");
                provider = (LocationFinder) (c.newInstance());
            }
        }
        catch (Exception e) {
            // Return null if location APIs are not supported
        }
        return provider;
    }

    /**
     * Sets listener for location updates
     */
    public abstract void setLocationListener(LocationListener listener);

    /**
     * Starts non-recurring location retrieval
     */
    public abstract void start(int timeout);

    /**
     * Cancels location retrieval
     */
    public abstract void cancel();

    /**
     * Determines whether the Location API (JSR-179) is available on the device
     */
    public static boolean supportsLocation() {
        try {
            Class.forName("javax.microedition.location.Location");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Determines whether the Location API Appendix is available on the device
     */
    public static boolean supportsLocationAppendix() {
        try {
            Class.forName("com.nokia.mid.location.LocationUtil");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
