/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.helpers;

/**
 * Puts a thread into sleep
 */
public class Sleeper {

    public synchronized void sleep(int delay) {
        try {
            wait(delay);
        }
        catch (InterruptedException e) {
        }
    }

    public synchronized void wakeup() {
        notifyAll();
    }
}
