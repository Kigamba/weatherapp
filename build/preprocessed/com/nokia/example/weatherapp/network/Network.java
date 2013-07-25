/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Provides asynchronous HTTP GET and POST operations. 
 */
public class Network
        implements Runnable {

    private NetworkListener listener;
    private String url = null;
    private String request = null;
    private String contentType = null;
    private static boolean allowed = false;
    private static boolean prompted = false;

    public Network(NetworkListener listener) {
        this.listener = listener;
    }

    /**
     * Sends a GET request synchronously.
     * @param url Target of the request.
     * @return Data received
     * @throws NetworkError
     */
    private String sendHttpGet(String url) throws NetworkError {
        HttpConnection hcon = null;
        DataInputStream dis = null;
        ByteArrayOutputStream response = new ByteArrayOutputStream();

        try {
            // A standard HttpConnection with READ access
            hcon = (HttpConnection) Connector.open(urlEncode(url));

            if (hcon == null) {
                throw new NetworkError("No network access");
            }

            // Obtain a DataInputStream from the HttpConnection
            dis = new DataInputStream(hcon.openInputStream());

            // Retrieve the response from the server
            int ch;
            while ((ch = dis.read()) != -1) {
                response.write((byte) ch);
            }
        }
        catch (Exception e) {
            if (e instanceof SecurityException) {
                prompted = true;
                allowed = false;
            }
            throw new NetworkError(e.getMessage());
        }
        finally {
            try {
                if (hcon != null) {
                    hcon.close();
                }
                if (dis != null) {
                    dis.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return response.toString();
    }

    /**
     * Sends a POST request synchronously.
     * @param url Target for the request.
     * @param request Request body
     * @throws NetworkError
     */
    private String sendHttpPost(String url, String request) throws NetworkError {
        HttpConnection hcon = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        StringBuffer responseMessage = new StringBuffer();

        try {
            // An HttpConnection with both read and write access
            hcon = (HttpConnection) Connector.open(urlEncode(url), Connector.READ_WRITE);
            if (hcon == null) {
                throw new NetworkError("No network access");
            }

            // Set the request method to POST
            hcon.setRequestMethod(HttpConnection.POST);
            // Content-Type is must to pass parameters in POST Request
            hcon.setRequestProperty("Content-Type", contentType);

            // Obtain DataOutputStream for sending the request string
            dos = hcon.openDataOutputStream();
            byte[] request_body = request.getBytes();

            // Send request string to server
            for (int i = 0; i < request_body.length; i++) {
                dos.writeByte(request_body[i]);
            }

            // Obtain DataInputStream for receiving server response
            dis = new DataInputStream(hcon.openInputStream());

            // Retrieve the response from server
            int ch;
            while ((ch = dis.read()) != -1) {
                responseMessage.append((char) ch);
            }
        }
        catch (Exception e) {
            if (e instanceof SecurityException) {
                prompted = true;
                allowed = false;
            }
            throw new NetworkError(e.getMessage());
        }
        finally {
            // Free up i/o streams and http connection
            try {
                if (hcon != null) {
                    hcon.close();
                }
                if (dis != null) {
                    dis.close();
                }
                if (dos != null) {
                    dos.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return responseMessage.toString();
    }

    /**
     * Thread entry point. Network operation is run in a separate thread.
     */
    public void run() {
        try {
            if (!isAllowed()) {
                throw new NetworkError("Network connection not allowed");
            }
            if (request == null) {
                String response = sendHttpGet(url);
                listener.networkHttpGetResponse(response);
            }
            else {
                String response = sendHttpPost(url, request);
                listener.networkHttpPostResponse(response);
            }
        }
        catch (NetworkError e) {
            if (request == null) {
                listener.networkHttpGetResponse(null);
            }
            else {
                listener.networkHttpPostResponse(null);
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * Sends a HTTP POST request asynchronously, returns immediately and
     * reports later using the listener.
     * @param url Target url
     * @param request Body
     * @param contentType Content type
     */
    void startHttpPost(String url, String request, String contentType) {
        this.url = url;
        this.request = request;
        this.contentType = contentType;
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Sends a HTTP POST request asynchronously, returns immediately and
     * reports later using the listener.
     * @param url Target url
     * @param request Body
     * @param contentType Content type
     */
    void startHttpPost(String url, String request) {
        startHttpPost(url, request, "application/x-www-form-urlencoded");
    }

    /**
     * Sends a HTTP POST request asynchronously, returns immediately and
     * reports later using the listener.
     * @param url Target url
     */
    void startHttpPost(String url) {
        startHttpPost(url, "");
    }

    /**
     * Sends a HTTP GET request asynchronously, returns immediately and
     * reports later using the listener.
     * @param url Target url
     */
    public void startHttpGet(String url) {
        this.url = url;
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Simple url encoder
     * @param url
     * @return Encoded string
     */
    public static String urlEncode(String url) {
        StringBuffer encoded = new StringBuffer();
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch == '<') {
                encoded.append("%3C");
            }
            else if (ch == '>') {
                encoded.append("%3E");
            }
            else if (ch == ' ') {
                encoded.append("%20");
            }
            else if (ch == '-') {
                encoded.append("%2D");
            }
            else {
                encoded.append(ch);
            }
        }
        return encoded.toString();
    }

    public static boolean isAllowed() {
        if (!prompted) {
            prompted = true;
            promptNetworkAccess();
        }
        return allowed;

    }

    /**
     * Opens a network connection just to trigger the network prompt
     */
    private static void promptNetworkAccess() {
        HttpConnection stimulus = null;
        try {
            stimulus = (HttpConnection) Connector.open("http://promptNetworkAccess.com");
        }
        catch (SecurityException se) {
            allowed = false;
            return;
        }
        catch (Exception e) {
            // Catch all the other exceptions
        }
        finally {
            try {
                if (stimulus != null) {
                    stimulus.close();
                }
            }
            catch (IOException ioe) {
            }
        }
        allowed = true;
    }
}
