package com.cgvsu.rasterizationfxapp.figure;

import javafx.beans.NamedArg;

public class Point {
    private int x;
    private int y;

    public Point(@NamedArg("x") int x, @NamedArg("y") int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double distance(double x, double y) {
        double distanceForX = this.getX() - x;
        double distanceForY = this.getY() - y;
        return Math.sqrt(distanceForX * distanceForX + distanceForY * distanceForY);
    }
}
