package com.cgvsu.rasterizationfxapp.figure;

import javafx.beans.NamedArg;

// неизменяемы (в моем случае :)) данные
public record Point(int x, int y) {
    public Point(@NamedArg("x") int x, @NamedArg("y") int y) {
        this.x = x;
        this.y = y;
    }

    public double distance(double x, double y) {
        double distanceForX = this.x() - x;
        double distanceForY = this.y() - y;
        return Math.sqrt(distanceForX * distanceForX + distanceForY * distanceForY);
    }
}
