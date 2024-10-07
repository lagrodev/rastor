package com.cgvsu.rasterizationfxapp.figure;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Rasterization {
    private static Canvas canvas;

    public static void drawRectangle(
            final GraphicsContext graphicsContext,
            final int x, final int y,
            final int width, final int height,
            final Color color) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        for (int row = y; row < y + height; ++row) {
            for (int col = x; col < x + width; ++col) {
                pixelWriter.setColor(col, row, color);
            }
        }
    }

    private static void fillScanline(
            PixelWriter pixelWriter,
            int x,
            int y,
            Point center,
            Color centerColor,
            Color edgeColor,
            double focusA,
            double focusB) {
        int startX = (int) (center.getX() - x);
        int endX = (int) (center.getX() + x);
        int y1 = (int) (center.getY() + y);
        int y2 = (int) (center.getY() - y);

        for (int xi = startX; xi <= endX; xi++) {
            interpolateAndSetColor(pixelWriter, xi, y1, center, centerColor, edgeColor, focusA, focusB);
            interpolateAndSetColor(pixelWriter, xi, y2, center, centerColor, edgeColor, focusA, focusB);
        }
    }

    private static void interpolateAndSetColor(
            PixelWriter pixelWriter,
            int x,
            int y,
            Point center,
            Color centerColor,
            Color edgeColor,
            double focusA,
            double focusB) {
        // Calculate normalized distance from the center
        double dx = x - center.getX();
        double dy = y - center.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double maxDistance = Math.sqrt(focusA * focusA + focusB * focusB);

        // Clamp t between 0 and 1
        double t = Math.min(distance / maxDistance, 1.0);

        // Manually interpolate color components
        double red = (1 - t) * centerColor.getRed() + t * edgeColor.getRed();
        double green = (1 - t) * centerColor.getGreen() + t * edgeColor.getGreen();
        double blue = (1 - t) * centerColor.getBlue() + t * edgeColor.getBlue();
        double opacity = (1 - t) * centerColor.getOpacity() + t * edgeColor.getOpacity();

        // Ensure components are within valid range [0, 1]
        red = Math.max(0, Math.min(red, 1));
        green = Math.max(0, Math.min(green, 1));
        blue = Math.max(0, Math.min(blue, 1));
        opacity = Math.max(0, Math.min(opacity, 1));

        // Create the new color
        Color color = new Color(red, green, blue, opacity);

        // Set the pixel color
        pixelWriter.setColor(x, y, color);
    }

    public static void drawOval(
            GraphicsContext graphicsContext,
            int x, int y,
            final int width, final int height,
            final Color centerColor,
            final Color edgeColor) {

        WritableImage writableImage = new WritableImage(
                (int) canvas.getWidth(),
                (int) canvas.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        int focusA = width / 2;
        int focusB = height / 2;

        // Use your own Point class if you have one
        Point center = new Point(x + focusA, y + focusB);

        int xi = 0;
        int yi = focusB;
        double a2 = focusA * focusA;
        double b2 = focusB * focusB;

        // Decision parameters
        double d1 = b2 - a2 * focusB + 0.25 * a2;
        double dx = 2 * b2 * xi;
        double dy = 2 * a2 * yi;

        // Region 1
        while (dx < dy) {
            fillScanline(pixelWriter, xi, yi, center, centerColor, edgeColor, focusA, focusB);
            if (d1 < 0) {
                xi++;
                dx += 2 * b2;
                d1 += dx + b2;
            } else {
                xi++;
                yi--;
                dx += 2 * b2;
                dy -= 2 * a2;
                d1 += dx - dy + b2;
            }
        }

        // Region 2
        double d2 = b2 * (xi + 0.5) * (xi + 0.5) + a2 * (yi - 1) * (yi - 1) - a2 * b2;
        while (yi >= 0) {
            fillScanline(pixelWriter, xi, yi, center, centerColor, edgeColor, focusA, focusB);
            if (d2 > 0) {
                yi--;
                dy -= 2 * a2;
                d2 += a2 - dy;
            } else {
                xi++;
                yi--;
                dx += 2 * b2;
                dy -= 2 * a2;
                d2 += dx - dy + a2;
            }
        }

        graphicsContext.drawImage(writableImage, 0, 0);
    }

    public static void setCanvas(Canvas canvas) {
        Rasterization.canvas = canvas;
    }
}
