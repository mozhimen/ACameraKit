package com.mozhimen.camerakuvctest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.mozhimen.camerakuvctest.R
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @date: Create in 3:36 PM 2020/7/8
 * @author: zhuyuliang
 * @description Demo Home
 */
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        checkPermission()
        findViewById<AppCompatButton>(R.id.btn_config).setOnClickListener {
            ConfigActivity.start(this)
        }
        // 双目 Camera TextureView 封装
        findViewById<AppCompatButton>(R.id.btn_binocularcameratextureview).setOnClickListener {
            BinocularTextureViewActivity.start(this)
        }
        // 双目 Camera SurfaceView 封装
        findViewById<AppCompatButton>(R.id.btn_BinocularCameraGLSurfaceView).setOnClickListener {
            BinocularGLSurfaceActivity.start(this)
        }
        // 单目 Camera TextureView 封装
        findViewById<AppCompatButton>(R.id.btn_CameraTextureView).setOnClickListener {
            CameraTextureViewActivity.start(this)
        }
        // 单目 Camera SurfaceView 封装
        findViewById<AppCompatButton>(R.id.btn_CameraGLSurfaceView).setOnClickListener {
            CameraGLSurfaceActivity.start(this)
        }
        // ScaleTextureView 单独封装
        findViewById<AppCompatButton>(R.id.btn_TextureView).setOnClickListener {
            ScaleTextureViewActivity.start(this)
        }
        // ScaleSurfaceView 单独封装
        findViewById<AppCompatButton>(R.id.btn_GLSurfaceView).setOnClickListener {
            ScaleGLSurfaceActivity.start(this)
        }
        // Camera1调用
        findViewById<AppCompatButton>(R.id.btn_Camera1).setOnClickListener {
            Camera1Activity.start(this)
        }
        // Camera2调用
        findViewById<AppCompatButton>(R.id.btn_Camera2).setOnClickListener {
            Camera2Activity.start(this)
        }
        // UvcCamera调用
        findViewById<AppCompatButton>(R.id.btn_UvcCamera).setOnClickListener {
            CameraUvcActivity.start(this)
        }
        // 两个摄像头预览
        findViewById<AppCompatButton>(R.id.btn_TwoPreviewCamera).setOnClickListener {
            TwoCameraScaleTextureViewActivity.start(this)
        }
    }

    /**
     * check permission
     */
    private fun checkPermission() {
        RxPermissions(this)
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isGranted: Boolean ->
                if (isGranted) {
                    //Toaster.show(R.string.init_success);
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    finish()
                }
            }, { throwable: Throwable ->
                Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT)
            })
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, HomeActivity::class.java)
            starter.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
            context.startActivity(starter)
        }
    }
}