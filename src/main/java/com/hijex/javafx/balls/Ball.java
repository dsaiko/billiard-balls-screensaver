/*
 * Ball.java - Copyright (c) 2013 hijex.com (dusan.saiko@gmail.com)
 */

package com.hijex.javafx.balls;

import com.hijex.shared.Vector2D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.util.Random;


class Ball extends Sphere {

    private final BallsScreensaver parent;
    private final double radius;

    private Vector2D motion;

    static final Vector2D WALL_VERTICAL = new Vector2D(0,0,0,100);
    static final Vector2D WALL_HORIZONTAL = new Vector2D(0,0,100,0);

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    private Rotate rotate;

    private Vector2D rotationAxes;
    private Transform baseRotation = null;

    public Ball(BallsScreensaver parent, double radius, String texture) {
        this.parent = parent;
        this.radius = radius;

        minX = radius;
        maxX = parent.screenBounds.getWidth() - radius;

        minY = radius;
        maxY = parent.screenBounds.getHeight() - radius;

        setRadius(radius);
        setTranslateZ(radius * 2);
        setTranslateX(-1000);
        setTranslateY(-1000);

        PhongMaterial material = new PhongMaterial();
//        material.setDiffuseColor(Color.RED);
        //material.setSpecularColor(Color.LIGHTGRAY);

        Image diffuseMap = new Image(texture);
        material.setDiffuseMap(diffuseMap);

        setMaterial(material);
        setDrawMode(DrawMode.FILL);
        setCullFace(CullFace.BACK);

        //randomize rotation
        Random rnd = new Random();

        //some random speed vector
        motion = new Vector2D(0,0, 0, (rnd.nextDouble()*3.5 + 1) * radius / 25);

        //rotate randomly
        motion = motion.rotate(Math.toRadians(rnd.nextInt(360)));

        rotationAxes = new Vector2D( motion.dy, -motion.dx);
        rotate = new Rotate(rnd.nextInt(360), new Point3D(rotationAxes.dx, rotationAxes.dy, 0));
        getTransforms().add(rotate);

        Timeline animation = new Timeline();
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(35), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                move();
            }
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    public static boolean intersects(double x, double y, Ball that) {
        Vector2D vector = new Vector2D(new Point2D(x, y), new Point2D(that.getTranslateX(), that.getTranslateY()));
        double distance = vector.size();

        return distance <= 2 * that.radius;
    }

    void move() {
        double x = getTranslateX() + motion.dx;
        double y = getTranslateY() + motion.dy;

        //degrees to rotate
        double rotation = motion.size() * 360 / (Math.PI * 2 * radius);

       rotate.setAngle(rotate.getAngle() + rotation);

        //hit walls
        if(x < minX || x > maxX) {
            x = x < minX ? minX + 1 : maxX - 1;
            motion = motion.rotate( (x < 2 * minX ? -1 : 1) * 2 * WALL_VERTICAL.getAngle(motion));
            resetRotation();
        }
        if(y < minY || y > maxY) {
            y = y < minY ? minY + 1 : maxY - 1;
            motion = motion.rotate( (y < 2* minY ? 1 : -1) * 2 * WALL_HORIZONTAL.getAngle(motion));
            resetRotation();
        }

        if(checkHits(x, y) == false) {
            setTranslateX(x);
            setTranslateY(y);
        }
    }


    boolean checkHits(double x, double y) {
        boolean hit = false;
        for(Ball that : parent.balls) {
            if(that == this) continue;

            if(intersects(x, y, that)) {

                hit = true;
                Vector2D centerLine = new Vector2D(new Point2D(x, y), new Point2D(that.getTranslateX(), that.getTranslateY()));

                Vector2D[] hit1 = computeHit(centerLine);
                Vector2D[] hit2 = that.computeHit(centerLine.getOpposite());

                motion = hit1[0].add(hit2[1]);
                that.motion = hit1[1].add(hit2[0]);

                resetRotation();
            }
        }

        return hit;
    }

    Vector2D[] computeHit(Vector2D centerLine) {
        double c = motion.size();
        double beta = centerLine.getAngle(motion);
        if(Double.isNaN(beta)) beta = 0;
        double a = c * Math.cos(beta);
        double b = c * Math.sin(beta);

        Vector2D nextMotionThat = centerLine.getUnitVector().multiply(a).moveTo(0,0);
        Vector2D nextMotionThis = new Vector2D(centerLine.dy, -centerLine.dx).getUnitVector().multiply(b).moveTo(0, 0);
        Vector2D nextMotionThisOpposite = nextMotionThis.getOpposite();

        if(motion.getAngle(nextMotionThis) > motion.getAngle(nextMotionThisOpposite))
            nextMotionThis = nextMotionThisOpposite;

        return new Vector2D[] {nextMotionThis, nextMotionThat};
    }


    private void resetRotation() {
        if(baseRotation == null) {
            baseRotation = rotate.clone();
        } else {
            baseRotation = baseRotation.createConcatenation(rotate);
        }

        rotationAxes = new Vector2D( motion.dy, -motion.dx);
        rotate = new Rotate(0, new Point3D(rotationAxes.dx, rotationAxes.dy, 0));

        getTransforms().setAll(baseRotation, rotate);
    }
}
