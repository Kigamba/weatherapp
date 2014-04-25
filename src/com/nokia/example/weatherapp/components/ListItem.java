/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.components;

import com.nokia.example.weatherapp.Main;
import com.nokia.example.weatherapp.helpers.TextWrapper;
import com.nokia.example.weatherapp.helpers.TouchChecker;
import com.nokia.mid.ui.LCDUIUtil;
import java.util.Vector;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * CustomItem trying to look and feel like a regular list item in List screen.
 */
public class ListItem
        extends CustomItem {

    private int padding;
    private int prefWidth;
    private int prefHeight;
    private int index = 0;
    private boolean pressed = false;
    private final Font font = Font.getFont(Font.FONT_STATIC_TEXT);
    private Vector lines;
    private String label;
    private Vector listeners = new Vector();

    public interface Listener {

        void clicked(ListItem listItem);
    }

    public ListItem(String label, int width, int padding, int index, boolean directSelect) {
        super("");
        this.label = label;
        this.padding = padding;
        this.index = index;

        // Receive touch events without having to be set as current (selected) first.
        if (directSelect && TouchChecker.touchEnabled() && TouchChecker.s40UITraitsSupported()) {
            LCDUIUtil.setObjectTrait(this, "nokia.ui.s40.item.direct_touch", new Boolean(true));
        }
        prefWidth = width;
        wrapLabel(width);
    }

    public void paint(Graphics g, int width, int height) {
        // Get platform foreground font color
        g.setColor(Display.getDisplay(Main.getInstance()).getColor(Display.COLOR_FOREGROUND));

        // Draw wrapped strings
        int length = lines.size();
        for (int i = 0; i < length; i++) {
            String line = (String) lines.elementAt(i);
            g.drawString(line, padding, padding + i * font.getHeight(), Graphics.TOP | Graphics.LEFT);
        }
    }

    public int getIndex() {
        return index;
    }

    public void addListener(Listener listener) {
        listeners.addElement(listener);
    }

    public void sizeChanged(int w, int h) {
        prefWidth = w;
        wrapLabel(w);
        repaint();
    }

    /**
     * Wraps label to the given width column. Affects the preferred height
     */
    protected void wrapLabel(int width) {
        lines = TextWrapper.wrapTextToWidth(label, width - 2 * padding, font);
        prefHeight = 2 * padding + lines.size() * font.getHeight();
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
    protected int getPrefContentWidth(int height) {
        return prefWidth;
    }

    /**     
     * Called by the system to retrieve preferred height for this control.
     */
    protected int getPrefContentHeight(int width) {
        return prefHeight;
    }

    protected void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        pressed = true;
    }

    protected void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
        if (pressed && y >= 0 && y <= prefHeight) { // For Symbian, the y value needs to be checked also
            int length = listeners.size();
            for (int i = 0; i < length; i++) {
                ((Listener) listeners.elementAt(i)).clicked(this);
            }
        }
        pressed = false;
    }

    protected int getPadding() {
        return padding;
    }

    protected boolean isPressed() {
        return pressed;
    }
}
