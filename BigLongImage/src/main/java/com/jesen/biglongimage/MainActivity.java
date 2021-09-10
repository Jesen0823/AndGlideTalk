package com.jesen.biglongimage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jesen.biglongimage.view.BigView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private BigView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testView = findViewById(R.id.testView);

        try {
            InputStream inputStream = getAssets().open("bigpic4.png");
            testView.setImage(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}