package com.mozhimen.camerakuvctest;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.mozhimen.camerak.uvc.basic.CallBackEvents;
import com.mozhimen.camerak.uvc.basic.CameraApiType;
import com.mozhimen.camerak.uvc.basic.CameraFacing;
import com.mozhimen.camerak.uvc.basic.CameraManager;
import com.mozhimen.camerak.uvc.basic.CameraPreviewCallback;
import com.mozhimen.camerak.uvc.basic.CameraSize;
import com.mozhimen.camerak.uvc.basic.FacingType;
import com.mozhimen.camerak.uvc.basic.IAttributes;
import com.mozhimen.camerak.uvc.GLSurfaceViewListener;
import com.mozhimen.camerak.uvc.ScaleGLSurfaceView;

/**
 * @author: Zhu Yuliang
 * @created Create in 2020/6/23 4:23 PM.
 * @description: ScaleGLSurfaceActivity
 */
public class ScaleGLSurfaceActivity extends AppCompatActivity {

    public static final String TAG = "ScaleGLSurfaceActivity";

    private AppCompatSpinner spinner2;
    private ScaleGLSurfaceView scaleTextureView;
    private CameraManager mInstance;
    private SurfaceTexture surface;
    private TextView textView;
    private ImageView img_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scalesurface);
        scaleTextureView = findViewById(R.id.camera_textureview);
        textView = findViewById(R.id.txt_title);
        img_back = findViewById(R.id.img_back);
        textView.setText("ScaleGLSurface");
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scaleTextureView.setListener(new GLSurfaceViewListener() {
            @Override
            public void onCallBackSurfaceTexture(SurfaceTexture surfaceTexture) {
                surface = surfaceTexture;
                openCamera();
            }
        });
        scaleTextureView.setDisplayDir(ConstantsConfig.getInstance().getFaceOri());
        scaleTextureView.resetPreviewSize(ConstantsConfig.getInstance().getWidth(), ConstantsConfig.getInstance().getHeight());
        getLifecycle().addObserver(scaleTextureView);

        spinner2 = findViewById(R.id.spinner);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                scaleTextureView.setStyle(Utils.SelectScaleForCameraView(name));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        scaleTextureView.setSurfaceTextureListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void openCamera() {
        mInstance = CameraManager.getInstance(
                new CameraFacing.Builder().setFacingType(FacingType.OTHER)
                        .setCameraId(0).build(),
                CameraApiType.CAMERA1, getBaseContext());
        mInstance.setCallBackEvents(
                (new CallBackEvents() {
                    @Override
                    public void onCameraOpen(IAttributes cameraAttributes) {
                        Log.e(TAG, "onCameraOpen");
                        mInstance.setPhotoSize(new CameraSize(ConstantsConfig.getInstance().getWidth(), ConstantsConfig.getInstance().getHeight()));
                        mInstance.setPreviewSize(new CameraSize(ConstantsConfig.getInstance().getWidth(), ConstantsConfig.getInstance().getHeight()));
                        mInstance.setPreviewOrientation(ConstantsConfig.getInstance().getFaceOri().getValue() * 90);
                        mInstance.setExposureCompensation(0);
                        mInstance.addPreviewCallbackWithBuffer(new CameraPreviewCallback() {
                            @Override
                            public void onCallBackPreview(byte[] data) {
                                Log.e(TAG, "onCallBackPreview");
                            }
                        });
                        mInstance.startPreview(surface);
                    }

                    @Override
                    public void onCameraClose() {
                        Log.e(TAG, "onCameraClose");
                    }

                    @Override
                    public void onCameraError(String errorMsg) {
                        Log.e(TAG, "onCameraError");
                    }

                    @Override
                    public void onPreviewStarted() {
                        Log.e(TAG, "onPreviewStarted");
                    }

                    @Override
                    public void onPreviewStopped() {
                        Log.e(TAG, "onPreviewStopped");
                    }

                    @Override
                    public void onPreviewError(String errorMsg) {
                        Log.e(TAG, "onPreviewError");
                    }
                }));
        mInstance.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInstance != null) {
            this.mInstance.stopPreview();
            this.mInstance.release();
        }
        getLifecycle().removeObserver(scaleTextureView);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ScaleGLSurfaceActivity.class);
        context.startActivity(starter);
    }
}