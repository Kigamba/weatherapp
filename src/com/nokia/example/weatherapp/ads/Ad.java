/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.ads;

import java.util.Vector;
import javax.microedition.lcdui.Image;

/**
 * Banner ad containing a banner image and an endpoint URL.
 */
public class Ad {

    private Image banner = null;
    private String url = null;

    /**
     * @param banner Banner image
     * @param url Endpoint url of the ad
     * @throws IllegalArgumentException If the vector cannot be turned into an Ad,
     * an IllegalArgumentException gets thrown
     */
    public Ad(Image banner, String url) throws IllegalArgumentException {
        this.banner = banner;
        this.url = url;
        validate();
    }

    /**
     * Constructs an ad based on a vector
     * @param ad Vector holding a banner image and an endpoint url
     * @throws IllegalArgumentException If the vector cannot be turned into an Ad,
     * an IllegalArgumentException gets thrown
     */
    public Ad(Vector ad) throws IllegalArgumentException {
        try {
            this.banner = (Image) ad.elementAt(0);
            this.url = (String) ad.elementAt(1);
        }
        catch (Exception e) {
            throw new IllegalArgumentException();
        }
        validate();
    }

    public Image getBanner() {
        return banner;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Validates the ad data
     * @throws IllegalArgumentException if ad data is invalid
     */
    private void validate() throws IllegalArgumentException {
        if (banner == null || banner.getWidth() == 0 || banner.getHeight() == 0 || url == null) {
            throw new IllegalArgumentException();
        }
    }
}
