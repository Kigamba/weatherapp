/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.location;

import com.nokia.mid.location.LocationUtil;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

/**
 * Determines location using CellID or WLAN
 */
public class CellIdLocationFinderImpl
        extends GpsLocationFinderImpl {

    /**
     * @return Instance of CellID or WLAN based location provider or GPS location provider as secondary option.
     * Returns null, in case no location provider can be instantiated.
     */
    protected LocationProvider getLocationProvider() {
        try {
            // Try using assisted GPS, CellID or network based locationing
            int[] methods = {Location.MTA_ASSISTED | Location.MTE_CELLID | Location.MTY_NETWORKBASED};
            LocationProvider lp = LocationUtil.getLocationProvider(methods, null);
            // Fallback to GPS locationing, if above constraints don't apply
            if (lp == null) {
                lp = super.getLocationProvider();
            }
            return lp;
        }
        catch (LocationException le) {
            return null;
        }
    }
}
