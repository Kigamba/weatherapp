/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.resources;

import com.nokia.example.weatherapp.location.Location;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Class containing application settings.
 */
public class Settings {

    // Wind speed units
    public static final int MPS = 0;
    public static final int KMPH = 1;
    public static final int MPH = 2;
    public static final int KNOTS = 3;
    // Temperature units    
    public static final int CELSIUS = 0;
    public static final int FAHRENHEIT = 1;
    public static int windSpeedUnit = KMPH;
    public static int temperatureUnit = CELSIUS;
    public static Location location = new Location();
    public static Vector recentLocations = new Vector();

    /**
     * Sets the location and appends it to recent locations as well
     * @param lctn Location to be set
     */
    public static void setLocation(Location lctn) {
        location = lctn;

        int length = recentLocations.size();
        for (int i = 0; i < length; i++) {
            Location recent = (Location) recentLocations.elementAt(i);
            if (lctn.toString().equals(recent.toString())) {
                recentLocations.removeElementAt(i);
                recentLocations.insertElementAt(lctn, 0);
                return;
            }
        }
        recentLocations.insertElementAt(lctn, 0);
        if (recentLocations.size() > 10) {
            recentLocations.setSize(10);
        }
    }

    /**
     * Save settings and locations to record storage
     */
    public static void save() {
        saveSettings();
        saveLocations();
    }

    /**
     * Load settings and locations from record storage
     */
    public static void load() {
        loadSettings();
        loadLocations();
    }

    private static void saveSettings() {
        try {
            RecordStore.deleteRecordStore("settings"); // Clear data
        }
        catch (Exception e) { /* Nothing to delete */ }

        try {
            RecordStore rs = RecordStore.openRecordStore("settings", true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(windSpeedUnit);
            dos.writeInt(temperatureUnit);

            byte[] b = baos.toByteArray();
            // Add it to the record store
            rs.addRecord(b, 0, b.length);
            rs.closeRecordStore();
        }
        catch (RecordStoreException rse) {
            rse.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void saveLocations() {
        try {
            RecordStore.deleteRecordStore("locations"); // Clear data
        }
        catch (Exception e) { /* Nothing to delete */ }

        try {
            RecordStore rs = RecordStore.openRecordStore("locations", true);
            int count = recentLocations.size();
            for (int i = 0; i < count; i++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                Location lctn = (Location) recentLocations.elementAt(i);
                dos.writeUTF(lctn.city);
                dos.writeUTF(lctn.country);
                byte[] b = baos.toByteArray();
                // Add it to the record store
                rs.addRecord(b, 0, b.length);
            }
            rs.closeRecordStore();
        }
        catch (RecordStoreException rse) {
            rse.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void loadSettings() {
        try {
            RecordStore rs = RecordStore.openRecordStore("settings", true);
            RecordEnumeration re = rs.enumerateRecords(null, null, true);

            // There might be nothing saved.
            if (!re.hasNextElement()) {
                return;
            }
            int id = re.nextRecordId();
            ByteArrayInputStream bais = new ByteArrayInputStream(rs.getRecord(id));
            DataInputStream dis = new DataInputStream(bais);
            try {
                windSpeedUnit = dis.readInt();
                temperatureUnit = dis.readInt();
            }
            catch (EOFException eofe) {
                eofe.printStackTrace();
            }
            rs.closeRecordStore();
        }
        catch (RecordStoreException rse) {
            rse.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void loadLocations() {
        try {
            boolean symbian = System.getProperty("microedition.platform").indexOf("S60") > -1;
            RecordStore rs = RecordStore.openRecordStore("locations", true);
            RecordEnumeration re = rs.enumerateRecords(null, null, true);
            while (re.hasNextElement()) {
                int id = re.nextRecordId();
                ByteArrayInputStream bais = new ByteArrayInputStream(rs.getRecord(id));
                DataInputStream dis = new DataInputStream(bais);
                try {
                    Location location = new Location();
                    location.city = dis.readUTF();
                    location.country = dis.readUTF();
                    if (symbian) { // On Symbian the elements have been stored in opposite order compared to S40
                        recentLocations.addElement(location);
                    }
                    else {
                        recentLocations.insertElementAt(location, 0);
                    }
                }
                catch (EOFException eofe) {
                    eofe.printStackTrace();
                }
            }
            rs.closeRecordStore();
        }
        catch (RecordStoreException rse) {
            rse.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
