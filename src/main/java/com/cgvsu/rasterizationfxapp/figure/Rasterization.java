package com.cgvsu.rasterizationfxapp.figure;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Rasterization {

    static Canvas canvas;

    /*public static void drawRectangle(
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
    }*/

    private static void outlining(PixelWriter pixelWriter, int x, int y, Point center, Color color) {
        pixelWriter.setColor(x,  y, color);

    }

    public static void drawOval(
            GraphicsContext graphicsContext,
            int x, int y,
            final int width, final int height,
            final Color baseColor, Color secondColor) {

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int focusA = width / 2;
        int focusB = height / 2;

        Point center = new Point(x + focusA, y + focusB);

        int xMax = x + width;
        int yMax = y + height;

        // Максимальное расстояние от центра до границы эллипса
        double maxDistance = Math.sqrt(focusA * focusA + focusB * focusB);
        double epsilon = 0.01;

        for (int row = y; row <= yMax; row++) {
            for (int col = x; col <= xMax; col++) {

                double ovalX = col - center.x();
                double ovalY = row - center.y();
                double equation = (ovalX * ovalX) / (focusA * focusA) + (ovalY * ovalY) / (focusB * focusB); // уравнение эллипса

                if (equation - 1<= epsilon) { // рисуем - если подошло наше уравнение, кнш, при выполнении задания на п. выше, я бы воткнул epsilon
                    // чет по типу: double epsilon = 0.01; if (Math.abs(equation - 1)<= epsilon) ... хотяяяяяяяяяяя, я это щас даже воткну
                    // как бы why not?

                    // Вычисляем расстояние от текущей точки до центра
                    double distanceFromCenter = Math.sqrt(ovalX * ovalX + ovalY * ovalY);

                    // Нормализуем расстояние в диапазон [0, 1]
                    double normalizedDistance = distanceFromCenter / maxDistance;

                    // Интерполируем цвет на основе расстояния
                    Color interpolatedColor = interpolateColor(baseColor, secondColor, normalizedDistance);

                    outlining(pixelWriter, col, row, center, interpolatedColor);
                }
            }
        }

        graphicsContext.drawImage(writableImage, 0, 0);
    }

    // Функция для линейной интерполяции цвета
    private static Color interpolateColor(Color center, Color second, double factor) {
        double red = center.getRed() * (1 - factor) + second.getRed() * factor;
        double green = center.getGreen() * (1 - factor) + second.getGreen() * factor;
        double blue = center.getBlue() * (1 - factor) + second.getBlue() * factor;
        double opacity = center.getOpacity() * (1 - factor) + second.getOpacity() * factor;
        return new Color(red, green, blue, opacity);
    }

    public static void setCanvas(Canvas canvas) {
        Rasterization.canvas = canvas;
    }
}
