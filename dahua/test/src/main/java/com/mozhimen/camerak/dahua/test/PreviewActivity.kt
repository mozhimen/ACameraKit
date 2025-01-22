package com.mozhimen.camerak.dahua.test

import android.os.Bundle
import android.view.SurfaceHolder
import com.mozhimen.bindk.bases.viewdatabinding.activity.BaseActivityVDB
import com.mozhimen.camerak.dahua.CameraKDahua
import com.mozhimen.camerak.dahua.test.databinding.ActivityPreviewBinding


/**
 * @ClassName PreviewActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2022/11/9 11:14
 * @Version 1.0
 */
class PreviewActivity : BaseActivityVDB<ActivityPreviewBinding>() {
    private val _surfaceView by lazy { vdb.previewSurface }
    private val _surfaceCallback by lazy {
        object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                CameraKDahua.instance.with("xxx").getLivePreview().initSurfaceView(_surfaceView)
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
            }
        }
    }
    private var _isStartPreview = false

    override fun initView(savedInstanceState: Bundle?) {
        _surfaceView.holder.addCallback(_surfaceCallback)
        vdb.previewBtnStart.setOnClickListener {
            if (_isStartPreview) return@setOnClickListener
            CameraKDahua.instance.with("xxx").getLivePreview().startPlay(CameraKDahua.instance.with("xxx").getIPLogin().getLoginHandle(), 0, 0, _surfaceView, null)
            _isStartPreview = true
        }

        vdb.previewBtnEnd.setOnClickListener {
            if (!_isStartPreview) return@setOnClickListener
            CameraKDahua.instance.with("xxx").getLivePreview().stopPlay()
            _isStartPreview = false
        }
    }

    override fun onDestroy() {
        CameraKDahua.instance.with("xxx").getLivePreview().stopPlay()
        super.onDestroy()
    }
}