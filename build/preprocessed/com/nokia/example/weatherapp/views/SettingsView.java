/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.views;

import com.nokia.example.weatherapp.helpers.UnitUtil;
import com.nokia.example.weatherapp.resources.Settings;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;

/**
 * Settings view
 */
public class SettingsView
        extends Form
        implements View {

    ChoiceGroup tempUnits = new ChoiceGroup("Temperature Unit", Choice.POPUP,
                                            new String[]{"Celsius", "Fahrenheit"}, null);
    ChoiceGroup windUnits = new ChoiceGroup("Windspeed Unit", Choice.POPUP,
                                            new String[]{UnitUtil.getWindSpeedUnit(Settings.MPS), UnitUtil.getWindSpeedUnit(
                Settings.KMPH),
                UnitUtil.getWindSpeedUnit(Settings.MPH), UnitUtil.getWindSpeedUnit(Settings.KNOTS)}, null);

    public SettingsView() {
        super("Settings");
        append(tempUnits);
        append(windUnits);
    }

    public void activate() {
        tempUnits.setSelectedIndex(Settings.temperatureUnit, true);
        windUnits.setSelectedIndex(Settings.windSpeedUnit, true);
    }

    public void deactivate() {
        Settings.temperatureUnit = tempUnits.getSelectedIndex();
        Settings.windSpeedUnit = windUnits.getSelectedIndex();
    }
}
