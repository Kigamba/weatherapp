/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.views;

import com.nokia.example.weatherapp.Main;
import com.nokia.example.weatherapp.components.DividerItem;
import com.nokia.example.weatherapp.components.ListItem;
import com.nokia.example.weatherapp.components.LoaderItem;
import com.nokia.example.weatherapp.location.Location;
import com.nokia.example.weatherapp.network.GetLocationsOperation;
import com.nokia.example.weatherapp.network.Network;
import com.nokia.example.weatherapp.resources.Settings;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.Ticker;

/**
 * Search view with auto completer functionality 
 */
public class SearchView
        extends Form
        implements View, ItemStateListener, ItemCommandListener,
                   ListItem.Listener,
                   GetLocationsOperation.Listener {

    private final Command selectCmd = new Command("Select", Command.ITEM, 1);
    private Display display;
    private TextField searchField;
    private String lastSearch = "";
    private Timer throttle;
    private Vector locations;

    public SearchView() {
        super("Search city");
        throttle = new Timer();
        searchField = new TextField(null, "", 50,
                                    TextField.NON_PREDICTIVE
                & TextField.INITIAL_CAPS_WORD);
        append(searchField);
        setItemStateListener(this);
    }

    public void setDisplay(Display d) {
        this.display = d;
    }

    /**
     * Performs a location search, if text input changes.     
     * @param item
     */
    public void itemStateChanged(Item item) {
        if (item instanceof TextField && !searchField.getString().equals(
                lastSearch)) {
            throttleSearch();
        }
    }

    /**
     * Delays location search and cancels a possible pending search
     */
    public void throttleSearch() {
        if (throttle != null) {
            throttle.cancel();
        }

        throttle = new Timer();
        throttle.schedule(new TimerTask() {

            public void run() {
                searchLocations();
                cancel();
            }
        }, 1000);
    }

    /**
     * Performs a location search with the current input string
     */
    public void searchLocations() {
        String searchText = searchField.getString();
        if (!searchText.equals("") && searchText.length() > 2) {
            clearList();
            append(new LoaderItem());
            // Create new GET request
            new GetLocationsOperation(this, searchText).start();
        }
    }

    public void clearList() {
        while (size() > 1) {
            delete(1);
        }
    }

    public void activate() {
        searchField.setString("");
        clearList();
        if (display != null) {
            display.setCurrentItem(searchField);
        }
        if (!Network.isAllowed()) {
            setTicker(
                    new Ticker(
                    "No network access. Please restart and allow network access."));
        }
    }

    public void deactivate() {
    }

    /**
     * Handles the select command related to a list item
     * @param c
     * @param item
     */
    public void commandAction(Command c, Item item) {
        if (item instanceof ListItem) {
            Location location = (Location) locations.elementAt(
                    ((ListItem) item).getIndex());
            if (location != null) {
                Settings.setLocation(location);
                ViewMaster.getInstance().backView();
            }
        }
    }

    public void clicked(ListItem listItem) {
        commandAction(selectCmd, listItem);
    }

    /**
     * Updates location list
     * @param locationsData List of Location objects
     */
    public synchronized void locationsReceived(Vector locationsData) {
        clearList();
        if (locationsData == null) {
            ViewMaster.getInstance().showAlert("Network error",
                                               "Sorry, cannot connect to internet.",
                                               AlertType.INFO);
        }
        else if (!locationsData.isEmpty()) {
            if (System.getProperty("microedition.platform").indexOf("Nokia303") > -1) {
                append(new DividerItem(getWidth()));
            }
            locations = locationsData;
            int length = locationsData.size();
            for (int i = 0; i < length; i++) {
                Location location = (Location) locationsData.elementAt(i);

                // Create new result item
                final ListItem listItem = new ListItem(
                        location.city + ", " + location.country, getWidth(), 10,
                        i, true);
                listItem.addListener(this);
                listItem.setDefaultCommand(selectCmd);
                listItem.setItemCommandListener(this);
                // Append the item to form from within UI thread
                Display.getDisplay(Main.getInstance()).callSerially(new Runnable() {
                    public void run() {
                        append(listItem);
                    }
                });        
                
            }
        }
    }

    protected synchronized void sizeChanged(int w, int h) {
        // Deliver size changed event to custom items, if not running Symbian platform
        // On Symbian resizing occasionally turns custom items into white boxes
        if (System.getProperty("microedition.platform").indexOf("S60") == -1) {
            int length = size();
            for (int i = 1; i < length; i++) {
                Item item = get(i);
                if (item instanceof ListItem) {
                    ((ListItem) item).sizeChanged(w, h);
                }
                else if (item instanceof DividerItem) {
                    ((DividerItem) item).sizeChanged(w, h);
                }
            }
        }
    }
}
