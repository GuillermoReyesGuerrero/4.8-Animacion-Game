package com.example.guillermo.a48_animacion_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class ActivityGame extends Activity {
    // gameView será la vista del juego
    // También contendrá la lógica del juego
    // y también respuesta a toques en la pantalla
    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);

        // Initialize gameView and set it as the view
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    // Aquí está nuestra implementación de GameView
    // es una clase interna.
    // Nota como la llave final }
    // está dentro de inside SimpleGameEngine

    // Nota que implementamos runnable para así tener
    // un hilo y poder anular el método run.
    class BreakoutView extends SurfaceView implements Runnable {

        // Este es nuestro hilo
        Thread gameThread = null;

        // Esto es nuevo. Necesitamos un SurfaceHolder
        // Cuando usamos Paint y Canvas en un hilo
        // Pronto lo veremos en acción en el método draw.
        SurfaceHolder ourHolder;

        // un boolean el cual estableceremos o no
        //cuando el juego esté corriendo- o no.
        volatile boolean playing;

        // El juego está pausado al inicio
        boolean paused = true;

        // Un objeto Canvas y uno Paint
        Canvas canvas;
        Paint paint;

        // Está variable monitorea los cuadros por segundo del juego
        long fps;

        // Esto es usado para ayudar a calcular los fps
        private long timeThisFrame;

        // El tamaño de la pantalla en pixeles
        int screenX;
        int screenY;

        // el paddle del jugador
        Paddle paddle;

        // Una ball
        Ball ball;

        // Hasta 200 bricks
        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        // Para efectos de sonido
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;
        int candyexplode = -1;
        int soun1 = -1;
        int soun2 = -1;
        int soun3 = -1;
        int soun4 = -1;
        int soun5 = -1;


        // el puntaje
        int score = 0;

        // vidas
        int lives = 3;

        // Cuando inicializamos (call new()) en GameView
        // Este método contructor especial corre
        public BreakoutView(Context context) {
            // La siguiente línea de código pide a la
            // clase SurfaceView que establezca nuestro objeto.
            // Que considerada.
            super(context);

            // Inicializa nuestros Objetos Holder y Paint
            ourHolder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            // Carga la resolución a un objeto Point
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            int mball=screenY-80;

            //Crea un paddle
            paddle = new Paddle(screenX, screenY);

            // Crea una ball
            ball = new Ball(screenX, screenY-30);

            // Cargar los sonidos
            // Este SoundPool es despreciado pero no te preocupes
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

            try{
                // Crea objetos de las 2 clases requeridas
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Carga nuestro efecto de sonido en la memoria listo para usarse
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("candy-crush-bomba-color.ogg");
                candyexplode = soundPool.load(descriptor,0);

                descriptor = assetManager.openFd("000923726_prev.ogg");
                soun1 = soundPool.load(descriptor,0);

                descriptor = assetManager.openFd("010564339_prev.ogg");
                soun2 = soundPool.load(descriptor,0);

                descriptor = assetManager.openFd("010593244_prev.ogg");
                soun3 = soundPool.load(descriptor,0);

                descriptor = assetManager.openFd("010609168_prev.ogg");
                soun4 = soundPool.load(descriptor,0);

                descriptor = assetManager.openFd("bites-ta-da-winner.ogg");
                soun5 = soundPool.load(descriptor,0);



            }catch(IOException e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

            //Iniciamos el metodo
            createBricksAndRestart();

        }
        public void reiniciar(){
            paused = true;
            // Pon la ball de vuelta en el inicio
            ball.reset(screenX, screenY-30);
            //Crea un paddle
            paddle = new Paddle(screenX-120, screenY);

        }

        public void createBricksAndRestart(){

            // Pon la ball de vuelta en el inicio
            ball.reset(screenX, screenY-30);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            // Construye una pared de bricks
            numBricks = 0;

            for(int column = 0; column < 8; column ++ ){
                for(int row = 0; row < 4; row ++ ){
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks ++;
                }
            }
            // Resetear scores y lives
            score = 0;
            lives = 3;

        }

        @Override
        public void run() {
            while (playing) {

                // Captura el tiempo actual en milisegundos en Capture startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Actualizar el cuadro
                // Actualizar el cuadro
                if(!paused){
                    update();
                }

                // Trazar el cuadro
                draw();

                // Calcular el fps este cuadro
                // Ahora podemos usar el resultado para
                // sincronizar animaciones y más.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Todo lo que necesita ser actualizado va aquí
        // Movimiento, detección de colisión, etc.
        public void update() {
            // Mueve el paddle si es necesario
            paddle.update(fps);
            // Chequea si una ball choca con un brick
            for(int i = 0; i < numBricks; i++) {

                if (bricks[i].getVisibility()) {

                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                        //soundPool.play(explodeID, 1, 1, 0, 0, 1);
                        soundPool.play(candyexplode,1,1,0,0,1);

                    }

                    // Chequea si la ball choca con el paddle
                    if(RectF.intersects(paddle.getRect(),ball.getRect())) {
                        ball.setRandomXVelocity();
                        ball.reverseYVelocity();
                        ball.clearObstacleY(paddle.getRect().top - 2);
                        soundPool.play(beep1ID, 1, 1, 0, 0, 1);
                    }

                    // Rebota la pelota cuando golpea el borde inferior de la pantalla
                    // Y resta una vida
                    if(ball.getRect().bottom > screenY){
                        ball.reverseYVelocity();
                        ball.clearObstacleY(screenY - 2);


                        // Pierde una vida
                        lives --;
                        soundPool.play(loseLifeID, 1, 1, 0, 0, 1);
                        reiniciar();


                        if(lives == 0){
                            paused = true;
                            //UnSegundo();
                            //createBricksAndRestart();
                            soundPool.play(soun4,1,1,0,0,1);
                            hilos();
                        }

                    }
                    // Rebota le pelota de vuelta cuando golpee el borde superior de la pantalla
                    if(ball.getRect().top < 0){
                        ball.reverseYVelocity();
                        ball.clearObstacleY(12);
                        soundPool.play(beep2ID, 1, 1, 0, 0, 1);
                    }
                    // Rebota si la ball golpea la pared izquierda
                    if(ball.getRect().left < 0){
                        ball.reverseXVelocity();
                        ball.clearObstacleX(2);
                        //soundPool.play(beep3ID, 1, 1, 0, 0, 1);
                    }
                    // Rebota si la ball golpea la pared derecha
                    if(ball.getRect().right > screenX - 10){
                        ball.reverseXVelocity();
                        ball.clearObstacleX(screenX - 22);
                        //soundPool.play(beep3ID, 1, 1, 0, 0, 1);
                    }
                    // Pausa si se despejó la pantalla
                    if(score == 320){
                        soundPool.play(soun5,1,1,0,0,1);
                        paused = true;
                        hilos();
                    }
                }
            }
            //Mueve el ball
            ball.update(fps);

        }
        // Traza la escena recién actualizada
        public void draw() {

            // Asegura nuestra superficie de trazado sea válida si no hace crash.
            if (ourHolder.getSurface().isValid()) {
                // Bloquea el lienzo listo para trazar
                canvas = ourHolder.lockCanvas();

                // Traza el color del fondo
                //canvas.drawColor(Color.argb(255,  26, 128, 182));
                canvas.drawColor(Color.WHITE);

                // Elige el color del pincel para trazar
                //paint.setColor(Color.argb(255,  255, 255, 255));
                paint.setColor(Color.BLACK);

                // Traza el paddle
                canvas.drawRect(paddle.getRect(), paint);

                paint.setColor(Color.BLUE);

                // Traza el ball
                canvas.drawRect(ball.getRect(), paint);

                // Traza los bricks
                // Cambia el color del pincel para trazar
                //paint.setColor(Color.argb(255,  249, 129, 0));
                paint.setColor(Color.RED);

                // traza los ladrillos si es visible
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                // Traza el HUD
                // Elige el color del pincel para dibujar
                //paint.setColor(Color.argb(255,  255, 255, 255));
                paint.setColor(Color.BLACK);

                // Traza el score
                paint.setTextSize(40);
                canvas.drawText("Puntos: " + score + "   Vidas: " + lives, 10,50, paint);

                // El jugador ha despejado la pantalla?
                if(score == numBricks * 10){
                    paint.setTextSize(90);
                    canvas.drawText("Ganaste!", 450,screenY/2, paint);
                }

                // el juador ha perdido?
                if(lives == 0){
                    paint.setTextSize(90);
                    canvas.drawText("Perdiste!", 450,screenY/2, paint);
                    paint.setTextSize(30);
                    canvas.drawText("reiniciando..", 450,(screenY/2)+100, paint);
                }
                //Traza todo en la pantalla
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }
        private void UnSegundo() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public void hilos() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UnSegundo();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    //createBricksAndRestart();
                }
            }).start();
        }

        // Si la Activity (Actividad) es pausada/detenida
        // cierra nuestro hilo.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // Si la Activity (Actividad) es iniciada entonces
        // Inicia nuestro hilo.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // La clase SurfaceView Implementa onTouchListener
        // Para poder anular este método y detectar toques en la pantalla.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Jugador a tocado la pantalla
                case MotionEvent.ACTION_DOWN:

                    paused = false;


                    if(motionEvent.getX() > screenX / 2){
                        paddle.setMovementState(paddle.RIGHT);
                    }
                    else{
                        paddle.setMovementState(paddle.LEFT);
                    }

                    break;

                // Jugado a quitado el dedo de la pantalla
                case MotionEvent.ACTION_UP:

                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

    }
    // Este es el final de nuestra clase interna BreakoutView

    // Este método se ejecuta cuando el jugador empieza el juego
    @Override
    protected void onResume() {
        super.onResume();

        // Le dice a el método de resumen de gameView que se ejecute
        breakoutView.resume();
    }

    // Este método se ejecuta cuando el jugador sale del juego
    @Override
    protected void onPause() {
        super.onPause();

        // Le dice método de pausa de gameView que se ejecute
        breakoutView.pause();
    }
}
