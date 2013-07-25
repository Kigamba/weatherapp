/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

import java.util.Vector;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * Parses the server forecast response.
 */
public class ForecastsParser
        extends Parser {

    // Vector of result forecasts
    private Vector forecasts = new Vector();

    public Vector getForecasts() {
        return forecasts;
    }

    /**
     * Parses JSON response
     * @param response JSON response as string
     * @throws ParseError
     */
    public void parse(String response) throws ParseError {
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.isNull("data")) {
                return;
            }
            JSONObject data = obj.getJSONObject("data");
            JSONArray currentCondition = data.getJSONArray("current_condition");
            forecasts.addElement(parseWeather(currentCondition.getJSONObject(0)));

            JSONArray upcomingConditions = data.getJSONArray("weather");
            int length = data.length();
            for (int i = 0; i < length; ++i) {
                forecasts.addElement(parseWeather(upcomingConditions.getJSONObject(i)));
            }
        }
        catch (Exception e) {
            throw new ParseError(e.getMessage());
        }
    }

    /**
     * Parses a single day from response
     * @param weather JSONObject containing weather data for one day
     * @return Populated Weather object
     * @throws JSONException
     */
    private Weather parseWeather(JSONObject weather) throws JSONException {
        Weather w = new Weather();
        w.humidity = weather.optString("humidity", "");
        w.temperatureC = weather.optString("temp_C", "");
        w.temperatureF = weather.optString("temp_F", "");
        w.minTempC = weather.optString("tempMinC", "");
        w.minTempF = weather.optString("tempMinF", "");
        w.maxTempC = weather.optString("tempMaxC", "");
        w.maxTempF = weather.optString("tempMaxF", "");
        w.windDirectionDegrees = weather.getString("winddirDegree");
        w.windDirectionPoints = weather.getString("winddir16Point");
        w.windSpeedKmph = weather.getString("windspeedKmph");
        w.windSpeedMph = weather.getString("windspeedMiles");

        JSONArray description = weather.getJSONArray("weatherDesc");
        w.description = description.getJSONObject(0).getString("value");

        JSONArray iconUrl = weather.getJSONArray("weatherIconUrl");
        w.iconUrl = iconUrl.getJSONObject(0).getString("value");
        return w;
    }
}
