package com.cgvsu.rasterizationfxapp.figure;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class DrawLine {

    Canvas canvas;

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawLine(GraphicsContext graphicsContext, Point point1, Point point2, int operation) {
        if (operation >= 1) {
            drawWuLine(graphicsContext, point1, point2);
        } else if (operation == 0) {
            drawBresenhamLine(graphicsContext, point1, point2);
        } else {
            drawDDALine(graphicsContext, point1, point2);
        }
    }


    private void drawWuLine(GraphicsContext graphicsContext, Point point1, Point point2) {
        int x0 = point1.getX();
        int y0 = point1.getY();
        int x1 = point2.getX();
        int y1 = point2.getY();

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

        if (steep) {
            int temp = x0;
            x0 = y0;
            y0 = temp;
            temp = x1;
            x1 = y1;
            y1 = temp;
        }
        if (x0 > x1) {
            int temp = x0;
            x0 = x1;
            x1 = temp;
            temp = y0;
            y0 = y1;
            y1 = temp;
        }
        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dx == 0 ? 1 : dy / dx;
        // первая точка
        double xEnd = round(x0);
        double yEnd = y0 + gradient * (xEnd - x0);
        double xGap = rfpart(x0 + 0.5);
        int xPixel1 = (int) xEnd;
        int yPixel1 = (int) Math.floor(yEnd);

        if (steep) {
            plot(pixelWriter, yPixel1, xPixel1, rfpart(yEnd) * xGap);
            plot(pixelWriter, yPixel1 + 1, xPixel1, rfpart(yEnd) * xGap);
        } else {
            plot(pixelWriter, xPixel1, yPixel1, rfpart(yEnd) * xGap);
            plot(pixelWriter, xPixel1, yPixel1 + 1, rfpart(yEnd) * xGap);
        }

        double intery = yEnd + gradient;
        // 2 - последняя точка
        xEnd = round(x1);
        yEnd = y1 + gradient * (xEnd - x1);
        xGap = fpart(x1 + 0.5);
        int xPixel2 = (int) xEnd;
        int yPixel2 = (int) Math.floor(yEnd);
        if (steep) {
            plot(pixelWriter, yPixel2, xPixel2, rfpart(yEnd) * xGap);
            plot(pixelWriter, yPixel2 + 1, xPixel2, rfpart(yEnd) * xGap);
        } else {
            plot(pixelWriter, xPixel2, yPixel2, rfpart(yEnd) * xGap);
            plot(pixelWriter, xPixel2, yPixel2 + 1, rfpart(yEnd) * xGap);
        }

        // сама линия
        if (steep) {
            for (int x = xPixel1 + 1; x < xPixel2; x++) {
                plot(pixelWriter, (int) Math.floor(intery), x, rfpart(intery));
                plot(pixelWriter, (int) Math.floor(intery) + 1, x, fpart(intery));
                intery += gradient;
            }
        } else {
            for (int x = xPixel1 + 1; x < xPixel2; x++) {
                plot(pixelWriter, x, (int) Math.floor(intery), rfpart(intery));
                plot(pixelWriter, x, (int) Math.floor(intery) + 1, fpart(intery));
                intery += gradient;
            }
        }
        graphicsContext.drawImage(writableImage, 0, 0);

    }

    private void plot(PixelWriter pixelWriter, int x, int y, double brightness) {
        if (x >= 0 && y >= 0 && x < canvas.getWidth() && y < canvas.getHeight()) {
            // Ограничиваем яркость в диапазоне от 0 до 1
            brightness = Math.max(0, Math.min(1, brightness));
            Color color = new Color(0, 0, 0, brightness);
            pixelWriter.setColor(x, y, color);
        }
    }

    private double fpart(double x) {
        return x - Math.floor(x);
    }

    private double rfpart(double x) {
        return 1 - fpart(x);
    }

    private double round(double x) {
        return Math.floor(x + 0.5);
    }

    private void drawBresenhamLine(GraphicsContext graphicsContext, Point point1, Point point2) {
        int x0 = point1.getX();
        int y0 = point1.getY();
        int x1 = point2.getX();
        int y1 = point2.getY();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        while (true) {
            pixelWriter.setColor(x0, y0, Color.BLACK);

            if (x0 == x1 && y0 == y1)
                break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }

        graphicsContext.drawImage(writableImage, 0, 0);
    }


    // фу нахуй, говноо сссаное DDA
    private void drawDDALine(GraphicsContext graphicsContext, Point point1, Point point2) {

        double dx = point2.getX() - point1.getX();
        double dy = point2.getY() - point1.getY();
        final double step;
        if (Math.abs(dx) > Math.abs(dy)) {
            step = Math.abs(dx);
        } else {
            step = Math.abs(dy);
        }
        dy /= step;
        dx /= step;
        double x = point1.getX();
        double y = point1.getY();
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < step; i++) {


            pixelWriter.setColor((int) x, (int) y, Color.BLACK);

            x = x + dx;
            y = y + dy;
        }
        graphicsContext.drawImage(writableImage, 0, 0);
    }


}
