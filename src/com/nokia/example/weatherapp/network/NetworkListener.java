/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

/**
 * Interface for reporting network operations.
 */
public interface NetworkListener {

    /**
     * Called when a HTTP POST request is done.
     * @param response Server reply
     */
    void networkHttpPostResponse(String response);

    /**
     * Called when a HTTP GET request is done.
     * @param response Server reply
     */
    void networkHttpGetResponse(String response);
}
