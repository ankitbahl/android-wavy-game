package com.example.ankit.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Ankit on 10/22/2016.
 */

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static String LOG_TAG = "GameSurface";
    private static int FPS = 60;
    private Thread thread;
    int bottomOfScreenChar;
    int screenWidth;
    int characterHeight;
    int characterWidth;
    int screenHeight;
    boolean direction = true;
    int position = 0;
    int topOfScreenChar = 0;
    private boolean running;
    private Rect rect;
    private Paint myPaint;
    public GameSurface(Context context) {
        super(context);
        Activity fake = (Activity) context;
        Display thisDisplay = fake.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        thisDisplay.getSize(size);
        screenWidth = size.x;
        characterHeight = (int) getResources().getDimension(R.dimen.character_height);
        characterWidth = (int) getResources().getDimension(R.dimen.character_width);
        screenHeight = size.y;
        bottomOfScreenChar = (int)(screenHeight-1.4*characterHeight);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        rect = new Rect(0,0,100,100);
        myPaint = new Paint();
        myPaint.setARGB(0,255,0,0);
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG_TAG,"Surface changed??");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG,"Surface destroyed??");
        destroyThread();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            direction = !direction;
        }
        return super.onTouchEvent(event);
    }

    private void update() {
        if(direction) {
            position+=40;
        } else {
            position-=40;
        }

        if(position > screenWidth-characterWidth) {
            direction = false;
        }

        if(position < 0) {
            direction = true;
        }
    }


    private void destroyThread() {
        //Stop thread's loop
        running = false;

        //Try to join thread with UI thread
        boolean retry = true;
        while (retry)
        {
            try {thread.join(); retry = false;}
            catch (InterruptedException e) {}
        }
    }

    private void updateDraw(Bitmap background, Bitmap cube) {
        Canvas canvas;
        SurfaceHolder ourHolder = getHolder();
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            if(canvas == null) {
                Log.d(LOG_TAG,"rip");
                return;
            }
            canvas.drawBitmap(background,0,0,null);
            canvas.drawBitmap(cube,position ,50,null);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public void run() {
        long startTime = SystemClock.currentThreadTimeMillis();
        long prevValue = 0;
        Bitmap background = BitmapFactory.decodeResource(getResources(),R.drawable.pink_rice);
        Bitmap cube = BitmapFactory.decodeResource(getResources(),R.drawable.cube);
        while(true) {
            long currentTime = SystemClock.currentThreadTimeMillis() - startTime;
            if(prevValue != currentTime && currentTime > 1000/FPS) {
                startTime = SystemClock.currentThreadTimeMillis();
                updateDraw(background,cube);
                update();
            }
        }
    }
}
