package com.example.guillermo.a48_animacion_game;

import android.graphics.RectF;

/**
 * Created by guillermo on 9/05/18.
 */

public class Paddle {

    // RectF es un objeto que contiene cuatro coordenadas - justo lo que necesitamos
    private RectF rect;

    // Cuán larga y alta será nuesto paddle
    private float length;
    private float height;

    // X es el extremo izquierdo del rectángulo que forma nuestro paddle
    private float x;

    // Y es la coordenada superior
    private float y;

    // Esto contendrá los pixeles por segundo en los que se moverá el paddle
    private float paddleSpeed;

    // En que direcciones se puede mover el paddle
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // El paddle se está moviendo y en que dirección
    private int paddleMoving = STOPPED;

    // Este es el método constructor
    // Cuando creemos un objeto de esta clase introduciremos
    // el ancho y alto de la pantalla
    public Paddle(int screenX, int screenY){
        // 130 pixeles de ancho y 20 pixeles de alto
        length = 130;
        height = 20;

        // Inicia al paddle cerca del centro de la pantalla
        x = screenX / 2;
        y = screenY - 20;

        rect = new RectF(x, y, x + length, y + height);

        // Cuán rápido es el paddle en piexeles por segundo
        paddleSpeed = 350;
    }

    // Este es un método getter para hacer para hacer el rectángulo que
    // define nuestro paddle disponible en la clase BreakoutView
    public RectF getRect(){
        return rect;
    }

    // Este método se usará para cambiar/establecer si el paddle va hacia la izquierda, derecha o no se mueve
    public void setMovementState(int state){
        paddleMoving = state;
    }

    // Este método update será llamado desde update en BreakoutView
    // Determina si el paddle necesita moverse y cambiar las coordenadas
    // contenido en rect si es necesario
    public void update(long fps){
        if(paddleMoving == LEFT){
            x = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

}
