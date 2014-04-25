/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.ads;

import InneractiveSDK.IADView;
import com.nokia.example.weatherapp.network.Keys;
import com.nokia.example.weatherapp.network.Network;
import javax.microedition.midlet.MIDlet;

/**
 * Singleton class which manages the ad retrieval in its own thread.
 */
public class AdManager {

    public static final int IDLE = 0;
    public static final int GET_BANNER_AD = 1;
    public static final int DISPLAY_FULL_AD = 2;
    private static AdManager instance = null;
    private Worker worker;
    private boolean running = false;
    private AdListener listener = null;
    private static MIDlet context;

    /**
     * Thread executing the ad retrieval
     */
    private class Worker
            extends Thread {

        private int task = 0;
        private long interval = -1;

        public synchronized void run() {
            running = true;
            while (running) {
                if (Network.isAllowed()) {
                    switch (task) {
                        case DISPLAY_FULL_AD:
                            if (listener == null) {
                                IADView.displayInterstitialAd(context, Keys.APPID);
                            }
                            else {
                                try {
                                    IADView.displayInterstitialAd(context, Keys.APPID, listener);
                                }
                                catch (NullPointerException e) {
                                    // Internal exception occurs in Inneractive plugin
                                    // version 1.0.9 if no valid API key is
                                    // provided. Fail silently.
                                }
                            }
                            break;
                        case GET_BANNER_AD:
                            Ad ad = null;
                            try {
                                ad = new Ad(IADView.getBannerAdData(context, Keys.APPID));
                            }
                            catch (IllegalArgumentException iae) {
                            }
                            if (listener != null) {
                                listener.bannerReceived(ad);
                            }
                            break;
                    }
                }
                try {
                    if (interval < 0) {
                        task = IDLE;
                        wait();
                    }
                    else {
                        wait(interval);
                    }
                }
                catch (InterruptedException ex) {
                }
            }
        }

        public synchronized void doTask(int task, long interval) {
            if (context == null) {
                return;
            }
            this.task = task;
            this.interval = interval;
            notify();
        }
    }

    protected AdManager() {
        worker = new Worker();
        worker.start();
    }

    /**
     * Returns a singleton instance of AdManager. Note, that getInstance taking Midlet as parameter needs to be called
     * at least once before the AdManager can be used for ad
     */
    public static AdManager getInstance() {
        if (instance == null) {
            instance = new AdManager();
        }
        return instance;
    }

    /**
     * Returns a singleton instance of AdManager
     * @param midlet Midlet is used as context for determining ad sizes, application id, distribution id etc.
     * This method needs to be called at least once to make the AdManager functional
     */
    public static AdManager getInstance(MIDlet midlet) {
        context = midlet;
        return getInstance();
    }

    /**
     * Sets listener for ad updates
     */
    public void setListener(AdListener listener) {
        this.listener = listener;
    }

    /**
     * Starts banner retrieval
     * @param interval Interval defines how long AdManager will wait before retieving a new ad.
     * -1 can be used for non-repitive ad retrieval.
     */
    public synchronized void fetchBanner(long interval) {
        worker.doTask(GET_BANNER_AD, interval);
    }

    /**
     * Starts fullscreen ad retrieval.
     * Notifies the listener after the ad has been put to display.
     * @param interval Interval defines how long AdManager will wait before retieving a new ad.
     * -1 can be used for non-repitive ad retrieval.
     */
    public synchronized void showFullscreenAd(long interval) {
        worker.doTask(DISPLAY_FULL_AD, interval);
    }
}
