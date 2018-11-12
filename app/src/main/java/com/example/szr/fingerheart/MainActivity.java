package com.example.szr.fingerheart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.szr.fingerheart.R.id.bt_start;

public class MainActivity extends AppCompatActivity {

    private Button BtStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BtStart = findViewById(bt_start);
        BtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,StartActivity.class);
                startActivity(intent);
            }
        });

    }
}
