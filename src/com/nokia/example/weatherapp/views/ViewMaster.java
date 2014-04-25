/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.views;

import com.nokia.example.weatherapp.components.IconCommandFactory;
import com.nokia.example.weatherapp.components.ViewStack;
import com.nokia.example.weatherapp.resources.Resources;
import com.nokia.example.weatherapp.resources.Settings;
import com.nokia.mid.ui.IconCommand;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 * Singleton class for handling application views. Handles view switching.
 */
public class ViewMaster
        implements CommandListener {

    public static final int VIEW_NOVIEW = -1;
    public static final int VIEW_WEATHER = 0;
    public static final int VIEW_SETTINGS = 1;
    public static final int VIEW_LOCATIONS = 2;
    public static final int VIEW_TEMPERATURE = 3;
    public static final int VIEW_WIND = 4;
    public static final int VIEW_SEARCH = 5;
    private static ViewMaster self = null;
    private int activeView = VIEW_NOVIEW;
    private final Command backCmd = new Command("Back", Command.BACK, 1);
    private final Command cancelCmd = new Command("Cancel", Command.CANCEL, 1);
    private final Command settingsCmd = new Command("Settings", Command.SCREEN, 2);
    private final Command locationCmd = new Command("Location", Command.SCREEN, 1);
    private final Command goCmd = new Command("GO", Command.OK, 1);
    private Command addCmd = new Command("Add", Command.SCREEN, 2);
    private Display display;
    private WeatherView weatherView;
    private SettingsView settingsView;
    private LocationsView locationsView;
    private SearchView searchView;
    private ViewStack viewStack = new ViewStack();

    private ViewMaster() {
    }

    /**
     * Returns and/or instantiates ViewMaster object
     * @return
     */
    public static ViewMaster getInstance() {
        if (self == null) {
            self = new ViewMaster();
            self.initialize();
        }
        return self;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display d) {
        this.display = d;
        searchView.setDisplay(d);
    }

    /**
     * Returns view instance according to given ID
     * @param id view ID
     * @return View
     */
    public Displayable getView(int id) {
        switch (id) {
            case VIEW_SETTINGS:
                return settingsView;
            case VIEW_LOCATIONS:
                return locationsView;
            case VIEW_SEARCH:
                return searchView;
            default:
                return weatherView;
        }
    }

    /**
     * Activates previous view
     */
    public synchronized void backView() {
        setView(viewStack.popView());
    }

    /**
     * Opens a view according to ID and pushes it to view stack
     * @param id View ID
     */
    public synchronized void openView(int id) {
        if (id != activeView) {
            viewStack.pushView(activeView);
            setView(id);
        }
    }

    /**
     * Handles command actions
     * @param cmd Selected command
     * @param dsplbl The Displayable on which this event has occurred
     */
    public void commandAction(Command cmd, Displayable dsplbl) {
        if (cmd == backCmd || cmd == cancelCmd) {
            if (activeView == VIEW_WEATHER) {
                if (weatherView.getDay() != 0) {
                    weatherView.setDay(weatherView.getDay() - 1);
                } else {
                    weatherView.showAdAndExit();
                }
            } else {
                backView();                
            }
        }
        else if (cmd == locationCmd) {
            openLocationsView();
        }
        else if (cmd == settingsCmd) {
            openView(VIEW_SETTINGS);
        }
        else if (cmd == addCmd && activeView == VIEW_LOCATIONS) {
            openView(VIEW_SEARCH);
        }
        else if (cmd == goCmd) {
            weatherView.launchAdEndpoint();
        }
    }

    /**
     * Starts location retrieval
     */
    public void startLocationFinder() {
        weatherView.startOrUpdateLocationFinder();
    }

    /**
     * Displays an alert
     * @param title Title of the alert
     * @param alertText The actual content of the alert
     * @param type Type of the alert
     */
    public void showAlert(String title, String alertText, AlertType type) {
        if (!weatherView.isClosing()) {
            final Alert alert = new Alert(title, alertText, null, type);
            display.setCurrent(alert, getView(activeView));
        }
    }

    public void openLocationsView() {
        if (Settings.recentLocations.isEmpty()) {
            openView(ViewMaster.VIEW_SEARCH);
        }
        else {
            openView(ViewMaster.VIEW_LOCATIONS);
        }
    }

    /**
     * Instantiates all the views and sets commands to them
     */
    private void initialize() {
        weatherView = new WeatherView();
        weatherView.addCommand(settingsCmd);
        weatherView.addCommand(locationCmd);
        weatherView.addCommand(backCmd);
        weatherView.setCommandListener(this);

        settingsView = new SettingsView();
        settingsView.addCommand(backCmd);
        settingsView.setCommandListener(this);

        locationsView = new LocationsView();

        // Replace the add command with platform specific commands
        if (System.getProperty("microedition.platform").indexOf("sw_platform=S60;sw_platform_version=5.3") > -1) {
            // Prevent Add turning into a check mark on Symbian Belle
            addCmd = new Command("Add", Command.EXIT, 2);
        }

        Resources r = Resources.getInstance(locationsView.getWidth(), locationsView.getHeight());
        if (IconCommandFactory.iconCommandsSupported()) {
            addCmd = IconCommandFactory.getIconCommand("Add", r.getAddIcon(), r.getAddIcon(), IconCommand.SCREEN, 1);
        }

        locationsView.addCommand(addCmd);
        locationsView.addCommand(backCmd);
        locationsView.setCommandListener(this);

        searchView = new SearchView();
        searchView.addCommand(cancelCmd);
        searchView.setCommandListener(this);
    }

    /**
     * Sets a view to be displayed on the screen
     * @param id View ID
     */
    private synchronized void setView(int id) {
        // Deactivate previous one
        View current = (View) getView(activeView);
        current.deactivate();

        final Displayable next = getView(id);

        display.setCurrent(next);

        // Activate the next one
        activeView = id;
        ((View) next).activate();
    }

    /**
     * Appends or removes the GO-command button to weather view
     */
    public void showGoCommmand(boolean show) {
        if (show) {
            weatherView.addCommand(goCmd);
        }
        else {
            weatherView.removeCommand(goCmd);
        }

    }
}
