/*
 * BallsScreensaver.java - Copyright (c) 2013 hijex.com (dusan.saiko@gmail.com)
 */

package com.hijex.javafx.balls;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BallsScreensaver extends Application {

    final List<Ball> balls = new ArrayList<>(16);
    private final List<Card> cards = new ArrayList<>(16);

    private double     ballRadius;
    Rectangle2D screenBounds;

    final Random rnd = new Random();



    @Override public void start(Stage stage) {

        stage.setTitle("Billiard Balls Screensaver");

        Screen screen = Screen.getPrimary();
        screenBounds = screen.getBounds();

        //standard pool desk size is 224 x 112 cm, standard pool ball diameter is 57.15 mm
        ballRadius = 57.15 * screenBounds.getWidth() / 2240;

        for(int i=1; i<=16; i++) {
            String id = String.valueOf(i);
            if(id.length() < 2) id = "0"+id;
            balls.add(new Ball(this, ballRadius, "ball_"+id+".png"));
        }

        cards.add(new Card(this, "cards-hearts-king.png"));
        cards.add(new Card(this, "cards-hearts-queen.png"));

        randomizeBalls();

        //set up light
        PointLight pointLight = new PointLight(Color.ANTIQUEWHITE);
        pointLight.setTranslateX(- screenBounds.getWidth() );
        pointLight.setTranslateY(- screenBounds.getWidth() );
        pointLight.setTranslateZ( - screenBounds.getWidth() * 10);


        ImageView backgroundImage = new ImageView("pool-table.jpg");
        backgroundImage.setFitHeight(screenBounds.getHeight());
        backgroundImage.setFitWidth(screenBounds.getWidth());


        //place balls
        Group root = new Group(backgroundImage);
        root.getChildren().addAll(cards.toArray(new Card[cards.size()]));
        root.getChildren().addAll(balls.toArray(new Ball[balls.size()]));
        root.getChildren().add(pointLight);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

          //show
        stage.show();
    }

    private void randomizeBalls() {

        for(int i=0; i<balls.size(); i++) {
            Ball ball = balls.get(i);

            boolean intersects = true;

            while(intersects) {

                double x = rnd.nextInt((int)(screenBounds.getWidth() - ballRadius * 2) ) + ballRadius;
                double y = rnd.nextInt((int)(screenBounds.getHeight() - ballRadius * 2)) + ballRadius;
                ball.setTranslateX(x);
                ball.setTranslateY(y);

                intersects = false;

                for(int n = 0; n<i; n++) {
                    if(Ball.intersects(ball.getTranslateX(), ball.getTranslateY(), balls.get(n))) {
                        intersects = true;
                        break;
                    }
                }
            }

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
