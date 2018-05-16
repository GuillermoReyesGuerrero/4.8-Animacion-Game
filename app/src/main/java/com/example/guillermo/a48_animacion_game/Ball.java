package com.example.guillermo.a48_animacion_game;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by guillermo on 9/05/18.
 */

public class Ball {

    RectF rect;
    float xVelocity;
    float yVelocity;
    float ballWidth = 12;
    float ballHeight = 12;

    public Ball(int screenX, int screenY){

        // Inicia la pelota viajando derecho hacia arriba a 100 pixeles por segundo
        xVelocity = 200;
        yVelocity = -400;

        // Posiciona la pelota en el centro de la pantalla en el borde inferior
        // Lo hace un cuadro de 10 x 10 pixeles
        rect = new RectF();

    }

    public RectF getRect(){
        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }

}
