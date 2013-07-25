/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.views;

import com.nokia.example.weatherapp.helpers.TextWrapper;
import com.nokia.example.weatherapp.helpers.UnitUtil;
import com.nokia.example.weatherapp.network.Weather;
import com.nokia.example.weatherapp.resources.Resources;
import com.nokia.example.weatherapp.resources.Settings;
import com.nokia.example.weatherapp.resources.Visual;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Paints the forecast data to weather view canvas
 */
public class WeatherLayout {
    // Constants
    private static final String CelsiusUnitString = " °C";
    private static final String FahrenheitUnitString = " °F";

    public static void layout(Weather weather,
                              Graphics g,
                              Visual v,
                              Resources r,
                              WeatherView wv)
    {
        if (wv.getWidth() < 240) {
            layoutToTinyPortrait(weather, g, v, r, wv);
        }
        else if (wv.getWidth() <= wv.getHeight()) {
            layoutToPortrait(weather, g, v, r, wv);
        }
        else {
            layoutToLandscape(weather, g, v, r, wv);
        }
    }

    /**
     * Portrait flavor
     */
    public static void layoutToPortrait(Weather weather,
                                        Graphics g,
                                        Visual v,
                                        Resources r,
                                        WeatherView wv)
    {
        int headerHeight = v.HEIGHT_HEADER;
        int margin = (int) (0.06 * wv.getWidth());
        int width = wv.getWidth();
        int height = wv.getHeight();
        int anchorX = width / 2;
        int anchorY = headerHeight + height / 4;
        
        // Draw weather icon
        Image weatherIcon = getWeatherIcon(r, wv, weather.iconUrl);
        if (weatherIcon != null) {
            g.drawImage(weatherIcon, anchorX, anchorY, Graphics.VCENTER | Graphics.RIGHT);
        }
        
        // Draw temperature
        g.setColor(v.COLOR_PRIMARY_TEXT);
        g.setFont(v.FONT_LARGE);
        int fontHeight = g.getFont().getHeight();
        anchorY -= fontHeight - 10;
        anchorX = width / 20 * 11;
        
        if (wv.getDay() == 0) {
            if (Settings.temperatureUnit == Settings.CELSIUS) {
                g.drawString(weather.temperatureC + CelsiusUnitString,
                             anchorX, anchorY, Graphics.BOTTOM | Graphics.LEFT);
            }
            else {
                g.drawString(weather.temperatureF + FahrenheitUnitString,
                             anchorX, anchorY, Graphics.BOTTOM | Graphics.LEFT);
            }
        }
        else {
            String maxTemp = weather.maxTempC
                + (Settings.temperatureUnit == Settings.CELSIUS
                ? CelsiusUnitString : FahrenheitUnitString);
            String minTemp = weather.minTempC
                + (Settings.temperatureUnit == Settings.CELSIUS
                ? CelsiusUnitString : FahrenheitUnitString);
            int offset = Math.max(g.getFont().stringWidth(maxTemp),
                                  g.getFont().stringWidth(minTemp));
            
            g.drawString(maxTemp, anchorX + offset, anchorY,
                         Graphics.BOTTOM | Graphics.RIGHT);
            g.drawString(minTemp, anchorX + offset, anchorY + fontHeight,
                         Graphics.BOTTOM | Graphics.RIGHT);
            
            g.setFont(v.FONT_SMALL);
            g.drawString(" max", anchorX + offset, anchorY, Graphics.BOTTOM | Graphics.LEFT);
            g.drawString(" min", anchorX + offset, anchorY + fontHeight,
                         Graphics.BOTTOM | Graphics.LEFT);
            anchorY += fontHeight;
        }
        
        // Draw weather description
        g.setFont(v.FONT_SMALL);
        fontHeight = g.getFont().getHeight();
        Vector lines = TextWrapper.wrapTextToWidth(weather.description,
                                                   wv.getWidth() / 2 - 2 * margin,
                                                   v.FONT_SMALL);
        int length = lines.size();
        
        for (int i = 0; i < length; i++) {
            String line = (String) lines.elementAt(i);
            g.drawString(line, anchorX, anchorY, Graphics.TOP | Graphics.LEFT);
            anchorY += fontHeight;
        }
        
        // Draw wind speed description
        try {
            lines = TextWrapper.wrapTextToWidth(UnitUtil.getBeaufortDesc(
                    Double.parseDouble(weather.windSpeedKmph)),
                    wv.getWidth() / 2 - 2 * margin, v.FONT_SMALL);
            int length2 = lines.size();
            
            if (wv.getDay() == 0 && length + length2 < 5
                || wv.getDay() > 0 && length + length2 < 4)
            {
                for (int i = 0; i < length2; i++) {
                    String line = (String) lines.elementAt(i);
                    g.drawString(line, anchorX, anchorY, Graphics.TOP | Graphics.LEFT);
                    anchorY += fontHeight;
                }
            }
        }
        catch (NumberFormatException nfe) {
            // Well, at least we tried
        }
        
        // Draw humidity
        String humidity = "Relative Humidity: " + weather.humidity + "%";
        int humidityWidth = g.getFont().stringWidth(humidity);
        anchorX = (width - humidityWidth + r.getHumidity().getWidth()) / 2;
        anchorY = headerHeight;
        
        if (!weather.humidity.equals("")) {
            anchorY += height / 7 * 4;
            g.drawImage(r.getHumidity(), anchorX - 3, anchorY, Graphics.TOP | Graphics.RIGHT);
            g.drawString(humidity, anchorX, anchorY + r.getHumidity().getHeight() / 2
                    - fontHeight / 2, Graphics.TOP | Graphics.LEFT);
        }
        else {
            anchorY += height / 9 * 6;
        }
        
        // Draw wind speed
        try {
            double windSpeed = Double.parseDouble(weather.windSpeedKmph);
            String wind = "Wind: "
                + (int) (UnitUtil.convertVelocity(Settings.KMPH, Settings.windSpeedUnit, windSpeed) + 0.5)
                + " " + UnitUtil.getWindSpeedUnit(Settings.windSpeedUnit) + ", "
                + weather.windDirectionPoints;
            Sprite windDirection = wv.getWinddirection();
            g.drawString(wind, anchorX, anchorY - windDirection.getHeight() / 2
                    - fontHeight / 2, Graphics.TOP | Graphics.LEFT);
            windDirection.setRefPixelPosition(anchorX - 3, anchorY);
            windDirection.paint(g);
        }
        catch (NumberFormatException nfe) {
            //No drawing then
        }
    }

    /**
     * Portrait flavor for tiny screens like 128x160
     */
    public static void layoutToTinyPortrait(Weather weather,
                                            Graphics g,
                                            Visual v,
                                            Resources r,
                                            WeatherView wv)
    {
        int width = wv.getWidth();
        int height = wv.getHeight();
        int stretch = height / 6;
        int margin = (int) (0.02 * wv.getWidth());
        
        // Draw weather icon
        int anchorX = width / 4 * 3;
        int anchorY = height / 7 * 3;
        int anchor = Graphics.VCENTER | Graphics.HCENTER;
        
        Image weatherIcon = getWeatherIcon(r, wv, weather.iconUrl);
        
        if (weatherIcon != null) {
            g.drawImage(weatherIcon, anchorX, anchorY, anchor);
        }
        
        anchorX = width / 6;
        anchorY = height / 2 - 2 * stretch;
        anchor = Graphics.RIGHT | Graphics.TOP;
        
        g.setColor(v.COLOR_PRIMARY_TEXT);
        
        if (wv.getDay() > 0) {
            anchorY += 0.3 * stretch;
        }
        
        // Draw temperature
        int fontHeight = v.FONT_MEDIUM.getHeight();
        Image termometer = r.getTermometer();
        int iconOffset = (termometer.getHeight() - fontHeight) / 2;
        g.drawImage(r.getTermometer(), anchorX,
                    anchorY - (wv.getDay() == 0 ? iconOffset : 0), anchor);
        g.setFont(v.FONT_MEDIUM);
        fontHeight = g.getFont().getHeight();
        int tempY = anchorY + fontHeight;
        anchorX += margin;
        anchor = Graphics.LEFT | Graphics.TOP;
        
        if (wv.getDay() == 0) {
            if (Settings.temperatureUnit == Settings.CELSIUS) {
                g.drawString(weather.temperatureC + CelsiusUnitString, anchorX,
                             tempY, Graphics.BOTTOM | Graphics.LEFT);
            }
            else {
                g.drawString(weather.temperatureF + FahrenheitUnitString, anchorX,
                             tempY, Graphics.BOTTOM | Graphics.LEFT);
            }
        }
        else {
            String maxTemp = weather.maxTempC
                + (Settings.temperatureUnit == Settings.CELSIUS
                ? CelsiusUnitString : FahrenheitUnitString);
            String minTemp = weather.minTempC
                + (Settings.temperatureUnit == Settings.CELSIUS
                ? CelsiusUnitString : FahrenheitUnitString);
            int offset = Math.max(g.getFont().stringWidth(maxTemp),
                                  g.getFont().stringWidth(minTemp));
            
            g.drawString(maxTemp, anchorX + offset, tempY,
                         Graphics.BOTTOM | Graphics.RIGHT);
            g.drawString(minTemp, anchorX + offset, tempY + (int) (1.2 * fontHeight),
                         Graphics.BOTTOM | Graphics.RIGHT);
            
            g.setFont(v.FONT_SMALL);
            g.drawString(" max", anchorX + offset, tempY, Graphics.BOTTOM | Graphics.LEFT);
            g.drawString(" min", anchorX + offset, tempY + (int) (1.2 * fontHeight),
                         Graphics.BOTTOM | Graphics.LEFT);
        }
        
        anchorX -= margin;
        anchorY += (wv.getDay() == 0) ? stretch : 1.7 * stretch;
        Sprite windDirection = wv.getWinddirection();
        windDirection.setRefPixelPosition(anchorX,
            anchorY + windDirection.getHeight() - iconOffset);
        windDirection.paint(g);
        g.setFont(v.FONT_MEDIUM);
        anchorX += margin;
        anchor = Graphics.LEFT | Graphics.TOP;
        
        try {
            double windSpeed = Double.parseDouble(weather.windSpeedKmph);
            String wind =
                (int) (UnitUtil.convertVelocity(Settings.KMPH, Settings.windSpeedUnit, windSpeed) + 0.5)
                + " " + UnitUtil.getWindSpeedUnit(Settings.windSpeedUnit);
            g.drawString(wind, anchorX, anchorY, Graphics.TOP | Graphics.LEFT);
        }
        catch (NumberFormatException nfe) {
            //No drawing then
        }
        
        if (wv.getDay() == 0) {
            anchorY += stretch;
            anchorX -= margin;
            anchor = Graphics.RIGHT | Graphics.TOP;
            g.drawImage(r.getHumidity(), anchorX, anchorY - iconOffset, anchor);
            
            g.setFont(v.FONT_MEDIUM);
            anchorX += margin;
            anchor = Graphics.LEFT | Graphics.TOP;
            
            if (!weather.humidity.equals("")) {
                g.drawString(weather.humidity + "%", anchorX, anchorY, anchor);
            }
            else {
                g.drawString("-", anchorX, anchorY, anchor);
            }
        }
        
        // Draw weather description
        anchorX = width / 2;
        anchorY = height / 6 * 4;
        anchor = Graphics.TOP | Graphics.HCENTER;
        g.setFont(v.FONT_SMALL);
        fontHeight = g.getFont().getHeight();
        
        String description = weather.description;
        
        try {
            description += ", "
                + UnitUtil.getBeaufortDesc(Double.parseDouble(weather.windSpeedKmph));
            StringBuffer sb = new StringBuffer(description);
            boolean truncated = false;
            
            while (v.FONT_SMALL.stringWidth(sb.toString()) > wv.getWidth() - 2 * margin) {
                sb.deleteCharAt(sb.length() - 1);
                truncated = true;
            }
            
            if (truncated) {
                sb.delete(sb.length() - 3, sb.length() - 1);
                sb.append("...");
                description = sb.toString();
            }
        }
        catch (NumberFormatException nfe) {
            // Well, at least we tried
        }
        
        g.drawString(description, anchorX, anchorY, anchor);
    }

    /**
     * Landscape flavor
     */
    public static void layoutToLandscape(Weather weather,
                                         Graphics g,
                                         Visual v,
                                         Resources r,
                                         WeatherView wv)
    {
        int width = wv.getWidth();
        int height = wv.getHeight();
        int stretch = height / 6;
        int margin = (int) (0.02 * wv.getWidth());
        int anchorX = width / 4;
        int anchorY = height / 2 - 2 * stretch;
        int anchor = Graphics.RIGHT | Graphics.TOP;
        
        g.setColor(v.COLOR_PRIMARY_TEXT);
        
        if (wv.getDay() > 0) {
            anchorY += 0.3 * stretch;
        }
        
        // Draw temperature
        g.setFont(v.FONT_SMALL);
        g.drawString("Temp", anchorX, anchorY, anchor);
        g.setFont(v.FONT_MEDIUM);
        int fontHeight = g.getFont().getHeight();
        int tempY = anchorY + fontHeight;
        anchorX += margin;
        anchor = Graphics.LEFT | Graphics.TOP;

        if (wv.getDay() == 0) {
            if (Settings.temperatureUnit == Settings.CELSIUS) {
                g.drawString(weather.temperatureC + CelsiusUnitString, anchorX,
                             tempY, Graphics.BOTTOM | Graphics.LEFT);
            }
            else {
                g.drawString(weather.temperatureF + FahrenheitUnitString, anchorX,
                             tempY, Graphics.BOTTOM | Graphics.LEFT);
            }
        }
        else {
            String maxTemp = weather.maxTempC
                + (Settings.temperatureUnit == Settings.CELSIUS
                ? CelsiusUnitString : FahrenheitUnitString);
            String minTemp = weather.minTempC
                + (Settings.temperatureUnit == Settings.CELSIUS
                ? CelsiusUnitString : FahrenheitUnitString);
            int offset = Math.max(g.getFont().stringWidth(maxTemp),
                                  g.getFont().stringWidth(minTemp));
            
            g.drawString(maxTemp, anchorX + offset, tempY, Graphics.BOTTOM | Graphics.RIGHT);
            g.drawString(minTemp, anchorX + offset, tempY + (int) (1.2 * fontHeight),
                         Graphics.BOTTOM | Graphics.RIGHT);
            
            g.setFont(v.FONT_SMALL);
            g.drawString(" max", anchorX + offset, tempY, Graphics.BOTTOM | Graphics.LEFT);
            g.drawString(" min", anchorX + offset, tempY + (int) (1.2 * fontHeight),
                         Graphics.BOTTOM | Graphics.LEFT);
        }
        
        g.setFont(v.FONT_SMALL);
        anchorX -= margin;
        anchorY += (wv.getDay() == 0) ? stretch : 1.7 * stretch;
        anchor = Graphics.RIGHT | Graphics.TOP;
        g.drawString("Wind", anchorX, anchorY, anchor);
        g.setFont(v.FONT_MEDIUM);
        anchorX += margin;
        anchor = Graphics.LEFT | Graphics.TOP;
        
        try {
            double windSpeed = Double.parseDouble(weather.windSpeedKmph);
            String wind =
                (int) (UnitUtil.convertVelocity(Settings.KMPH, Settings.windSpeedUnit, windSpeed) + 0.5)
                + " " + UnitUtil.getWindSpeedUnit(Settings.windSpeedUnit);
            g.drawString(wind, anchorX, anchorY, anchor);
            Sprite windDirection = wv.getWinddirection();
            int windWidth = v.FONT_MEDIUM.stringWidth(wind + " ");
            windDirection.setRefPixelPosition(anchorX + windWidth + windDirection.getWidth(),
                                              anchorY + windDirection.getHeight());
            windDirection.paint(g);
        }
        catch (NumberFormatException nfe) {
            //No drawing then
        }
        
        if (wv.getDay() == 0) {
            g.setFont(v.FONT_SMALL);
            anchorY += stretch;
            anchorX -= margin;
            anchor = Graphics.RIGHT | Graphics.TOP;
            g.drawString("Humidity", anchorX, anchorY, anchor);
            
            g.setFont(v.FONT_MEDIUM);
            anchorX += margin;
            anchor = Graphics.LEFT | Graphics.TOP;
            
            if (!weather.humidity.equals("")) {
                g.drawString(weather.humidity + "%", anchorX, anchorY, anchor);
            }
            else {
                g.drawString("-", anchorX, anchorY, anchor);
            }
        }
        
        // Draw weather icon
        anchorX = width / 4 * 3;
        anchorY = height / 7 * 3;
        anchor = Graphics.VCENTER | Graphics.HCENTER;
        
        Image weatherIcon = getWeatherIcon(r, wv, weather.iconUrl);
        
        if (weatherIcon != null) {
            g.drawImage(weatherIcon, anchorX, anchorY - 10, anchor);
        }
        
        // Draw weather description
        anchorX = width / 2;
        anchorY = height / 6 * 4;
        anchor = Graphics.TOP | Graphics.HCENTER;
        g.setFont(v.FONT_SMALL);
        fontHeight = g.getFont().getHeight();
        
        String description = weather.description;
        
        try {
            description += ", "
                + UnitUtil.getBeaufortDesc(Double.parseDouble(weather.windSpeedKmph));
        }
        catch (NumberFormatException nfe) {
            // Well, at least we tried
        }
        
        g.drawString(description, anchorX, anchorY, anchor);
    }

    private static Image getWeatherIcon(Resources r, WeatherView wv, String iconUrl) {
        Image weatherIcon = r.getWeatherIcon();
        
        if (weatherIcon == null) {
            try {
                String url = iconUrl;
                int slashIndex = url.lastIndexOf('/');
                String fileName = url.substring(slashIndex + 1);
                weatherIcon = r.getWeatherIcon(fileName);
                
                if (wv.getDay() == 0) {
                    wv.updateMode(fileName.indexOf("night") > 0
                            ? Visual.NIGHT_MODE : Visual.DAY_MODE);
                }
            }
            catch (Exception e) {
                System.out.println("Invalid icon url");
            }
        }
        
        return weatherIcon;
    }
}
