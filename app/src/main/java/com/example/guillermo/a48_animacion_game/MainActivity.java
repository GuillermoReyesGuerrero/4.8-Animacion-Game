package com.example.guillermo.a48_animacion_game;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity{

    private ImageView imageView,imageView2;
    private AnimationDrawable bricksAnimation,logoAnimation;
    MediaPlayer intro;
    Button play,info,close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.animation);
        if (imageView == null) throw new AssertionError();
        imageView2 = (ImageView) findViewById(R.id.animation2);
        if(imageView == null) throw new AssertionError();

        play = (Button) findViewById(R.id.btnPlay);
        info = (Button) findViewById(R.id.btnInfo);
        close = (Button) findViewById(R.id.btnSalir);

        //intro = MediaPlayer.create(MainActivity.this,R.raw.tono);
        startIntro();


        //imageView.setVisibility(View.INVISIBLE);
        imageView.setBackgroundResource(R.drawable.ic_bruicks);

        //imageView2.setVisibility(View.INVISIBLE);
        imageView2.setBackgroundResource(R.drawable.ic_nombre);

        bricksAnimation = (AnimationDrawable) imageView.getBackground();
        bricksAnimation.setOneShot(true);

        logoAnimation = (AnimationDrawable) imageView2.getBackground();
        logoAnimation.setOneShot(true);

        startAnimation();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityGame.class);
                startActivity(intent);
                intro.stop();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
                intro.stop();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intro.start();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Integrantes: \n\nGuillermo Guadalupe Reyes Guerrero\nOscar Alberto Cordero Villa\n\n\nArkanoid version: 2.0  ")
                        .setTitle("Acerca de...")
                        .setCancelable(false)
                        .setNeutralButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        intro.start();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    protected void startAnimation() {
        if (bricksAnimation != null && !bricksAnimation.isRunning()) {
            bricksAnimation.start();
            logoAnimation.start();
        }
        bricksAnimation.start();
        logoAnimation.start();
    }

    private void startIntro(){
        if(intro == null){
            intro = MediaPlayer.create(this, R.raw.loop);
        }
        if(!intro.isPlaying()){
            intro.setLooping(true);
            intro.start();
        }
    }

}
