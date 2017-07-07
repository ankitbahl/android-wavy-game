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
    Sprite(float x, float y, float xVelocity, float yVelocity, float xAcceleration, float yAcceleration, SpriteType spriteType) {
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
    static void setBitmaps(Bitmap cubeGuyBMP, Bitmap obstacleBMP) {
        CUBE_GUY_BMP = cubeGuyBMP;
        OBSTACLE_BMP = obstacleBMP;
        INITIALIZED = true;
    }
    float getXPosition() {return _xCoords;}
    float getYPosition() {return _yCoords;}
    float getXVelocity() {return _xVelocity;}
    float getYVelocity() {return _yVelocity;}
    float getXAcceleration() {return _xAcceleration;}
    float getYAcceleration() {return _yAcceleration;}
    Bitmap getBitmap() {
        switch (this._spriteType) {
            case CUBE_GUY:
                return CUBE_GUY_BMP;
            case OBSTACLE:
                return OBSTACLE_BMP;
            default:
                return OBSTACLE_BMP;
        }
    }

    void incrementXCoords(float x) {
        _xCoords += x;
    }
    void incrementYCoords(float y) {
        _yCoords += y;
    }
    public void setXCoords(float x) {
        _xCoords = x;
    }
    void setYCoords(float y) {
        _yCoords = y;
    }
    void incrementXVelocity(float x) {
        _xVelocity += x;
    }
    void incrementYVelocity(float y) {
        _yVelocity += y;
    }
    protected void setXAcceleration(float x) {
        _xAcceleration = x;
    }
    void setYAcceleration(float y) {
        _yAcceleration = y;
    }
    void stopMoving() {
        _xAcceleration = 0;
        _yAcceleration = 0;
        _xVelocity = 0;
        _yVelocity = 0;
    }

    enum SpriteType {
        CUBE_GUY,
        OBSTACLE
    }
}
