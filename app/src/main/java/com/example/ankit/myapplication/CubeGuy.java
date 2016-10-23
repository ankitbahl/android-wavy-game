package com.example.ankit.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class CubeGuy extends Sprite{

    public CubeGuy(float x, float y, float yAcceleration, View view) {
        super(x,y,0,0,0,yAcceleration,view.getResources(),R.drawable.cube);
    }

    public Bitmap getCubeImage() {return getBitmap();}
    public void changeYAcceleration(float val) {
        setYAcceleration(val);
    }
}
