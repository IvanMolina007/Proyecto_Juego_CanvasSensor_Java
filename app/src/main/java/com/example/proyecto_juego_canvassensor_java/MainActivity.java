package com.example.proyecto_juego_canvassensor_java;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Resources resources;
    Bitmap bola, fondo, canastaUp, canastaDown;
    Point pint = new Point();
    Sensor acel;
    SensorManager mAcel;
    Movimiento cMov;
    int posiBallX, posiBallY, alturaZ, ancho, alto, total = 0;
    float velocidadX, velocidadY;
    CanvasView canvasViieew;
    Rect pantalla;
    boolean arriba = true, fuera = false;
    MediaPlayer mainSongMP;

    Paint cincel = new Paint();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDisplay().getSize(pint);
        mainSongMP = MediaPlayer.create(this, R.raw.lakers);
        cincel.setTextSize(100);

        posiBallX = pint.x/2-150;
        posiBallY = pint.y/2-150;
        alturaZ = 200;
        ancho = pint.x;
        alto = pint.y;
        pantalla = new Rect(0, 0, ancho, alto);

        resources = getResources();
        canvasViieew = new CanvasView(this);
        setContentView(canvasViieew);
        mAcel = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores = mAcel.getSensorList(Sensor.TYPE_ACCELEROMETER);
        acel = listaSensores.get(0);
        cMov = new Movimiento();
    }


    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mAcel.registerListener(cMov, acel, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAcel.unregisterListener(cMov);
        mainSongMP.pause();
    }

    public class CanvasView extends View {

        public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }

        public CanvasView(Context context) {
            super(context);
            bola = BitmapFactory.decodeResource(resources, R.drawable.bola);

            fondo = BitmapFactory.decodeResource(resources,R.drawable.pista);

            canastaUp = BitmapFactory.decodeResource(resources, R.drawable.canasta_arriba);

            canastaDown = BitmapFactory.decodeResource(resources, R.drawable.canasta_abajo);

            bola = getResizedBitmap(bola, alturaZ, alturaZ);

            canastaUp = getResizedBitmap(canastaUp, ancho/2 - canastaUp.getWidth()/2, alto/5);

            canastaDown = getResizedBitmap(canastaDown, ancho/2 - canastaDown.getWidth()/2, alto/5);

        }
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(fondo, null, pantalla, null);

            if (arriba) {

                canvas.drawBitmap(canastaUp, ancho/7, 0, null );

                canvas.drawRect(0, alto-alto/14, ancho, alto, cincel);

                if (total == 0) {
                    cincel.setColor(Color.YELLOW);
                } else {

                    if (total < 0) {
                        cincel.setColor(Color.RED);
                    } else {
                        cincel.setColor(Color.GREEN);
                    }

                }

                canvas.drawText("Balance : " + String.valueOf(total), ancho/4, alto - 50, cincel);

                cincel.setColor(Color.BLACK);

            } else {

                canvas.drawBitmap(canastaDown, ancho/7, alto-canastaDown.getHeight(), null );

                canvas.drawRect(0, 0, ancho, alto/14, cincel);

                if (total == 0) {

                    cincel.setColor(Color.YELLOW);

                } else {

                    if (total < 0) {
                        cincel.setColor(Color.RED);
                    } else {
                        cincel.setColor(Color.GREEN);
                    }


                }

                canvas.drawText("Balance : " + String.valueOf(total), ancho/4, 100, cincel);

                cincel.setColor(Color.BLACK);

            }

            //Pruebaaaaa Canasta-----------------------------------------------------

            // Canasta Arriba  canvas.drawCircle(ancho/2, alto/7, 150, cincel);

            //canvas.drawCircle(ancho/2, alto-alto/7, 150, cincel);

            //--------------------------------------------------------

            canvas.drawBitmap(bola, posiBallX, posiBallY, null);

            if (fuera) {

                arriba = !arriba;

                total--;

                fuera = false;

            }

        }

        public void reDraw() {
            invalidate();
        }

    }

    public class Movimiento implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                //Z ---------------------------------------------------------------
                alturaZ = (int) (alturaZ + event.values[2] * 10);

                if (alturaZ > 200) {
                    alturaZ = 200;
                    bola = canvasViieew.getResizedBitmap(bola, alturaZ, alturaZ);
                } else {
                    if (alturaZ < 100) {
                        alturaZ = 100;
                        bola = canvasViieew.getResizedBitmap(bola, alturaZ, alturaZ);
                    }
                }



                //X ------------------------------------------------------
                velocidadX = (float) (velocidadX - 0.6);

                velocidadX = velocidadX - event.values[0];

                posiBallX = posiBallX + (int) (velocidadX * 0.5);

                if (posiBallX + bola.getWidth() > pint.x) {

                    posiBallX = pint.x - bola.getWidth();

                    velocidadX = 0;

                } else {

                    if (posiBallX < 0) {

                        posiBallX = 0;

                        velocidadX = 0;

                    }
                }


                //Y --------------------------------------------------------
                velocidadY = velocidadY + event.values[1];

                posiBallY = posiBallY + (int) (velocidadY * 0.5);

                if (arriba) {
                    if (posiBallY + bola.getHeight() > alto-alto/14) {

                        posiBallY = (alto- alto/14) - bola.getHeight();
                        velocidadY = -velocidadY;

                    } else {

                        if (posiBallY + bola.getHeight()  < 0) {

                            posiBallY = 0;
                            velocidadY = 0;
                            fuera = true;

                        }
                    }
                } else {
                    if (posiBallY < alto/14) {

                        posiBallY = (alto/14);
                        velocidadY = -velocidadY;

                    } else {

                        if (posiBallY  > alto) {

                            posiBallY = alto - bola.getHeight();
                            velocidadY = 0;
                            fuera = true;

                        }
                    }
                }



                //Canasta ------------------------------------------------

                if (posiBallX > ancho/2-150 &&  posiBallX < ancho/2+150 && posiBallY > alto/7-150 && posiBallY < alto/7+150 && alturaZ < 200 && arriba) {
                    mainSongMP.start();
                    arriba = false;
                    total++;
                } else {
                    if (posiBallX > ancho/2-150 &&  posiBallX < ancho/2+150 && posiBallY > (alto - alto/7)-150 && posiBallY < (alto - alto/7)+150 && alturaZ < 200 && !arriba) {
                        mainSongMP.start();
                        arriba = true;
                        total++;
                    }

                }


                canvasViieew.reDraw();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

}