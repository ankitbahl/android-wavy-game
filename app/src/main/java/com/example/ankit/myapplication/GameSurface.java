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
    private static int FPS = 80;
    private static float defaultXPosition = 300;
    private static float minYPosition = 0;
    private static float maxYPosition;
    private static float defaultAcceleration = 3.5f;
    private Thread thread;
    private Bitmap background;
    private PhysicsEngine physicsEngine;
    private CubeGuy cubeGuy;
    private int screenWidth;
    private int characterHeight;
    private int characterWidth;
    private int screenHeight;
    private boolean touchHeld = false;
    private boolean running;

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
        maxYPosition = screenHeight-(float)(characterHeight*1.5);
        background = BitmapFactory.decodeResource(getResources(),R.drawable.pink_rice);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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
            touchHeld = true;
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            touchHeld = false;
        } else  {
            return super.onTouchEvent(event);
        }
        return true;
    }

    private void update() {
        if(touchHeld) {
            cubeGuy.setYAcceleration(-defaultAcceleration);
        } else {
            cubeGuy.setYAcceleration(defaultAcceleration);
        }

        if(cubeGuy.getYPosition() > maxYPosition) {
            cubeGuy.stopMoving();
            cubeGuy.setYCoords(maxYPosition);
        }

        if(cubeGuy.getYPosition() < 0) {
            cubeGuy.stopMoving();
            cubeGuy.setYCoords(0);
        }
    }


    private void destroyThread() {
        Log.d(LOG_TAG,"rip thread");
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

    private void updateDraw(Sprite... sprites) {
        Canvas canvas;
        SurfaceHolder ourHolder = getHolder();
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            if(canvas == null) {
                Log.d(LOG_TAG,"No canvas");
                return;
            }
            canvas.drawBitmap(background,0,0,null);
            for(Sprite sprite : sprites) {
                canvas.drawBitmap(sprite.getBitmap(), sprite.getXPosition(), sprite.getYPosition(), null);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public void run() {
        long startTime = SystemClock.currentThreadTimeMillis();
        long prevValue = 0;
        long forFps, prevTime = 0;
        cubeGuy = new CubeGuy(defaultXPosition,minYPosition,defaultAcceleration,this);
        physicsEngine = new PhysicsEngine(cubeGuy);
        while(running) {
            long currentTime = SystemClock.currentThreadTimeMillis() - startTime;
//            forFps = SystemClock.currentThreadTimeMillis() - prevTime;
            if(currentTime > 1000/FPS) {
//                prevTime = SystemClock.currentThreadTimeMillis();
                startTime = SystemClock.currentThreadTimeMillis();
                updateDraw(cubeGuy);
                update();
            }
        }
    }
}
