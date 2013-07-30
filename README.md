WeatherApp
==========

WeatherApp is a simple weather application showing a four-day forecast with
temperatures, relative humidity, wind speed, and wind direction. The 
application retrieves the current location via CellID or GPS or a user-defined
location and uses it to retrieve the weather forecast. Worldweatheronline.com
APIs are used for both weather data and location search data. The application 
uses the org.json.me library for parsing JSON. The application has been 
designed to work Nokia Asha software platform 1.0 and on various Series 40 
devices with different form factors and input methods.

WeatherApp introduces in-app advertising in form of banners and
full-screen ads. In-app advertiseming generates better revenue for your
application. All the ads are provided by Inneractive.

This example demonstrates:
- Integration of in-app advertising
- Supporting different locationing methods on a range of devices
- Using JSON-based web services

The application is hosted in Nokia Developer Projects:
- http://projects.developer.nokia.com/JMEWeatherApp

For more information on the implementation, visit the wiki page:
- http://projects.developer.nokia.com/JMEWeatherApp/wiki

-------------------------------------------------------------------------------

PREREQUISITES

Java ME basics
Java ME threads and timers

-------------------------------------------------------------------------------

IMPORTANT FILES AND CLASSES

src\..\views\ViewMaster.java
src\..\views\WeatherView.java
src\..\network\ForecastsParser.java
src\..\network\LocationsParser.java
src\..\location\LocationFinder.java
src\..\location\CellIdLocationinderImpl.java
src\..\orientation\Orientation.java
src\..\ads\AdManager.java

Classes: JSONObject, LocationProvider, LocationUtil, IADView

-------------------------------------------------------------------------------

DESIGN CONSIDERATIONS

WeatherApp has been designed to scale across a range of Nokia Asha and Series 
40 devices. The UI graphics are available in two sizes and there are three
different layouts for different screen sizes. Layouts have been tested to scale
on the following screen resolutions: 128x160, 240x320, 320x240, 240x400,
400x240, 360x640, 640x360, and 640x480.

The MIDlet uses the fastest available positioning method, starting with cell ID
positioning. If cell ID positioning cannot be used, the MIDlet tries to
retrieve the location using GPS. If GPS is not available either, the user can
enter the location manually through the location search. The location search
supports auto-complete. The MIDlet has been designed so that missing location
APIs do not cause any errors: even if the device supports no location APIs, the
MIDlet still runs gracefully.

Worldweatheronline.com APIs are used for both weather data and location search
data. Due to licencing terms, the API key is not published in the source code
of this application. If you compile the application from the sources, the 
application is run in test mode without the WorldWeatherOnline key. In the 
test mode the application uses a static example forecast data instead of a real 
forecast.

In-app advertising required some modifications to the layout, since the banners
need some extra space. Unfortunately the layout that had been serving all the
different screen resolution did not work anymore, so an additional layout had
to be designed for landscape. Banners also need to be clickable. On a touch
device it is easy just to tap the banner, but non-touch devices also require
focus handling, which allows the user to select the banner and then click it.

-------------------------------------------------------------------------------

KNOWN ISSUES

- When mist, fog, or black clouds are forecast, the visual style always follows
  the day mode.

- On some devices, network access is set to "ask always" by default. On Nokia 
  Asha software platform, the setting can be changed from Settings > Installed 
  apps > WeatherApp > Permissions. On Series 40 devices, scroll down to the 
  MIDlet in question and selecting files > my apps Options > Application access.

-------------------------------------------------------------------------------

BUILD AND INSTALLATION INSTRUCTIONS

The example has been created with NetBeans 6.9.1 and Nokia SDK 2.0 for Java.
The project can be easily opened in NetBeans by selecting 'Open Project' 
from the File menu and selecting the application. 

Before opening the project, make sure the Nokia SDK 2.0 for Java or newer is 
installed and added to NetBeans. Building is done by selecting 'Build main 
project'. Also make sure the JAR files inside the lib folder are included
into the project (properties -> Libraries & Resources -> Add Jar/Zip).

You can install the application on a phone by transfering the JAR file 
with Nokia Suite or over Bluetooth.

The example can also be built and run with Nokia IDE.

-------------------------------------------------------------------------------

RUNNING THE EXAMPLE

The application requires a network connection to work. When the
application starts, the user will in most cases be prompted to retrieve the
current location and to use network connection. If there are no means for 
positioning, the user will be redirected to recently used locations. On the
first run there are no recent locations, so a search view will be opened 
instead.

After the location-based or user-defined location has been specified, the 
application tries to retrieve the weather forecast for the next four days.
Days can be browsed left and right using the arrows or by flicking 
horizontally. Also navi keys can be used if the phone has those.

The user can change the location any time by opening the Location view and 
selecting any of the previously used locations or search for a new location.

The application also supports regional settings for temperature and wind speed.

A banner ad is shown for 60 seconds and then changed to a new one. A 
full-screen ad is shown when the user is about to exit the application.

-------------------------------------------------------------------------------

COMPATIBILITY

Nokia Asha software platform 1.0 and Series 40 3rd Edition phones and newer.

Note that the device needs to support the maximum Java heap size and a JAR file
size of at least 1 MB. Network connectivity is also required.

Tested on:
Nokia X3-02 (Series 40 6th Edition FP1)
Nokia Asha 200 (Java Runtime 1.1.0 for Series 40)
Nokia Asha 302 (Java Runtime 1.1.0 for Series 40)
Nokia Asha 303 (Java Runtime 1.1.0 for Series 40)
Nokia Asha 306 (Java Runtime 2.0.0 for Series 40)
Nokia Asha 311 (Java Runtime 2.0.0 for Series 40)
Nokia Asha 501 (Nokia Asha software platform 1.0)

Developed with:
Netbeans 7.3
Nokia Asha SDK 1.0
Inneractive Ad SDK v1.0.9 for J2ME

-------------------------------------------------------------------------------

CHANGE HISTORY

v1.5 NAX replaced by Inneractive
v1.4 Minor stability fixes.
v1.3 Example ported to Nokia Asha devices. Inneractive plugin updated and
     worldweatheronline links updated.
v1.2 Orientation support and optimisation done for Series 40 full touch devices, 
     updated Ad SDK, fixed Symbian related issues
v1.1 Added in-app advertisement, fixed Symbian related issues and some memory 
     issues
v1.0 The first version published at developer.nokia.com.

