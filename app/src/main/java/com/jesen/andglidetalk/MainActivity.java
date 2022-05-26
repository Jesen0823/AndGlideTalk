package com.jesen.andglidetalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.go_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("com.memory_leak.test.second");
                intent.addCategory("com.memory_leak.test");
                startActivity(intent);
            }
        });

        findViewById(R.id.go_third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("com.memory_leak.test.third");
                intent.addCategory("com.memory_leak.test");
                startActivity(intent);
            }
        });
    }
}