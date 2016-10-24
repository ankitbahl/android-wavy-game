package com.example.ankit.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class Sprite {
    private float _xCoords;
    private float _yCoords;
    private float _xVelocity;
    private float _yVelocity;
    private float _xAcceleration;
    private float _yAcceleration;
    private Bitmap _bitmap;
    protected Sprite(float x, float y, float xVelocity, float yVelocity, float xAcceleration, float yAcceleration, Resources res, int imageId) {
        _xCoords = x;
        _yCoords = y;
        _xVelocity = xVelocity;
        _yVelocity = yVelocity;
        _xAcceleration = xAcceleration;
        _yAcceleration = yAcceleration;
        _bitmap = BitmapFactory.decodeResource(res,imageId);
    }
    public float getXPosition() {return _xCoords;}
    public float getYPosition() {return _yCoords;}
    public float getXVelocity() {return _xVelocity;}
    public float getYVelocity() {return _yVelocity;}
    public float getXAcceleration() {return _xAcceleration;}
    public float getYAcceleration() {return _yAcceleration;}
    public Bitmap getBitmap() {return _bitmap;}

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
}
