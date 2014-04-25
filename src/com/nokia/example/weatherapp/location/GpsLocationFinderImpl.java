/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.location;

import javax.microedition.location.Criteria;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

/**
 * Determines location using GPS
 */
public class GpsLocationFinderImpl
        extends LocationFinderImpl {

    private Criteria criteria;
    protected boolean accessAllowed = true;

    public GpsLocationFinderImpl() {
        // Define locationing criteria
        criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
        criteria.setSpeedAndCourseRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAddressInfoRequired(false);
    }

    /**
     * @return Instance of GPS location provider.
     * Returns null, in case no location provider can be instantiated.
     */
    protected LocationProvider getLocationProvider() {
        try {
            return LocationProvider.getInstance(criteria);
        }
        catch (LocationException le) {
            return null;
        }
    }
}
