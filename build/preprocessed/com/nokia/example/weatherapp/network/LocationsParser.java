/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

import com.nokia.example.weatherapp.location.Location;
import java.util.Vector;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * Parses the server locations response.
 */
public class LocationsParser
        extends Parser {

    /**
     * Vector of result forecasts.
     */
    private Vector locations = new Vector();

    public Vector getLocations() {
        return locations;
    }

    /**
     * Parses JSON response
     * @param response JSON response as string
     * @throws ParseError
     */
    public void parse(String response) throws ParseError {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.isNull("search_api")) {
                return;
            }
            JSONObject data = obj.getJSONObject("search_api");
            JSONArray results = data.getJSONArray("result");
            int length = results.length();
            for (int i = 0; i < length; ++i) {
                locations.addElement(parseLocation(results.getJSONObject(i)));
            }
        }
        catch (Exception e) {
            throw new ParseError(e.getMessage());
        }
    }

    /**
     * Parses a single location from response
     * @param location JSONObject containing location
     * @return Location object
     * @throws JSONException
     */
    private Location parseLocation(JSONObject location) throws JSONException {
        Location l = new Location();
        JSONArray city = location.getJSONArray("areaName");
        l.city = city.getJSONObject(0).getString("value");

        JSONArray country = location.getJSONArray("country");
        l.country = country.getJSONObject(0).getString("value");

        l.latitude = location.getString("latitude");
        l.latitude = location.getString("longitude");

        return l;
    }
}
