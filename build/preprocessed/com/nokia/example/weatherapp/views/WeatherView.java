/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.views;

import com.nokia.example.weatherapp.Main;
import com.nokia.example.weatherapp.ads.Ad;
import com.nokia.example.weatherapp.ads.AdListener;
import com.nokia.example.weatherapp.ads.AdManager;
import com.nokia.example.weatherapp.components.Banner;
import com.nokia.example.weatherapp.components.Button;
import com.nokia.example.weatherapp.components.LoaderImage;
import com.nokia.example.weatherapp.helpers.ImageHelper;
import com.nokia.example.weatherapp.helpers.TextWrapper;
import com.nokia.example.weatherapp.location.Location;
import com.nokia.example.weatherapp.location.LocationFinder;
import com.nokia.example.weatherapp.location.LocationListener;
import com.nokia.example.weatherapp.network.GetForecastsOperation;
import com.nokia.example.weatherapp.network.GetLocationsOperation;
import com.nokia.example.weatherapp.network.Network;
import com.nokia.example.weatherapp.network.Weather;
import com.nokia.example.weatherapp.orientation.Orientation;
import com.nokia.example.weatherapp.resources.Resources;
import com.nokia.example.weatherapp.resources.Settings;
import com.nokia.example.weatherapp.resources.Visual;
import com.nokia.mid.ui.VirtualKeyboard;
import java.util.Calendar;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.game.Sprite;

/**
 * View displaying the weather forecast
 */
public class WeatherView
        extends GameCanvas
        implements View, LocationListener, AdListener, Button.Listener {

    private static final int AD_DURATION = 60000;
    private static final boolean overlayBackBtn = detectCategoryBar();
    private boolean startup = true;
    private boolean closing = false;
    private boolean exitAdShown = false;
    private int day = 0;
    private int downX = -1; // Flick start x coordinate
    private Graphics g;
    private Resources r;
    private Visual v;
    private Timer timer;
    private Timer closingTimer = new Timer();
    private LoaderImage loaderImg;
    private Location currentLocation;
    private LocationFinder locationFinder;
    private Ad ad = null;
    private AdManager adManager;
    private Banner banner;
    private Vector forecasts = null;
    private Vector lines = new Vector();
    private Button prevBtn;
    private Button nextBtn;
    private Sprite windDirection;
    private Sprite background;
    private Calendar calendar = Calendar.getInstance();
    private String message = "";
    private String caption = "";

    public WeatherView() {
        super(false);
        this.setFullScreenMode(false);
        setTitle("WeatherApp");

        g = getGraphics();
        r = Resources.getInstance(getWidth(), getHeight());
        v = Visual.getInstance();
        initialize();
        if (Network.isAllowed()) {
            adManager = AdManager.getInstance(Main.getInstance());
            adManager.setListener(this);
            adManager.fetchBanner(AD_DURATION + 4000);
        }
        setMessage("Welcome to WeatherApp!");

        setCommandListener(ViewMaster.getInstance());
        hideOpenKeypadCommand();
        Orientation.enableOrientations();
    }

    /**
     * Starts the location retrieval
     * @param interval
     */
    public synchronized void startOrUpdateLocationFinder() {
        if (Network.isAllowed() && !closing) {
            if (locationFinder == null) {
                locationFinder = LocationFinder.getLocationFinder();
            }

            if (locationFinder != null) {
                setMessage("Retrieving location...");
                locationFinder.setLocationListener(this);
                locationFinder.start(45);
            }
            else {
                ViewMaster.getInstance().openLocationsView();
            }
        }
        else {
            // App requires network access            
            ViewMaster.getInstance().showAlert("Access required", "Network access is required. Please restart "
                    + "and allow network access.", AlertType.INFO);
            setMessage("Network access required.");
            loaderImg.hide();
        }
    }

    /**
     * Cancels location retrieval
     */
    public synchronized void stopLocationFinder() {
        if (locationFinder != null) {
            locationFinder.cancel();
        }
    }

    /**
     * Draws the forecasts and messages
     */
    public void render() {
        Weather w = null;
        Vector f = forecasts;

        if (f != null) {
            try {
                w = (Weather) forecasts.elementAt(day);
            }
            catch (Exception e) {
                // On rare occasions the day might be out of bounds during a weather update.
            }
        }

        g = getGraphics();
        g.setColor(v.COLOR_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        background.paint(g);
        Image clouds = r.getCloudsBack();
        if (clouds != null) {
            g.drawImage(clouds, getWidth() / 2, getHeight(), Graphics.BOTTOM | Graphics.HCENTER);
        }

        if (w == null) {
            g.setFont(v.FONT_MEDIUM);
            g.setColor(v.COLOR_PRIMARY_TEXT);
            int anchorX = getWidth() / 2;
            int anchorY = getHeight() / 8 * 5;
            int length = lines.size();
            for (int i = 0; i < length; i++) {
                g.drawString((String) lines.elementAt(i), anchorX, anchorY, Graphics.TOP | Graphics.HCENTER);
                anchorY += g.getFont().getHeight();
            }
        }
        else {
            drawNaviHeader();
            WeatherLayout.layout(w, g, v, r, this);
        }

        banner.paint(g);
        loaderImg.paint(g);
    }

    /**
     * Draws the navigation header
     */
    private void drawNaviHeader() {
        g.setColor(v.COLOR_HEADER_TEXT);
        g.setFont(v.FONT_MEDIUM_BOLD);
        int anchor = Graphics.TOP | Graphics.HCENTER;
        int y = v.HEIGHT_HEADER / 2 - v.FONT_MEDIUM_BOLD.getHeight() / 2;
        g.drawString(caption, getWidth() / 2, y, anchor);
        nextBtn.paint(g);
        prevBtn.paint(g);
    }

    /**
     * Initializes weather view
     */
    private void initialize() {
        Image img = r.getLoader();
        loaderImg = new LoaderImage(img, getWidth() / 2, getHeight() / 15 * 7,
                                    img.getWidth() / 4, img.getHeight());

        nextBtn = new Button(r.getNextUp(), r.getNextDown());
        nextBtn.setPosition(getWidth() - nextBtn.getWidth(), 0);

        prevBtn = new Button(r.getPrevUp(), r.getPrevDown());
        prevBtn.setPosition(0, 0);

        nextBtn.addListener(this);
        prevBtn.addListener(this);

        img = r.getWindDirection();
        windDirection = new Sprite(img, img.getWidth() / 16, img.getHeight());
        windDirection.defineReferencePixel(img.getHeight(), img.getHeight());

        img = r.getBackground();
        background = new Sprite(img);
        updateBackground();

        // Initialize banner to prevent null pointer exceptions
        banner = new Banner(Image.createImage(1, 1), Image.createImage(1, 1), -1, -1);
    }

    /**
     * Checks the key events
     * @param keyCode
     */
    protected void keyPressed(int keyCode) {
        switch (getGameAction(keyCode)) {
            case GameCanvas.LEFT:
                prevBtn.select();
                break;
            case GameCanvas.RIGHT:
                nextBtn.select();
                break;
            case GameCanvas.UP:
                if (banner.isVisible()) {
                    banner.toggle();
                    ViewMaster.getInstance().showGoCommmand(banner.isSeleted());
                }
                break;
            case GameCanvas.DOWN:
                if (banner.isVisible()) {
                    banner.toggle();
                    ViewMaster.getInstance().showGoCommmand(banner.isSeleted());
                }
        }
    }

    /**
     * Checks the key events
     * @param keyCode
     */
    protected void keyReleased(int keyCode) {
        switch (getGameAction(keyCode)) {
            case GameCanvas.LEFT:
                prevBtn.deselect();
                setDay(day - 1);
                break;
            case GameCanvas.RIGHT:
                nextBtn.deselect();
                setDay(day + 1);
                break;
        }
    }

    /**
     * Changes day
     * @param indx
     */
    public void setDay(int indx) {
        if (forecasts != null) {
            if (indx >= 0 && indx < forecasts.size()) {
                Weather weather = (Weather) forecasts.elementAt(indx);
                day = indx;
                r.clearWeatherIcon();
                windDirection.setFrame((int) ((Integer.parseInt(weather.windDirectionDegrees) + 180) / 22.5 + 0.5) % 16);
            }
            setWeekday();
            updateButtons();
        }
    }

    public int getDay() {
        return day;
    }

    public Sprite getWinddirection() {
        return windDirection;
    }

    /**
     * Updates button visibilities
     */
    private void updateButtons() {
        if (day >= 0 && day < forecasts.size()) {
            prevBtn.setVisible(true);
            nextBtn.setVisible(true);
        }
        if (day == 0) {
            prevBtn.setVisible(false);
        }
        else if (day == forecasts.size() - 1) {
            nextBtn.setVisible(false);
        }
    }

    /**
     * Handles the location updates
     * @param location
     */
    public void locationUpdated(Location location) {
        if (location == null) {
            informLocationNotAvailable();
        }
        else {

            // Create new GET request
            new GetLocationsOperation(new GetLocationsOperation.Listener() {

                // Define callback for the response data
                public synchronized void locationsReceived(Vector locationsData) {
                    Vector locations = locationsData;
                    if (locations == null || locations.isEmpty()) {
                        informLocationNotAvailable();
                    }
                    else {
                        Location received = (Location) locations.elementAt(0);
                        Settings.setLocation(received);
                        setTitle(received.city);
                        updateForecasts();
                    }
                }
            }, location.latitude + "," + location.longitude).start();
        }
    }

    public void informLocationNotAvailable() {
        if (!closing) {
            loaderImg.hide();
            setMessage("Location not available.");
            ViewMaster.getInstance().openLocationsView();
        }
    }

    /**
     * Retrieves forecast for current location
     */
    public void updateForecasts() {
        stopLocationFinder();
        Location location = Settings.location;
        if (!location.city.equals("") && location != currentLocation) {
            currentLocation = location;
            setMessage("Updating forecast...");
            forecasts = null;
            loaderImg.show();

            // Create GET request for forecast
            new GetForecastsOperation(new GetForecastsOperation.Listener() {

                // Define callback for response data
                public synchronized void forecastsReceived(Vector forecastData) {
                    if (!closing) {
                        if (forecastData == null) {
                            setMessage("Could not connect to internet.");
                            ViewMaster.getInstance().showAlert("Network error", "Sorry, cannot connect to internet.",
                                                               AlertType.INFO);
                        }
                        else if (forecastData.isEmpty()) {
                            setMessage("Forecast not available.");
                            ViewMaster.getInstance().showAlert("No forecast",
                                                               "Sorry, unable to retrieve forecast data for current location.",
                                                               AlertType.ERROR);
                        }
                        else {
                            forecasts = forecastData;
                            setDay(0);
                        }
                        loaderImg.hide();
                    }
                }
            }, location.city + "," + location.country).start();
        }
    }

    public void activate() {
        Location location = Settings.location;
        if (!location.city.equals("")) {
            setTitle(Settings.location.city);
            updateForecasts();
        }
        else if (startup && !LocationFinder.supportsLocation()) {
            startup = false;
            loaderImg.hide();
            setMessage("Location unknown.");
            ViewMaster.getInstance().openLocationsView();
        }
        sizeChanged(getWidth(), getHeight());

        // Start the drawing loop
        timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                render();
                flushGraphics();
            }
        }, 0, 200);
    }

    public void deactivate() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Updates the visual theme according to the mode
     * @param mode
     */
    public void updateMode(int mode) {
        if (mode != Visual.getInstance().getMode()) {
            freeBackground();

            v = Visual.getInstance(mode);
            r.updateMode(mode);

            nextBtn.setReleasedImg(r.getNextUp());
            prevBtn.setReleasedImg(r.getPrevUp());
            Image img = r.getWindDirection();
            windDirection.setImage(img, img.getWidth() / 16, img.getHeight());

            img = r.getBackground();
            background.setImage(img, img.getWidth(), img.getHeight());
            updateBackground();
        }
    }

    /**
     * Handles the button clicks
     * @param button
     */
    public void clicked(Button button) {
        if (button == nextBtn) {
            setDay(day + 1);
        }
        else if (button == prevBtn) {
            setDay(day - 1);
        }
        else if (button == banner) {
            launchAdEndpoint();
        }
    }

    public void selected(Button button) {
    }

    public void deselected(Button button) {
        if (button == banner) {
            ViewMaster.getInstance().showGoCommmand(false);
        }
    }

    /**
     * Shows a full-screen ad before closing the application. Sets also a timer
     * for timeout just in case the ad request is taking too long.
     */
    public void showAdAndExit() {

        setMessage("Closing...");
        closing = true;
        forecasts = null;
        stopLocationFinder();
        banner.setVisible(false);
        loaderImg.show();
        if (adManager != null && Network.isAllowed()) {
            // In case ad retrieval takes too much time,
            // allow user to exit after 12 seconds
            closingTimer.schedule(new TimerTask() {

                public void run() {
                    Main.getInstance().exit();
                }
            }, 12000);
            // Release some memory for the fullscreen ad
            banner.setImage(Image.createRGBImage(new int[]{0x00000000}, 1, 1, true), 1, 1);
            r.clearClouds();

            adManager.showFullscreenAd(-1);
            exitAdShown = true;
        }
        else {
            Main.getInstance().exit();
        }
    }

    public boolean isClosing() {
        return closing;
    }

    /**
     * Launches ad url to browser. On older platforms the application needs to exit before launching
     */
    public void launchAdEndpoint() {
        try {
            Main midlet = Main.getInstance();
            if (midlet.platformRequest(ad.getUrl())) {
                midlet.exit();
            }
        }
        catch (ConnectionNotFoundException cnfe) {
        }
        catch (NullPointerException npe) {
        }
    }

    /**
     * Called when and ad has been returned from the Ad SDK
     * @param ad Contains a banner image and an endpoint url.
     * Ad is null, if the retrieval was unsuccesful.
     */
    public synchronized void bannerReceived(Ad ad) {
        if (!closing) {
            try {
                this.ad = ad;
                Image released = ad.getBanner();

                // Scale the banner for Series 40, unless it's the tiny screen
                if (System.getProperty("microedition.platform").indexOf("S60") == -1 && getWidth() > 128) {
                    float scaling = 1.0f;
                    /*
                     * Java Runtime 2.0 for Series 40 places the back button on top, of the
                     * banner, so the banner needs to be scaled smaller and anchored left                     
                     */
                    if (overlayBackBtn) {
                        scaling = 50 * 0.6f / released.getHeight();
                    }
                    else {
                        scaling = 50 * 0.74f / released.getHeight();
                    }
                    released = ImageHelper.scaleImage(released, scaling);
                }

                // Draw highlight to the banner
                Image pressed = Image.createImage(released.getWidth(), released.getHeight());
                Graphics bg = pressed.getGraphics();
                bg.setColor(0x76b8cc);
                bg.drawRect(0, 0, pressed.getWidth() - 1, pressed.getHeight() - 3);
                bg.setColor(0x000a42);
                bg.drawRect(1, 1, pressed.getWidth() - 3, pressed.getHeight() - 5);
                bg.drawRegion(released, 2, 2, pressed.getWidth() - 4, pressed.getHeight() - 6, Sprite.TRANS_NONE, 2, 2,
                              Graphics.LEFT | Graphics.TOP);

                banner = new Banner(released, pressed, getHeight(), getHeight() - released.getHeight());
                banner.addListener(this);
                if (overlayBackBtn) {
                    banner.setPosition(10, banner.getOrigin());
                }
                else {
                    banner.setPosition((getWidth() - banner.getWidth()) / 2, banner.getOrigin());
                }

                banner.show(AD_DURATION);
            }
            catch (Exception e) {
                // Received invalid banner
            }
        }
    }

    /*
     * InneractiveAdListener callbacks
     */
    public void inneractiveOnReceiveAd() {
        closingTimer.cancel();
    }

    public void inneractiveOnReceiveDefaultAd() {
        closingTimer.cancel();
    }

    public void inneractiveOnFailedToReceiveAd() {
        Main.getInstance().exit();
    }

    public void inneractiveOnClickAd() {
    }

    public void inneractiveOnSkipAd() {
        Main.getInstance().exit();
    }

    public void showNotify() {
        if (exitAdShown) {
            Main.getInstance().exit();
        }
    }

    /**
     * Handles the orientation switches
     * @param w
     * @param h
     */
    public void sizeChanged(int w, int h) {
        // Update the Graphics object and paint the background with the correct color
        // immediately after orientation change
        g = getGraphics();
        g.setColor(v.COLOR_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        flushGraphics();

        updateBackground();

        loaderImg.setRefPixelPosition(w / 2, h / 15 * 7);
        nextBtn.setPosition(w - nextBtn.getWidth(), 0);
        prevBtn.setPosition(0, 0);
        setMessage(message);
        int offsetY = banner.getOrigin() - banner.getY();
        banner.setOrigin(getHeight());
        banner.setDestination(getHeight() - banner.getHeight() - getHeight() / 48);
        if(overlayBackBtn && getWidth() <= getHeight()) {
            banner.setPosition(10, banner.getOrigin() - offsetY);
        } else {
            banner.setPosition((getWidth() - banner.getWidth()) / 2, banner.getOrigin() - offsetY);
        }
    }

    protected void pointerPressed(int x, int y) {
        nextBtn.touchDown(x, y);
        prevBtn.touchDown(x, y);
        downX = x;
        banner.touchDown(x, y);
    }

    protected void pointerReleased(int x, int y) {
        nextBtn.touchUp(x, y);
        prevBtn.touchUp(x, y);
        banner.touchUp(x, y);
    }

    protected void pointerDragged(int x, int y) {
        int flickX = (int) (x - downX) / 60;
        if (downX > 0 && Math.abs(flickX) == 1) {
            setDay(day - flickX);
            downX = -1;
        }
    }

    /**
     * Sets the current shown week day
     */
    private void setWeekday() {
        int weekday = (calendar.get(Calendar.DAY_OF_WEEK) - 1 + day) % 7 + 1;
        String str = "";
        switch (weekday) {
            case Calendar.MONDAY:
                str = "Monday";
                break;
            case Calendar.TUESDAY:
                str = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                str = "Wednesday";
                break;
            case Calendar.THURSDAY:
                str = "Thursday";
                break;
            case Calendar.FRIDAY:
                str = "Friday";
                break;
            case Calendar.SATURDAY:
                str = "Saturday";
                break;
            case Calendar.SUNDAY:
                str = "Sunday";
                break;
        }
        if (day == 0) {
            str = "Today (" + str.substring(0, 3) + ")";
        }
        caption = str;
    }

    private void setMessage(String msg) {
        if (!closing) {
            message = msg;
            lines = TextWrapper.wrapTextToWidth(msg, getWidth() / 10 * 9, v.FONT_MEDIUM);
        }
    }

    /**
     * Releases biggest resources
     */
    private void freeBackground() {
        // Release reference to current background avoiding also null pointer exception
        Image img = Image.createRGBImage(new int[]{0x00000000}, 1, 1, true);
        background.setImage(img, 1, 1);
        r.clearBackground();
        System.gc();
    }

    private void hideOpenKeypadCommand() {
        try {
            String keypad = System.getProperty("com.nokia.keyboard.type");
            if (keypad.equals("None")) {
                // This is a full touch device
                VirtualKeyboard.hideOpenKeypadCommand(true);
            }
        }
        // If this fails, there's probably no need for hiding the command
        catch (NoClassDefFoundError ncdfe) {
        }
        catch (Exception e) {
        }
    }

    /**
     * Rotates and centers background according to screen orientation
     */
    private void updateBackground() {
        if (getWidth() > getHeight()) {
            background.setTransform(Sprite.TRANS_ROT90);
        }
        else {
            background.setTransform(Sprite.TRANS_NONE);
        }
        background.setPosition((getWidth() - background.getWidth()) / 2, (getHeight() - background.getHeight()) / 2);
    }

    /**
     * Check if CategoryBar class is present
     */
    private static boolean detectCategoryBar() {
        try {
            Class.forName("com.nokia.mid.ui.CategoryBar");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
