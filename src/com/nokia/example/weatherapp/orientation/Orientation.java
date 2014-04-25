/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.orientation;

/**
 * Orientation is supported for Java Runtime 2.0.0 for Series 40 onwards.
 */
public abstract class Orientation {

    private static Orientation orientation;

    /**
     * Set oritentation listener
     * @param listener
     */
    public static void enableOrientations() {
        if (orientation == null) {
            orientation = getImplementation();
            System.out.println("enabledOrientations");
        }

    }

    /**
     * Loads up the isolated implementation class
     * @return Orientation Returns Orientation implementation or null, if orientations are not supported
     */
    private static Orientation getImplementation() {
        Orientation implementation = null;
        try {
            Class.forName("com.nokia.mid.ui.orientation.OrientationListener");

            Class c = Class.forName("com.nokia.example.weatherapp.orientation.OrientationImpl");
            implementation = (Orientation) (c.newInstance());
        }
        catch (Exception e) {
            // No orientation support
        }
        return implementation;
    }
}
