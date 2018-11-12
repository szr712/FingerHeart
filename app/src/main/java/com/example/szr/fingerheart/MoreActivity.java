package com.example.szr.fingerheart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText inputEditText;
    private Button mbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        inputEditText = findViewById(R.id.et_input);
        mbtn = findViewById(R.id.btn_test);

        mbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_test) {
            Toast.makeText(this, "输出结果为1", Toast.LENGTH_SHORT).show();
        }
    }
}
