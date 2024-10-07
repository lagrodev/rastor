package com.cgvsu.rasterizationfxapp.figure;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Rasterization {

    static Canvas canvas;

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

    private static void outlining(PixelWriter pixelWriter, int x, int y, Point center, Color color) {
        pixelWriter.setColor(center.getX() + x, center.getY() + y, color);
        pixelWriter.setColor(center.getX() - x, center.getY() + y, color);
        pixelWriter.setColor(center.getX() + x, center.getY() - y, color);
        pixelWriter.setColor(center.getX() - x, center.getY() - y, color);
    }

    public static void drawOval(
            GraphicsContext graphicsContext,
            int x, int y,
            final int width, final int height,
            final Color color) {

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        int focusA = width / 2;
        int focusB = height / 2;
        Point center = new Point(x + focusA, y + focusB);
        x = 0;
        y = focusB;
        double d1 = (focusB * focusB) - (focusA * focusA * focusB) + (0.25 * focusA * focusA);
        while ((2 * focusB * focusB * x) <= (2 * focusA * focusA * y)) {
            outlining(pixelWriter, x, y, center, color);
            if (d1 < 0) {
                x++;
                d1 += (2 * focusB * focusB * x) + (focusB * focusB);
            } else {
                x++;
                y--;
                d1 += (2 * focusB * focusB * x) - (2 * focusA * focusA * y) + (focusB * focusB);
            }
        }
        double d2 = ((focusB * focusB) * ((x + 0.5) * (x + 0.5))) + ((focusA * focusA) * ((y - 1) * (y - 1))) - (focusA * focusA * focusB * focusB);

        while (y >= 0) {
            outlining(pixelWriter, x, y, center, color);
            if (d2 > 0) {
                y--;
                d2 -= (2 * focusA * focusA * y) + (focusA * focusA);
            } else {
                x++;
                y--;
                d2 += (2 * focusB * focusB * x) - (2 * focusA * focusA * y) + (focusA * focusA);
            }

        }
        graphicsContext.drawImage(writableImage, 0, 0);

    }

    public static void setCanvas(Canvas canvas) {
        Rasterization.canvas = canvas;
    }
}
