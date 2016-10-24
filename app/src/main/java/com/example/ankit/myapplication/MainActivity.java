package com.example.ankit.myapplication;

import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GameSurface game = new GameSurface(this);
        setContentView(game);

//        TestCanvasDrawer test = new TestCanvasDrawer(this);
//        setContentView(test);
    }
}
