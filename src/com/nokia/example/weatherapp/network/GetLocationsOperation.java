/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

import com.nokia.example.weatherapp.Main;
import com.nokia.example.weatherapp.location.Location;
import com.nokia.example.weatherapp.resources.Resources;
import java.util.Vector;

/**
 * Operation for retrieving location data.
 */
public class GetLocationsOperation
        extends NetworkOperation {

    final static String SERVICE_URL = "http://api.worldweatheronline.com/free/v1/search.ashx";
    final static String SERVICE_PARAMS = "format=json&num_of_results=3&key=" + Keys.WORLDWEATHERONLINE;
    private Listener listener;
    private String query = "";

    /**
     * Listener interface
     */
    public interface Listener {

        /**
         * Returns the retrieved locations or null in case of an error.
         * @param comments
         */
        public void locationsReceived(Vector locations);
    }

    /**
     * Cosntructor
     * @param listener
     * @param query Preferred form "<city>"
     */
    public GetLocationsOperation(Listener listener, String query) {
        this.listener = listener;
        this.query = query;
    }

    /**
     * Starts the operation.
     */
    public void start() {
        new Thread() {

            public void run() {
                startNetwork();
            }
        }.start();
    }

    /**
     * Initiates a new GET request
     */
    private void startNetwork() {
        // Search from static city list in TEST MODE
        if (Main.getInstance().isInTestMode()) {
            Vector locations = new Vector();
            int length = Resources.CITIES.length;
            for (int i = 0; i < length - 1; i += 2) {
                // Returns always at least the last location in the list (Testham)
                if (Resources.CITIES[i].startsWith(query) || i == length - 2) {
                    Location location = new Location();
                    location.city = Resources.CITIES[i];
                    location.country = Resources.CITIES[i + 1];
                    locations.addElement(location);
                }
            }
            listener.locationsReceived(locations);
            return;
        }

        // Start the http request
        Network nw = new Network(this);
        nw.startHttpGet(SERVICE_URL + "?q=" + query + "&" + SERVICE_PARAMS);
    }

    /**
     * Callback for GET request
     * @param response
     */
    public void networkHttpGetResponse(String response) {
        parseLocations(response);
    }

    /**
     * Parses the server response and calls the listener.
     * @param response
     */
    public void parseLocations(String response) {
        System.out.println("response: " + response);
        try {
            LocationsParser parser = new LocationsParser();
            parser.parse(response);

            Vector locations = parser.getLocations();
            listener.locationsReceived(locations);
        }
        catch (ParseError pe) {
            listener.locationsReceived(null);
        }
    }
}
