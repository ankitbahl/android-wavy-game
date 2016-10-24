package com.example.ankit.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCanvasDrawer extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static String LOG_TAG = "GameSurface";
    private static int FPS = 80;
    private static float defaultCharacterXPosition = 300;
    private static float minCharacterPosition = 0;
    private static float maxObstaclePosition = 0;
    private static float minObstaclePosition = 0;
    private static float maxCharacterPosition;
    private static float defaultAcceleration = 3.5f;
    private Thread thread;
    private Bitmap background;
    private PhysicsEngine physicsEngine;
    private CubeGuy cubeGuy;
    private int screenWidth;
    private int characterHeight;
    private int characterWidth;
    private int screenHeight;
    private int obstacleHeight;
    private int obstacleWidth;
    private Random random = new Random();
    private boolean touchHeld = false;
    private boolean running;
    private List<Sprite> spriteList;

    public TestCanvasDrawer(Context context) {
        super(context);
        Activity fake = (Activity) context;
        Display thisDisplay = fake.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        thisDisplay.getSize(size);
        screenWidth = size.x;
        Resources res = getResources();
        characterHeight = (int) res.getDimension(R.dimen.character_height);
        characterWidth = (int) res.getDimension(R.dimen.character_hitbox_width);
        obstacleHeight = (int) res.getDimension(R.dimen.obstacle_height);
        obstacleWidth = (int) res.getDimension(R.dimen.obstacle_width);
        screenHeight = size.y;
        maxCharacterPosition = screenHeight-(float)(characterHeight*1.5);
        maxObstaclePosition = screenHeight-(float)(obstacleHeight*1.3);
        background = BitmapFactory.decodeResource(getResources(),R.drawable.pink_rice);
        spriteList = new ArrayList<>();
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

        if(cubeGuy.getYPosition() > maxCharacterPosition) {
            cubeGuy.stopMoving();
            cubeGuy.setYCoords(maxCharacterPosition);
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

    private void updateDraw() {
        Canvas canvas;
        SurfaceHolder ourHolder = getHolder();
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            if(canvas == null) {
                Log.d(LOG_TAG,"No canvas");
                return;
            }
            canvas.drawBitmap(background,0,0,null);
            for(Sprite sprite : spriteList) {
                canvas.drawBitmap(sprite.getBitmap(), sprite.getXPosition(), sprite.getYPosition(), null);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private float generateRandomPosition() {
        return (random.nextFloat() - minObstaclePosition)*maxObstaclePosition;
    }

    @Override
    public void run() {
        CubeGuy cg = new CubeGuy(50,50 + obstacleHeight,0,this);
        Obstacle o  = new Obstacle(50,50 ,0,getResources(),R.drawable.osbtacles);
        spriteList.add(cg);
        spriteList.add(o);
        updateDraw();
    }

}
