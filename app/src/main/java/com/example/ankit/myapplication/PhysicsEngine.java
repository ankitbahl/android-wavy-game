package com.example.ankit.myapplication;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhysicsEngine implements Runnable {
    private CubeGuy _cubeGuy;
    private int updateTimeMS = 30;
    private List<Obstacle> _obstacles;
    public PhysicsEngine(CubeGuy cubeGuy) {
        _cubeGuy = cubeGuy;
        _obstacles = new ArrayList<>();
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
        for(Sprite sprite: _obstacles) {
            updateSprite(sprite);
        }
    }

    private void updateSprite(Sprite sprite) {
        sprite.incrementXVelocity(sprite.getXAcceleration());
        sprite.incrementYVelocity(sprite.getYAcceleration());
        sprite.incrementXCoords(sprite.getXVelocity());
        sprite.incrementYCoords(sprite.getYVelocity());
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
