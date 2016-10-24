package com.example.ankit.myapplication;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhysicsEngine implements Runnable {
    private CubeGuy _cubeGuy;
    private static float cubeGuyXSize;
    private static float cubeGuyYSize;
    private static float obstacleXSize;
    private static float obstacleYsize;

    private static final int updateTimeMS = 30;
    private List<Obstacle> _obstacles;
    public PhysicsEngine(CubeGuy cubeGuy,Resources res) {
        _cubeGuy = cubeGuy;
        _obstacles = new ArrayList<>();
        cubeGuyYSize = res.getDimension(R.dimen.character_height);
        cubeGuyXSize = res.getDimension(R.dimen.character_hitbox_width);
        obstacleYsize = res.getDimension(R.dimen.obstacle_height);
        obstacleYsize = res.getDimension(R.dimen.obstacle_width);
        Thread thread = new Thread(this);
        thread.start();
    }
    public void addObstacles(Obstacle... sprites) {
        _obstacles.addAll(Arrays.asList(sprites));
    }
    public boolean removeObstacle(Obstacle obstacle) {
        return _obstacles.remove(obstacle);
    }
    public void update() {
        updateSprite(_cubeGuy);
        for(Obstacle obstacle: _obstacles) {
            updateSprite(obstacle);
            if(isCollision(_cubeGuy,obstacle)) {
                GameSurface.running = false;
            }
        }
    }

    private void updateSprite(Sprite sprite) {
        sprite.incrementXVelocity(sprite.getXAcceleration());
        sprite.incrementYVelocity(sprite.getYAcceleration());
        sprite.incrementXCoords(sprite.getXVelocity());
        sprite.incrementYCoords(sprite.getYVelocity());
    }

    //TODO
    private boolean isCollision(CubeGuy cubeGuy, Obstacle obstacle) {
        //front end collision
        float cubeGuyXLeft = cubeGuy.getXPosition();
        float cubeGuyXRight = cubeGuyXLeft + cubeGuyXSize;
        float cubeGuyYUp = cubeGuy.getYPosition();
        float cubeGuyYDown = cubeGuyYUp + cubeGuyYSize;
        float obstacleXPosition = obstacle.getXPosition();
        float obstacleYPosition = obstacle.getXPosition();
        return false;
    }

    @Override
    public void run() {
        while(true) {
            update();
            try {
                Thread.sleep(updateTimeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
