/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.location;

import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

/**
 * Private implementation for the LocationFinder. Cannot be instantiated though.
 */
abstract class LocationFinderImpl
        extends LocationFinder
        implements Runnable {

    private LocationProvider lp = null;
    private LocationListener listener;
    private int timeout;
    private Thread t;

    /**
     * Sets a listener for location updates. The listener will be called with updated
     * location at the defined interval.
     * @param listener
     */
    public void setLocationListener(LocationListener listener) {
        this.listener = listener;
    }

    /**
     * Starts non-recurring location retrieval
     * @param timeout
     * Returns null via locationUpdated callback, if location cannot be determined in time.
     */
    public synchronized void start(int timeout) {
        this.timeout = timeout;
        if (lp == null) {
            lp = getLocationProvider();
        }
        if (t == null || !t.isAlive()) {
            t = new Thread(this);
            t.start();
        }
    }

    /**
     * Cancels location retrieval
     */
    public synchronized void cancel() {
        if (lp != null) {
            lp.reset();
            lp = null;
        }
    }

    /**
     * Executes the location retrieval
     */
    public void run() {
        try {
            updateLocation(lp.getLocation(timeout));
        }
        catch (InterruptedException ie) {
            // Location retrieval cancelled            
        }
        catch (Exception e) {
            if (listener != null) {
                listener.locationUpdated(null);
            }
        }
    }

    /**
     * Notifies listener about location update
     * @param lctn Original location object
     */
    public void updateLocation(javax.microedition.location.Location lctn) {
        Location location = null;
        QualifiedCoordinates coordinates = lctn.getQualifiedCoordinates();
        if (coordinates != null) {
            location = new Location();
            location.latitude = "" + coordinates.getLatitude();
            location.longitude = "" + coordinates.getLongitude();
        }
        listener.locationUpdated(location);
    }

    /**
     * @return Instance of LocationProvider best available method
     */
    protected abstract LocationProvider getLocationProvider();
}
