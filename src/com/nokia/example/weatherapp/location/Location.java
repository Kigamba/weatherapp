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
 * Represents a location.
 */
public class Location {

    /**
     * City or region name
     */
    public String city = "";
    /**
     * Country name
     */
    public String country = "";
    /**
     * Latitude
     */
    public String latitude = "";
    /**
     * Longitude
     */
    public String longitude = "";

    public String toString() {
        return city + ", " + country;
    }
}
