package com.example.ankit.myapplication;

import android.graphics.Bitmap;

public abstract class Sprite {
    private static Bitmap CUBE_GUY_BMP;
    private static Bitmap OBSTACLE_BMP;
    private static boolean INITIALIZED = false;
    private float _xCoords;
    private float _yCoords;
    private float _xVelocity;
    private float _yVelocity;
    private float _xAcceleration;
    private float _yAcceleration;
    private SpriteType _spriteType;
    protected Sprite(float x, float y, float xVelocity, float yVelocity, float xAcceleration, float yAcceleration, SpriteType spriteType) {
        if(INITIALIZED) {
            _xCoords = x;
            _yCoords = y;
            _xVelocity = xVelocity;
            _yVelocity = yVelocity;
            _xAcceleration = xAcceleration;
            _yAcceleration = yAcceleration;
            _spriteType = spriteType;
        } else {
            throw new RuntimeException("Sprite bitmaps have not been initialized!");
        }
    }
    public static void setBitmaps(Bitmap cubeGuyBMP, Bitmap obstacleBMP) {
        CUBE_GUY_BMP = cubeGuyBMP;
        OBSTACLE_BMP = obstacleBMP;
        INITIALIZED = true;
    }
    public float getXPosition() {return _xCoords;}
    public float getYPosition() {return _yCoords;}
    public float getXVelocity() {return _xVelocity;}
    public float getYVelocity() {return _yVelocity;}
    public float getXAcceleration() {return _xAcceleration;}
    public float getYAcceleration() {return _yAcceleration;}
    public Bitmap getBitmap() {
        switch (this._spriteType) {
            case CUBE_GUY:
                return CUBE_GUY_BMP;
            case OBSTACLE:
                return OBSTACLE_BMP;
            default:
                return OBSTACLE_BMP;
        }
    }

    public void incrementXCoords(float x) {
        _xCoords += x;
    }
    public void incrementYCoords(float y) {
        _yCoords += y;
    }
    public void setXCoords(float x) {
        _xCoords = x;
    }
    public void setYCoords(float y) {
        _yCoords = y;
    }
    public void incrementXVelocity(float x) {
        _xVelocity += x;
    }
    public void incrementYVelocity(float y) {
        _yVelocity += y;
    }
    protected void setXAcceleration(float x) {
        _xAcceleration = x;
    }
    protected void setYAcceleration(float y) {
        _yAcceleration = y;
    }
    public void stopMoving() {
        _xAcceleration = 0;
        _yAcceleration = 0;
        _xVelocity = 0;
        _yVelocity = 0;
    }

    public enum SpriteType {
        CUBE_GUY,
        OBSTACLE
    }
}
