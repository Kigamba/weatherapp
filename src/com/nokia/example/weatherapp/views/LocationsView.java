/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.views;

import com.nokia.example.weatherapp.location.Location;
import com.nokia.example.weatherapp.resources.Settings;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;

/**
 * View of recent locations
 */
public class LocationsView
        extends Form
        implements View {

    private Vector locations;
    private ChoiceGroup list;

    public LocationsView() {
        super("Recent locations");
        list = new ChoiceGroup(null, Choice.EXCLUSIVE);
        list.setFitPolicy(Choice.TEXT_WRAP_ON);
        append(list);
    }

    /**
     * Populates the recent locations list. If there are no locations to show, redirects to search view
     */
    public void activate() {
        locations = Settings.recentLocations;
        if (!locations.isEmpty()) {
            int length = locations.size();
            for (int i = 0; i < length; i++) {
                Location location = (Location) locations.elementAt(i);
                list.append(location.city + ", " + location.country, null);
            }
            int index = locations.indexOf(Settings.location);
            if (index > -1) {
                list.setSelectedIndex(index, true);
            }
        }
    }

    /**
     * Empties the list items
     */
    public void deactivate() {
        selectItem();
        list.deleteAll();
    }

    public void selectItem() {
        int selected = list.getSelectedIndex();
        if (selected > -1) {
            Settings.setLocation((Location) locations.elementAt(selected));
        }
    }
}
