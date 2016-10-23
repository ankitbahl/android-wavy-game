package com.example.ankit.myapplication;

import android.content.res.Resources;

public class Obstacle extends Sprite {
    public Obstacle(float x, float y, float xVelocity, Resources res, int imageId) {
        super(x, y, xVelocity, 0, 0, 0, res, imageId);
    }
}
