package com.example.ankit.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ankit on 10/22/2016.
 */

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static String LOG_TAG = "GameSurface";

    private static final int FPS = 80;
    private static final float defaultCharacterXPosition = 300;
    private static final float minCharacterPosition = 0;
    private static final float minObstaclePosition = 0;
    private static final float defaultObstacleSpeed = -10f;
    private static final float defaultAcceleration = 2.5f;
    private static final int objectsPerSecond = 2;
    private static boolean gameOver = false;

    private static float maxCharacterPosition;
    private static float maxObstaclePosition;
    private static float screenWidth;
    private static float characterWidth;
    private static float characterHitboxWidth;
    private static float characterHeight;
    private static float obstacleWidth;
    private static float obstacleHeight;

    private Thread thread;
    private Bitmap background;
    private PhysicsEngine physicsEngine;
    private CubeGuy cubeGuy;

    private Random random = new Random();
    public static boolean running;
    private List<Sprite> drawList;


    /**
     * Surface for drawing the entire game, constructor mainly sets constants to be used in rest of app
     * @param context
     */
    public GameSurface(Context context) {
        super(context);
        background = BitmapFactory.decodeResource(getResources(),R.drawable.pink_rice);
        drawList = new ArrayList<>();
        generateDimensionalConstants(context);
        setBitmaps(getResources());
        getHolder().addCallback(this);
   }

    /**
     * generates dimensional constants such as size of various sprites and screens, assigns to global
     * @param context
     */
    private static void generateDimensionalConstants(Context context) {
        Activity activity = (Activity) context;
        Display thisDisplay = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        thisDisplay.getSize(size);
        Resources res = context.getResources();
        characterHeight = res.getDimension(R.dimen.character_height);
        characterHitboxWidth = res.getDimension(R.dimen.character_hitbox_width);
        characterWidth = res.getDimension(R.dimen.character_width);
        obstacleHeight = res.getDimension(R.dimen.obstacle_height);
        obstacleWidth =  res.getDimension(R.dimen.obstacle_width);
        screenWidth = size.x;
        float screenHeight = size.y;
        maxCharacterPosition = screenHeight -(float)(characterHeight*1.5);
        maxObstaclePosition = screenHeight -(float)(obstacleHeight*1.3);
    }

    /**
     * sets the bitmaps for the sprites, stored within the Sprite class
     * @param res
     */
    private static void setBitmaps(Resources res) {
        Bitmap cubeGuy = BitmapFactory.decodeResource(res,R.drawable.cube);
        Bitmap obstacle = BitmapFactory.decodeResource(res,R.drawable.osbtacles);
        Sprite.setBitmaps(cubeGuy,obstacle);
    }

    /**
     * callback called when game surface is created, used to initialize thread
     * @param holder
     */
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

    /**
     * callback called when game surface is destroyed, used for cleanup
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG,"Surface destroyed??");
        destroyThread();
    }

    /**
     * callback called when screen is touched with touch even passed in as param
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            cubeGuy.setYAcceleration(-defaultAcceleration);
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            cubeGuy.setYAcceleration(defaultAcceleration);
        } else  {
            return super.onTouchEvent(event);
        }
        return true;
    }

    /**
     * called often to keep cubeGuy within the bounds of the screen
     */
    private void updateCubeGuyState() {
        if(cubeGuy.getYPosition() > maxCharacterPosition) {
            cubeGuy.stopMoving();
            cubeGuy.setYCoords(maxCharacterPosition);
        }

        if(cubeGuy.getYPosition() < 0) {
            cubeGuy.stopMoving();
            cubeGuy.setYCoords(0);
        }
    }

    /**
     * cleanup for thread
     */
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

    /**
     * redraw based on the state of the sprites
     */
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
            for(Sprite sprite : drawList) {
                canvas.drawBitmap(sprite.getBitmap(), sprite.getXPosition(), sprite.getYPosition(), null);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * clears all drawings on screen
     */
    private void clearCanvas() {
        Canvas canvas;
        SurfaceHolder ourHolder = getHolder();
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            if (canvas == null) {
                Log.d(LOG_TAG, "No canvas");
                return;
            }
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }


    private float generateRandomPosition() {
        return (random.nextFloat() - minObstaclePosition)*maxObstaclePosition;
    }

    /**
     * generates an obstacle in a random position
     * @return
     */
    private Obstacle generateRandomObstacle() {
        Obstacle obstacle = new Obstacle(screenWidth,generateRandomPosition(),defaultObstacleSpeed);
        physicsEngine.getObstacleList().add(obstacle);
        drawList.add(obstacle);
        return obstacle;
    }

    /**
     * check if there are any collisions with the front most obstacle
     * @return
     */
    private boolean checkCollisions() {
        LinkedList<Obstacle> obstacles = physicsEngine.getObstacleList();
        Obstacle obstacle = physicsEngine.getObstacleList().element();
        if(obstacle.getXPosition() + obstacleWidth < 0) {
            obstacles.removeFirst();
            obstacle = obstacles.element();
        } else if(obstacle.getXPosition() + obstacleWidth < cubeGuy.getXPosition()) {
            obstacle = obstacles.get(1);
        }
        RectF rect1 = new RectF(cubeGuy.getXPosition(),cubeGuy.getYPosition(),cubeGuy.getXPosition() + characterHitboxWidth, cubeGuy.getYPosition() + characterHeight);
        RectF rect2 = new RectF(obstacle.getXPosition(),obstacle.getYPosition() ,obstacle.getXPosition() + obstacleWidth, obstacle.getYPosition()+ obstacleHeight);
        return RectF.intersects(rect1,rect2);
    }

    @Override
    /**
     * main ui thread being run
     */
    public void run() {
        long startTime = SystemClock.currentThreadTimeMillis();
//        long prevValue = 0;
//        long forFps, prevTime = 0;

        // create main character
        cubeGuy = new CubeGuy(defaultCharacterXPosition, minCharacterPosition,defaultAcceleration);

        // initialize engine for handling game physics
        physicsEngine = new PhysicsEngine(cubeGuy);

        // add main character to list of sprites to be drawn
        drawList.add(cubeGuy);
        int counter = 0;
        generateRandomObstacle();
        int numTimes = FPS/objectsPerSecond;
        while(running) {
            long currentTime = SystemClock.currentThreadTimeMillis() - startTime;
//            forFps = SystemClock.currentThreadTimeMillis() - prevTime;
            if(currentTime > 1000/FPS) {
                counter++;
                if(counter == numTimes) {
                    counter = 0;
                    generateRandomObstacle();
                }
//                prevTime = SystemClock.currentThreadTimeMillis();
                startTime = SystemClock.currentThreadTimeMillis();
                updateCubeGuyState();
                updateDraw();
                if(checkCollisions()) {
                    running = false;
                    gameOver = true;
                }
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawList.clear();
        physicsEngine.getObstacleList().clear();
        if(gameOver = true) {
            Log.d(LOG_TAG,"game over");
            gameOver = false;
            running = true;
            run();
        }
        clearCanvas();
    }
}
