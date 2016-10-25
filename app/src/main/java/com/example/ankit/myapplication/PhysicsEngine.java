package com.example.ankit.myapplication;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static com.example.ankit.myapplication.GameSurface.running;

public class PhysicsEngine implements Runnable {

    private static final int updateTimeMS = 25;
    private Queue<Obstacle> _obstacles;
    private CubeGuy _cubeGuy;
    public PhysicsEngine(CubeGuy cubeGuy) {
        _obstacles = new ArrayBlockingQueue<>(10);
        _cubeGuy = cubeGuy;
        Thread thread = new Thread(this);
        thread.start();
    }
    public void addObstacle(Obstacle sprite) {
        _obstacles.add(sprite);
    }
    public boolean removeObstacle() {
        return _obstacles.remove() != null;
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
        while(running) {
            update();
            try {
                Thread.sleep(updateTimeMS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _obstacles.clear();
    }
}
