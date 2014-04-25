/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp;

import com.nokia.example.weatherapp.network.Keys;
import com.nokia.example.weatherapp.resources.Settings;
import com.nokia.example.weatherapp.views.ViewMaster;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/**
 * Main MIDlet
 */
public class Main
        extends MIDlet {

    private static Main self;
    private Display display;
    private ViewMaster viewMaster;

    public static Main getInstance() {
        return self;
    }

    public void startApp() {
        if (display == null) {
            self = this;
            display = Display.getDisplay(this);
            // Load persistent settings
            Settings.load();
            
            // Initiate view master and let it handle the views
            viewMaster = ViewMaster.getInstance();
            viewMaster.setDisplay(display);
            viewMaster.openView(ViewMaster.VIEW_WEATHER);

            // Perform location retrieval            
            viewMaster.startLocationFinder();
            
            // Show alert if application is running in test mode (no valid
            // API keys have been entered)
            if (isInTestMode()) {
                Alert alert = new Alert("Test mode", "The application is "
                        + "running in test mode. To run the application in "
                        + "online mode you need to have valid API keys for "
                        + "Inneractive In App Advertisement and World Weather Online "
                        + "services.", null, AlertType.INFO);
                display.setCurrent(alert);
                alert.setTimeout(10000);
            }
        }
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        // Save persistent settings
        Settings.save();
    }

    public void exit() {
        destroyApp(true);
        notifyDestroyed();
    }

    // Checks whether API keys have been set
    public boolean isInTestMode() {
        return Keys.APPID.equals("")
                || Keys.WORLDWEATHERONLINE.equals("0000000000000000000000");
    }
}
