/*
 * Vector2D.java - Copyright (c) 2013 hijex.com (dusan.saiko@gmail.com)
 */

package com.hijex.shared;

import javafx.geometry.Point2D;


/**
 * @author dsaiko
 *         Mathematical vector representation
 *         <p/>
 *         x0, y0 - origin
 *         dx, dy - direction
 */
@SuppressWarnings({"InstanceVariableNamingConvention", "NonReproducibleMathCall", "OverlyComplexArithmeticExpression"})
public class Vector2D {

    private final double x0;
    private final double y0;
    public final double dx;
    public final double dy;

    public Vector2D(double x0, double y0, double dx, double dy) {
        this.x0 = x0;
        this.y0 = y0;
        this.dx = dx;
        this.dy = dy;
    }

    public Vector2D() {
        this(0, 0, 0, 0);
    }

    public Vector2D(double dx, double dy) {
        this(0, 0, dx, dy);
    }

    public Vector2D(Point2D a, Point2D b) {
        this(a.getX(), a.getY(), b.getX() - a.getX(), b.getY() - a.getY());
    }

    public Vector2D moveTo(double x1, double y1) {
        return new Vector2D(x1, y1, dx, dy);
    }

    public Vector2D moveTo(Point2D a) {
        return moveTo(a.getX(), a.getY());
    }

    public Vector2D multiply(double r) {
        return new Vector2D(x0, y0, dx * r, dy * r);
    }

    Vector2D divide(double r) {
        return multiply(1.0 / r);
    }

    public Vector2D getOpposite() {
        return multiply(-1.0);
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    public Vector2D add(Vector2D other) {
        return new Vector2D(x0, y0, dx + other.dx, dy + other.dy);
    }

    public double size() {
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public Vector2D getUnitVector() {
        return divide(size());
    }

    public double getAngle(Vector2D other) {
        double a = size();
        double b = other.size();
        double c = new Vector2D(0, 0, dx - other.dx, dy - other.dy).size();
        double cosC = (((a * a) + (b * b)) - (c * c)) / (2 * a * b);

        return Math.acos(cosC);
    }

    public Vector2D rotate(double alfa) {

        double r = size();

        double angle = getAngle(new Vector2D(0, 0, r, 0));
        if (dy < 0) angle = 2 * Math.PI - angle;

        //rotate
        angle += alfa;

        //get new coordinates
        return new Vector2D(x0, y0, r * Math.cos(angle), r * Math.sin(angle));
    }

    @SuppressWarnings({"ParameterNameDiffersFromOverriddenParameter", "RedundantIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2D)) return false;

        Vector2D vector2D = (Vector2D) o;

        if (Double.compare(vector2D.dx, dx) != 0) return false;
        if (Double.compare(vector2D.dy, dy) != 0) return false;
        if (Double.compare(vector2D.x0, x0) != 0) return false;
        if (Double.compare(vector2D.y0, y0) != 0) return false;

        return true;
    }

    public boolean intersects(Vector2D other) {
        return getIntersection(other) != null;
    }

    Point2D getIntersection(Vector2D other) {
        double x1 = x0;
        double y1 = y0;
        double x2 = x0 + dx;
        double y2 = y0 + dy;

        double x3 = other.x0;
        double y3 = other.y0;
        double x4 = other.x0 + other.dx;
        double y4 = other.y0 + other.dy;

        double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (d == 0) return null;

        double x = (((x3 - x4) * ((x1 * y2) - (y1 * x2))) - ((x1 - x2) * ((x3 * y4) - (y3 * x4)))) / d;
        double y = (((y3 - y4) * ((x1 * y2) - (y1 * x2))) - ((y1 - y2) * ((x3 * y4) - (y3 * x4)))) / d;

        //check boundaries
        if(Math.abs(dx) > Math.abs(dy)) {
            if(dx < 0) {
                if((x < (x0 + dx)) || (x > x0)) return null;
            } else {
                if((x < x0) || (x > (x0 + dx))) return null;
            }
        } else {
            if(dy < 0) {
                if((y < (y0 + dy)) || (y > y0)) return null;
            } else {
                if((y < y0) || (y > (y0 + dy))) return null;
            }
        }
        return new Point2D(x, y);
    }

    @SuppressWarnings("TooBroadScope")
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x0);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y0);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dx);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%.3f,%.3f](%.3f,%.3f)", x0, y0, dx, dy);
    }
}
