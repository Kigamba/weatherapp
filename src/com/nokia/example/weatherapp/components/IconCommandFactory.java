/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.components;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

/**
 * Produces icon commands, if the platform supports them.
 * IconCommand is supported from Java Runtime 2.0.0 for Series 40 onwards.
 */
public abstract class IconCommandFactory {

    private static IconCommandFactory implementation = null;

    /**
     * Try to instantiate IconCommandFactory
     */
    static {
        try {
            Class.forName("com.nokia.mid.ui.IconCommand");
            Class c = Class.forName("com.nokia.example.weatherapp.components.IconCommandFactoryImplementation");
            implementation = (IconCommandFactory) (c.newInstance());
        }
        catch (Exception e) {
            // Icon commands not supported
        }
    }

    protected IconCommandFactory() {
    }

    /*
     * Creates an IconCommand
     */
    public abstract Command createIconCommand(String label, Image upState, Image downState, int type, int priority);

    /**
     * Returns new IconCommand or null, if IconCommands are not supported
     */
    public static Command getIconCommand(String label, Image upState, Image downState, int type, int priority) {
        if (implementation == null) {
            return null;
        }
        return implementation.createIconCommand(label, upState, downState, type, priority);
    }

    public static boolean iconCommandsSupported() {
        return implementation != null ? true : false;
    }
}

/**
 * Creates an IconCommand. Hides the usage of IconCommand from the linker
 */
class IconCommandFactoryImplementation
        extends IconCommandFactory {

    protected IconCommandFactoryImplementation() {
    }

    public Command createIconCommand(String label, Image upState, Image downState, int type, int priority) {
        return new com.nokia.mid.ui.IconCommand(label, upState, downState, type, priority);
    }
}
