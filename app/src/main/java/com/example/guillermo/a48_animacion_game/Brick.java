package com.example.guillermo.a48_animacion_game;

import android.graphics.RectF;

/**
 * Created by guillermo on 9/05/18.
 */

public class Brick {

    private RectF rect;

    private boolean isVisible;

    public Brick(int row, int column, int width, int height){

        isVisible = true;

        int padding = 3;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

}
