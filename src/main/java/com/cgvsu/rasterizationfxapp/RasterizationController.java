package com.cgvsu.rasterizationfxapp;

import com.cgvsu.rasterizationfxapp.figure.DrawLine;
import com.cgvsu.rasterizationfxapp.figure.Point;
import com.cgvsu.rasterizationfxapp.figure.Rasterization;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class RasterizationController {

    @FXML
    AnchorPane anchorPane;
    ArrayList<Point> points = new ArrayList<Point>();
    @FXML
    private Canvas canvas;

    public Canvas getCanvas() {
        return canvas;
    }

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        canvas.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY -> handlePrimaryClick(canvas.getGraphicsContext2D(), event);
            }
        });
        Rasterization.setCanvas(canvas);
        Rasterization.drawOval(canvas.getGraphicsContext2D(), 0, 0, 500, 200, Color.BLACK);
    }

    private void handlePrimaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        DrawLine draw = new DrawLine();
        draw.setCanvas(canvas);
        final Point clickPoint = new Point((int) event.getX(), (int) event.getY());
        if (!points.isEmpty()) {
            final Point lastPoint = points.get(points.size() - 1);
            draw.drawLine(graphicsContext, lastPoint, clickPoint, 1);
        }
        points.add(clickPoint);
    }


}