package com.example.szr.fingerheart;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

public class StartActivity extends AppCompatActivity {
    private Camera camera;

    private boolean isPreview=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        SurfaceView surfaceView=findViewById(R.id.surfaceView);
        final SurfaceHolder surfaceHolder=surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }
//        Button prview=findViewById(R.id.prview);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPreview){
                    camera= Camera.open();
                    isPreview=true;
                }
                try {


                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setDisplayOrientation(90);
                    Camera.Parameters parameters=camera.getParameters();;
                    parameters.setPictureFormat(PixelFormat.JPEG);
                    parameters.set("jpeg-quality",80);

                    camera.setParameters(parameters);
                    camera.startPreview();
                    camera.autoFocus(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(camera!=null){
            camera.stopPreview();
            camera.release();
        }
    }
}
