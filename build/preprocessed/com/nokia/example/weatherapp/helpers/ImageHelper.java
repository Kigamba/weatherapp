/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.weatherapp.helpers;

import javax.microedition.lcdui.Image;

/**
 * Provides helper methods for scaling images.
 */
public class ImageHelper {

    private ImageHelper() {
    }

    public static Image scaleImage(Image original, float factor) {
        return scaleImage(original, (int) (factor * original.getWidth() + 0.5),
                          (int) (factor * original.getHeight() + 0.5));
    }

    public static Image scaleImage(Image original, int newWidth, int newHeight) {
        Image scaledImage = original;
        int originalWidth = original.getWidth();
        if (originalWidth <= newWidth) {
            // Use pixel mixing for down scaling
            scaledImage = pixelMixing(original, newWidth, newHeight);
        }
        else if (originalWidth > newWidth) {
            // Use bilinear interpolation for up scaling
            scaledImage = bilinearInterpolation(original, newWidth, newHeight);
        }
        return scaledImage;
    }

    /**
     * Scales image by mixing pixels to the scaled image. Works best with down scaling.
     * @return Returns scaled image
     */
    private static Image pixelMixing(Image original,
                                     int newWidth, int newHeight) {
        int[] rawInput = new int[original.getHeight() * original.getWidth()];
        original.getRGB(rawInput, 0, original.getWidth(), 0, 0,
                        original.getWidth(), original.getHeight());

        int[] rawOutput = new int[newWidth * newHeight];

        int oWidth = original.getWidth();
        int[] oX16 = new int[newWidth + 1];
        for (int newX = 0; newX <= newWidth; newX++) {
            oX16[newX] = ((newX * oWidth) << 4) / newWidth;
        }

        int[] oXStartWidth = new int[newWidth];
        int[] oXEndWidth = new int[newWidth];
        for (int newX = 0; newX < newWidth; newX++) {
            oXStartWidth[newX] = 16 - (oX16[newX] % 16);
            oXEndWidth[newX] = oX16[newX + 1] % 16;
        }

        int oHeight = original.getHeight();
        int[] oY16 = new int[newHeight + 1];
        for (int newY = 0; newY <= newHeight; newY++) {
            oY16[newY] = ((newY * oHeight) << 4) / newHeight;
        }

        int oX16Start, oX16End, oY16Start, oY16End;
        int oYStartHeight, oYEndHeight;
        int oXStart, oXEnd, oYStart, oYEnd;
        int outArea, outColorArea, outAlpha, outRed, outGreen, outBlue;
        int areaHeight, areaWidth, area;
        int argb, a, r, g, b;
        for (int newY = 0; newY < newHeight; newY++) {
            oY16Start = oY16[newY];
            oY16End = oY16[newY + 1];
            oYStart = oY16Start >>> 4;
            oYEnd = oY16End >>> 4;
            oYStartHeight = 16 - (oY16Start % 16);
            oYEndHeight = oY16End % 16;
            for (int newX = 0; newX < newWidth; newX++) {
                oX16Start = oX16[newX];
                oX16End = oX16[newX + 1];
                oXStart = oX16Start >>> 4;
                oXEnd = oX16End >>> 4;
                outArea = 0;
                outColorArea = 0;
                outAlpha = 0;
                outRed = 0;
                outGreen = 0;
                outBlue = 0;
                for (int j = oYStart; j <= oYEnd; j++) {
                    areaHeight = 16;
                    if (oYStart == oYEnd) {
                        areaHeight = oY16End - oY16Start;
                    }
                    else if (j == oYStart) {
                        areaHeight = oYStartHeight;
                    }
                    else if (j == oYEnd) {
                        areaHeight = oYEndHeight;
                    }
                    if (areaHeight == 0) {
                        continue;
                    }
                    for (int i = oXStart; i <= oXEnd; i++) {
                        areaWidth = 16;
                        if (oXStart == oXEnd) {
                            areaWidth = oX16End - oX16Start;
                        }
                        else if (i == oXStart) {
                            areaWidth = oXStartWidth[newX];
                        }
                        else if (i == oXEnd) {
                            areaWidth = oXEndWidth[newX];
                        }
                        if (areaWidth == 0) {
                            continue;
                        }

                        area = areaWidth * areaHeight;
                        outArea += area;
                        argb = rawInput[i + j * original.getWidth()];
                        a = (argb >>> 24);
                        if (a == 0) {
                            continue;
                        }
                        area = a * area;
                        outColorArea += area;
                        r = (argb & 0x00ff0000) >>> 16;
                        g = (argb & 0x0000ff00) >>> 8;
                        b = argb & 0x000000ff;
                        outRed += area * r;
                        outGreen += area * g;
                        outBlue += area * b;
                    }
                }
                if (outColorArea > 0) {
                    outAlpha = outColorArea / outArea;
                    outRed = outRed / outColorArea;
                    outGreen = outGreen / outColorArea;
                    outBlue = outBlue / outColorArea;
                }
                rawOutput[newX + newY * newWidth] = (outAlpha << 24)
                        | (outRed << 16) | (outGreen << 8) | outBlue;
            }
        }
        return Image.createRGBImage(rawOutput, newWidth, newHeight, true);
    }

    /**
     * Scales image using bilinear interpolation. Best suited for up scaling.
     * @return Returns scaled image
     */
    private static Image bilinearInterpolation(Image original,
                                               int newWidth, int newHeight) {
        int[] rawInput = new int[original.getHeight() * original.getWidth()];
        original.getRGB(rawInput, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

        int[] rawOutput = new int[newWidth * newHeight];

        int oWidth = original.getWidth();
        int[] oX16 = new int[newWidth];
        int max = (oWidth - 1) << 4;
        for (int newX = 0; newX < newWidth; newX++) {
            oX16[newX] = ((((newX << 1) + 1) * oWidth) << 3) / newWidth - 8;
            if (oX16[newX] < 0) {
                oX16[newX] = 0;
            }
            else if (oX16[newX] > max) {
                oX16[newX] = max;
            }
        }

        int oHeight = original.getHeight();
        int[] oY16 = new int[newHeight];
        max = (oHeight - 1) << 4;
        for (int newY = 0; newY < newHeight; newY++) {
            oY16[newY] = ((((newY << 1) + 1) * oHeight) << 3) / newHeight - 8;
            if (oY16[newY] < 0) {
                oY16[newY] = 0;
            }
            else if (oY16[newY] > max) {
                oY16[newY] = max;
            }
        }

        int[] oX = new int[2];
        int[] oY = new int[2];
        int[] wX = new int[2];
        int[] wY = new int[2];
        int outWeight, outColorWeight, outAlpha, outRed, outGreen, outBlue;
        int w, argb, a, r, g, b;
        for (int newY = 0; newY < newHeight; newY++) {
            oY[0] = oY16[newY] >>> 4;
            wY[1] = oY16[newY] & 0x0000000f;
            wY[0] = 16 - wY[1];
            oY[1] = wY[1] == 0 ? oY[0] : oY[0] + 1;
            for (int newX = 0; newX < newWidth; newX++) {
                oX[0] = oX16[newX] >>> 4;
                wX[1] = oX16[newX] & 0x0000000f;
                wX[0] = 16 - wX[1];
                oX[1] = wX[1] == 0 ? oX[0] : oX[0] + 1;

                outWeight = 0;
                outColorWeight = 0;
                outAlpha = 0;
                outRed = 0;
                outGreen = 0;
                outBlue = 0;
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 2; i++) {
                        if (wY[j] == 0 || wX[i] == 0) {
                            continue;
                        }
                        w = wX[i] * wY[j];
                        outWeight += w;
                        argb = rawInput[oX[i] + oY[j] * original.getWidth()];
                        a = (argb >>> 24);
                        if (a == 0) {
                            continue;
                        }
                        w = a * w;
                        outColorWeight += w;
                        r = (argb & 0x00ff0000) >>> 16;
                        g = (argb & 0x0000ff00) >>> 8;
                        b = argb & 0x000000ff;
                        outRed += w * r;
                        outGreen += w * g;
                        outBlue += w * b;
                    }
                }
                if (outColorWeight > 0) {
                    outAlpha = outColorWeight / outWeight;
                    outRed = outRed / outColorWeight;
                    outGreen = outGreen / outColorWeight;
                    outBlue = outBlue / outColorWeight;
                }
                rawOutput[newX + newY * newWidth] = (outAlpha << 24)
                        | (outRed << 16) | (outGreen << 8) | outBlue;
            }
        }
        return Image.createRGBImage(rawOutput, newWidth, newHeight, true);
    }
}
