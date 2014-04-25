/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.components;

import javax.microedition.lcdui.Gauge;

/**
 * Loadin indicator based on a regular gauge item.
 */
public class LoaderItem
        extends Gauge {

    public LoaderItem() {
        super("", false, Gauge.INDEFINITE, CONTINUOUS_RUNNING);
    }
}
