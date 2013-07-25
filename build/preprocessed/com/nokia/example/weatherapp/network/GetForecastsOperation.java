/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

import com.nokia.example.weatherapp.Main;
import java.io.InputStream;
import java.util.Vector;

/**
 * Operation for retrieving weather data.
 */
public class GetForecastsOperation
        extends NetworkOperation {

    private final static String SERVICE_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx";
    private final static String SERVICE_PARAMS = "format=json&num_of_days=5&key=" + Keys.WORLDWEATHERONLINE;
    private Listener listener;
    private String query = "";

    /**
     * Listener interface
     */
    public interface Listener {

        /**
         * Returns the retrieved forecasts or null in case of an error.
         * @param comments
         */
        public void forecastsReceived(Vector forecasts);
    }

    /**
     * Cosntructor
     * @param listener
     * @param query Preferred form "<latitude>,<longitude>" or "<city>,<country>"
     */
    public GetForecastsOperation(Listener listener, String query) {
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
        // Use local static response in TEST MODE
        if (Main.getInstance().isInTestMode()) {
            InputStream is = getClass().getResourceAsStream("/weather_response.json");
            StringBuffer sb = new StringBuffer();
            try {
                int chars = 0;
                while ((chars = is.read()) != -1) {
                    sb.append((char) chars);
                }
                // Parse local xml
                parseForecasts(sb.toString());
            }
            catch (Exception e) {
            }
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
        parseForecasts(response);
    }

    /**
     * Parses the server response and calls the listener.
     * @param response
     */
    public void parseForecasts(String response) {
        try {
            ForecastsParser parser = new ForecastsParser();
            parser.parse(response);

            Vector forecasts = parser.getForecasts();
            listener.forecastsReceived(forecasts);
        }
        catch (ParseError pe) {
            listener.forecastsReceived(null);
        }
    }
}
