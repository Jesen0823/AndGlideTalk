package com.jesen.customglide;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jesen.customglide.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private static final String TEST_IMAGE_1 = "https://img1.baidu.com/it/u=1569546883,489283881&fm=26&fmt=auto&gp=0.jpg";

    private ImageView iv1,iv2,iv3;
    private Button testBt1,testBt2,testBt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        iv1 = findViewById(R.id.image1);
        iv2 = findViewById(R.id.image2);
        iv3 = findViewById(R.id.image3);
        testBt1 = findViewById(R.id.testBtn1);
        testBt2 = findViewById(R.id.testBtn2);
        testBt3 = findViewById(R.id.testBtn3);

        testBt1.setOnClickListener(view -> {
            Glide.with(this).load(TEST_IMAGE_1)
                    .into(iv1);
        });

        testBt2.setOnClickListener(view -> {
            Glide.with(this).load(TEST_IMAGE_1)
                    .into(iv2);
        });

        testBt3.setOnClickListener(view -> {
            Glide.with(this).load(TEST_IMAGE_1)
                    .into(iv3);
        });
    }

}