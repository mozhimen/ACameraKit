package com.mozhimen.camerak.uvc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @author: SteveZ
 * @created on: 2020/5/25 5:59 PM.
 * @description: Common TextureView
 */
public class ScaleTextureView extends TextureView implements ICameraView, LifecycleObserver {
    private static final int DEFAULT_PREVIEW_WIDTH = 640;
    private static final int DEFAULT_DISPLAY_DIR;
    private static final int DEFAULT_PREVIEW_HEIGHT = 480;
    private ScaleManager mScaleManager;
    private ScalableType mScalableType;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mDisplayDir;
    private boolean mIsMirror;

    public ScaleTextureView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ScaleTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScalableType = ScalableType.NONE;
        this.mPreviewWidth = DEFAULT_PREVIEW_WIDTH;
        this.mPreviewHeight = DEFAULT_PREVIEW_HEIGHT;
        this.mDisplayDir = DEFAULT_DISPLAY_DIR;
        this.mIsMirror = false;
        if (attrs != null) {
            this.setKeepScreenOn(true);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.cameraStyle, 0, 0);
            if (a != null) {
                int scaleType = a.getInt(R.styleable.cameraStyle_scalableType, ScalableType.NONE.ordinal());
                this.mDisplayDir = a.getInt(R.styleable.cameraStyle_cameraDir, DEFAULT_DISPLAY_DIR);
                this.mPreviewWidth = a.getInt(R.styleable.cameraStyle_previewWidth, DEFAULT_PREVIEW_WIDTH);
                this.mPreviewHeight = a.getInt(R.styleable.cameraStyle_previewHeight, DEFAULT_PREVIEW_HEIGHT);
                this.mIsMirror = a.getBoolean(R.styleable.cameraStyle_isMirror, false);
                a.recycle();
                this.mScalableType = ScalableType.values()[scaleType];
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        ScaleTextureView.this.scaleContentSize(ScaleTextureView.this.mPreviewWidth, ScaleTextureView.this.mPreviewHeight);
                    }
                });
            }
        }
    }

    private void scaleContentSize(int contentWidth, int contentHeight) {
        if (contentWidth != 0 && contentHeight != 0) {
            int width = this.getWidth();
            int height = this.getHeight();
            boolean exChange;
            if (DEFAULT_DISPLAY_DIR == this.mDisplayDir) {
                if (width > height) {
                    exChange = false;
                } else {
                    exChange = true;
                }
            } else if (this.mDisplayDir != Direction.LEFT.getValue() && this.mDisplayDir != Direction.RIGHT.getValue()) {
                exChange = false;
            } else {
                exChange = true;
            }

            PreviewSize viewSize = new PreviewSize(width, height);
            PreviewSize contentSize = new PreviewSize(contentWidth, contentHeight);
            this.mScaleManager = new ScaleManager(viewSize, contentSize, this.mIsMirror, exChange, this.mDisplayDir, false);
            Matrix matrix = this.mScaleManager.getScaleMatrix(this.mScalableType);
            if (matrix != null) {
                this.setTransform(matrix);
            }

        }
    }

    @Override
    public void resetPreviewSize(int width,int height) {
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
        this.scaleContentSize(this.mPreviewWidth, this.mPreviewHeight);
    }

    @Override
    public void setMirror(boolean mirror) {
        this.mIsMirror = mirror;
        this.scaleContentSize(this.mPreviewWidth, this.mPreviewHeight);
    }

    @Override
    public void setDisplayDir(Direction displayDirection) {
        this.mDisplayDir = displayDirection.getValue();
        this.scaleContentSize(this.mPreviewWidth, this.mPreviewHeight);
    }

    @Override
    public void setStyle(ScalableType style) {
        this.mScalableType = style;
        this.scaleContentSize(this.mPreviewWidth, this.mPreviewHeight);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    @Override
    public void onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @Override
    public void onStop() {

    }

    /**
     * ScaleManager
     *
     * @return
     */
    public ScaleManager getScaleManager() {
        return this.mScaleManager;
    }

    static {
        DEFAULT_DISPLAY_DIR = Direction.AUTO.getValue();
    }

}

