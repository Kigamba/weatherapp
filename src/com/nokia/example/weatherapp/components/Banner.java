/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.components;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Image;

/**
 * Represents a banner ad 
 */
public class Banner
        extends Button {

    private boolean appearing = false;
    private boolean disappearing = false;
    private int originY = 0;
    private int destY = 0;
    private Timer animator;
    private Timer timeout;

    public Banner(Image released, Image pressed, int originY, int destY) {
        super(released, pressed);
        this.originY = originY;
        this.destY = destY;
        setVisible(false);
        animator = new Timer();
        timeout = new Timer();
    }

    /**
     * Show the banner with eased slides in animation for specified time
     * @param duration Time that banner will be shown before hiding the banner
     */
    public void show(final int duration) {
        if (isVisible()) {
            return;
        }
        appearing = true;
        setVisible(true);
        animator.cancel();
        animator = new Timer();
        animator.schedule(new TimerTask() {

            public void run() {
                int dy = (int) (0.9 * (getY() - destY));
                setPosition(getX(), destY + dy);
                if (dy == 0) {
                    timeout.schedule(new TimerTask() {

                        public void run() {
                            hide();
                        }
                    }, duration);
                    this.cancel();
                    appearing = false;
                }
            }
        }, 0, 100);
    }

    /**
     * Hides the banner with eased slide out animation
     */
    public void hide() {
        if (!isVisible() || disappearing) {
            return;
        }
        disappearing = true;
        animator.cancel();
        animator = new Timer();
        animator.schedule(new TimerTask() {

            public void run() {
                int dy = (int) ((getY() - destY + 1) * 1.1);
                int y = destY + dy;
                setPosition(getX(), y);
                if (y > originY) {
                    setPosition(getX(), originY);
                    this.cancel();
                    setVisible(false);
                    disappearing = false;
                    if (isSeleted()) {
                        notifyDeselected();
                    }
                }
            }
        }, 0, 100);
    }

    public int getOrigin() {
        return originY;
    }

    public void setOrigin(int y) {
        originY = y;
    }

    public int getDestination(int y) {
        return destY;
    }

    public void setDestination(int y) {
        destY = y;
    }

    public boolean isAppearing() {
        return appearing;
    }

    public boolean isDisappearing() {
        return disappearing;
    }
}
