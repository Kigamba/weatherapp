/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.ads;

import InneractiveSDK.InneractiveAdEventsListener;

/**
 * Interface for ad listeners.
 */
public interface AdListener
        extends InneractiveAdEventsListener {

    /**
     * Called when and ad has been returned from the Ad SDK
     * @param ad Contains a banner image and an endpoint url. Ad is null, if the retrieval was unsuccesful.
     */
    public void bannerReceived(Ad ad);
}
