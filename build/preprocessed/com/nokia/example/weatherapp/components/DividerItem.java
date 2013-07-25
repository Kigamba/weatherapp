/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.components;

import com.nokia.example.weatherapp.Main;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

/**
 * CustomItem trying to look and feel like a regular list item in List screen.
 */
public class DividerItem
        extends CustomItem {

    private int prefWidth;
    private int prefHeight;

    public DividerItem(int width) {
        super(null);
        prefWidth = width;
        prefHeight = 1;
    }

    public void paint(Graphics g, int width, int height) {
        // Get platform foreground font color
        g.setColor(Display.getDisplay(Main.getInstance()).getColor(Display.COLOR_BORDER));
        g.drawLine(0, 0, width, 0);
    }

    public void sizeChanged(int w, int h) {
        prefWidth = w;
        invalidate();
        repaint();
    }

    /**
     * Called by the system to retrieve minimum width required for this control.
     */
    protected int getMinContentWidth() {
        return prefWidth;
    }

    /**
     * Called by the system to retrieve minimum height required for this control.
     */
    protected int getMinContentHeight() {
        return prefHeight;
    }

    /**
     * Called by the system to retrieve preferred width for this control.
     */
    protected int getPrefContentWidth(int arg0) {
        return prefWidth;
    }

    /**
     * Called by the system to retrieve preferred height for this control.
     */
    protected int getPrefContentHeight(int arg0) {
        return prefHeight;
    }

    protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
        return false;
    }
}
