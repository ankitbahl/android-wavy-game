package com.example.ankit.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Ankit on 10/22/2016.
 */

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static String LOG_TAG = "GameSurface";
    private static int FPS = 80;
    private static float defaultCharacterXPosition = 300;
    private static float minCharacterPosition = 0;
    private static float maxObstaclePosition = 0;
    private static float minObstaclePosition = 0;
    private static float maxCharacterPosition;
    private static float defaultAcceleration = 3f;
    private Thread thread;
    private Bitmap background;
    private PhysicsEngine physicsEngine;
    private CubeGuy cubeGuy;
    private int screenWidth;
    private int characterHeight;
    private int characterWidth;
    private float characterHitboxWidth;
    private int screenHeight;
    private int obstacleHeight;
    private int obstacleWidth;
    private Random random = new Random();
    private boolean touchHeld = false;
    public static boolean running;
    private List<Sprite> spriteList;
    private Queue<Obstacle> obstacles;

    public GameSurface(Context context) {
        super(context);
        Activity fake = (Activity) context;
        Display thisDisplay = fake.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        thisDisplay.getSize(size);
        screenWidth = size.x;
        Resources res = getResources();
        characterHeight = (int) res.getDimension(R.dimen.character_height);
        characterHitboxWidth = res.getDimension(R.dimen.character_hitbox_width);
        characterWidth = (int) res.getDimension(R.dimen.character_width);
        obstacleHeight = (int) res.getDimension(R.dimen.obstacle_height);
        obstacleWidth = (int) res.getDimension(R.dimen.obstacle_width);
        obstacles = new ArrayBlockingQueue<>(10);
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

            if(checkCollisions()) {
                running = false;
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private float generateRandomPosition() {
        return (random.nextFloat() - minObstaclePosition)*maxObstaclePosition;
    }

    private boolean checkCollisions() {
        Obstacle obstacle = obstacles.element();
        if(obstacle.getXPosition() + obstacleWidth < cubeGuy.getXPosition()) {
            obstacles.remove();
            physicsEngine.removeObstacle();
            spriteList.remove(obstacle);
            obstacle = obstacles.element();
        }
        RectF rect1 = new RectF(cubeGuy.getXPosition(),cubeGuy.getYPosition(),cubeGuy.getXPosition() + characterHitboxWidth, cubeGuy.getYPosition() + characterHeight);
        RectF rect2 = new RectF(obstacle.getXPosition(),obstacle.getYPosition() ,obstacle.getXPosition() + obstacleWidth, obstacle.getYPosition()+ obstacleHeight);
        return RectF.intersects(rect1,rect2);
    }

    @Override
    public void run() {
        long startTime = SystemClock.currentThreadTimeMillis();
        long prevValue = 0;
        long forFps, prevTime = 0;
        cubeGuy = new CubeGuy(defaultCharacterXPosition, minCharacterPosition,defaultAcceleration,this);
        spriteList.add(cubeGuy);
        int counter = 0;
        Obstacle obstacle = new Obstacle(screenWidth,generateRandomPosition(),-10f,getResources(),R.drawable.osbtacles);
        physicsEngine = new PhysicsEngine(cubeGuy);
        physicsEngine.addObstacle(obstacle);
        obstacles.add(obstacle);
        spriteList.add(obstacle);
        int numtimes = FPS;
        while(running) {
            long currentTime = SystemClock.currentThreadTimeMillis() - startTime;
//            forFps = SystemClock.currentThreadTimeMillis() - prevTime;
            if(currentTime > 1000/FPS) {
                counter++;
                if(counter == numtimes) {
                    counter = 0;
                    obstacle = new Obstacle(screenWidth,generateRandomPosition(),-10f,getResources(),R.drawable.osbtacles);
                    physicsEngine.addObstacle(obstacle);
                    obstacles.add(obstacle);
                    spriteList.add(obstacle);
                }
//                prevTime = SystemClock.currentThreadTimeMillis();
                startTime = SystemClock.currentThreadTimeMillis();
                updateDraw();
                update();
            }
        }

        Log.d(LOG_TAG,"you lose");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        running = true;
        spriteList.clear();
        obstacles.clear();
        run();
    }
}
