/*
 * Ball.java - Copyright (c) 2013 hijex.com (dusan.saiko@gmail.com)
 */

package com.hijex.javafx.balls;

import com.hijex.shared.Vector2D;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


class Card extends ImageView {

    private Vector2D motion;


    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public Card(BallsScreensaver parent, String texture) {
        super(texture);

        setFitWidth(parent.screenBounds.getWidth() / 10);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
        setOpacity(0.2);

        Bounds bounds = getLayoutBounds();

        minX = 2;
        minY = 2;
        maxX = parent.screenBounds.getWidth() - bounds.getWidth() - 2;
        maxY = parent.screenBounds.getHeight() - bounds.getHeight() - 2;

        setTranslateX(parent.rnd.nextInt((int)maxX));
        setTranslateY(parent.rnd.nextInt((int)maxY));

        //some random speed vector
        double speed = bounds.getWidth() / 100;
        switch(parent.rnd.nextInt(4)) {
            case 0:
                motion = new Vector2D(0,0, speed, speed);
                break;
            case 1:
                motion = new Vector2D(0,0, -speed, speed);
                break;
            case 2:
                motion = new Vector2D(0,0, -speed, -speed);
                break;
            default:
                motion = new Vector2D(0,0, speed, -speed);
                break;
        }

        Timeline animation = new Timeline();
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(35), e -> move()));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    void move() {
        double x = getTranslateX() + motion.dx;
        double y = getTranslateY() + motion.dy;

        //hit walls
        if(x < minX || x > maxX) {
            x = x < minX ? minX + 1 : maxX - 1;
            motion = motion.rotate( (x < 2 * minX ? -1 : 1) * 2 * Ball.WALL_VERTICAL.getAngle(motion));
        }
        if(y < minY || y > maxY) {
            y = y < minY ? minY + 1 : maxY - 1;
            motion = motion.rotate( (y < 2* minY ? 1 : -1) * 2 * Ball.WALL_HORIZONTAL.getAngle(motion));
        }

        setTranslateX(x);
        setTranslateY(y);
    }

}
