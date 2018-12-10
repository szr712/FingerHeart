package com.example.szr.fingerheart;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.example.szr.fingerheart.OverlayView.DrawCallback;
import com.example.szr.fingerheart.env.Logger;
import com.example.szr.fingerheart.env.BorderedText;

public class StartActivity extends CameraActivity implements ImageReader.OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    private Integer sensorOrientation;
    private TensorFlowImageClassifier classifier;

    private BorderedText borderedText;

    private Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                startAnimation();
            }
            return false;
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    private static final float TEXT_SIZE_DIP = 10;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        classifier = new TensorFlowImageClassifier(this);

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        final Display display = getWindowManager().getDefaultDisplay();
        final int screenOrientation = display.getRotation();

        LOGGER.i("Sensor orientation: %d, Screen orientation: %d", rotation, screenOrientation);

        sensorOrientation = rotation + screenOrientation;

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);

        yuvBytes = new byte[3][];

        addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        renderDebug(canvas);
                    }
                });
    }

    @Override
    protected void processImageRGBbytes(int[] rgbBytes) {
        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        final long startTime = SystemClock.uptimeMillis();
                        Classifier.Recognition r = classifier.classifyImage(rgbFrameBitmap, sensorOrientation);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        final List<Classifier.Recognition> results = new ArrayList<>();

                        if (r.getConfidence() > 0.7) {
                            results.add(r);
                        }

                        LOGGER.i("Detect: %s", results);
                        if (resultsView == null) {
                            resultsView = findViewById(R.id.results);
                        }
                        resultsView.setResults(results);
                        requestRender();
                        if (r.getTitle().equals("Heart")) {
                            uiHandler.sendEmptyMessage(1);
                        }
                        computing = false;
                        if (postInferenceCallback != null) {
                            postInferenceCallback.run();
                        }
                    }
                });

    }

    @Override
    public void onSetDebug(boolean debug) {
    }

    private void renderDebug(final Canvas canvas) {
        if (!isDebug()) {
            return;
        }

        final Vector<String> lines = new Vector<String>();
        lines.add("Inference time: " + lastProcessingTimeMs + "ms");
        borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
    }

    private void startAnimation() {
        final ImageView imgHeart = new ImageView(this);
        imgHeart.setImageResource(R.drawable.heart);
        imgHeart.setScaleType(ImageView.ScaleType.FIT_XY);
        float randomScale = 0.1f + (float) (Math.random()*(0.2f));
        float randomY = -300f - (float) (Math.random()*(200f));
        float randomX = (float) Math.pow(-1, (int) (Math.random()*(2))) * (float) (Math.random()*(500f));
        RelativeLayout rootView = findViewById(R.id.root);
        rootView.addView(imgHeart, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        ObjectAnimator.ofFloat(imgHeart, "scaleY", 0.0f, randomScale).setDuration(3000).start();
        ObjectAnimator.ofFloat(imgHeart, "scaleX", 0.0f, randomScale).setDuration(3000).start();
        ObjectAnimator.ofFloat(imgHeart, "alpha", 0.0f, 1.0f).setDuration(3000).start();
        ObjectAnimator.ofFloat(imgHeart, "translationY", 0f, randomY).setDuration(3000).start();
        ObjectAnimator.ofFloat(imgHeart, "translationX", 0f, randomX).setDuration(3000).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgHeart.setVisibility(View.GONE);
            }
        }, 3000);
    }
}
