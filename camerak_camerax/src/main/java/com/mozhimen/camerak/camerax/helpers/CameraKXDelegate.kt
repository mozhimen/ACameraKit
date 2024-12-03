package com.mozhimen.camerak.camerax.helpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.camera.camera2.internal.Camera2CameraInfoImpl
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.slider.Slider
import com.mozhimen.basick.utils.runOnMainScope
import com.mozhimen.kotlin.elemk.android.graphics.cons.CImageFormat
import com.mozhimen.kotlin.elemk.java.util.bases.BaseHandlerExecutor
import com.mozhimen.kotlin.lintk.optins.OFieldCall_Close
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_CAMERA
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.android.util.d
import com.mozhimen.kotlin.utilk.android.util.e
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import com.mozhimen.kotlin.utilk.kotlin.ranges.constraint
import com.mozhimen.camerak.camerax.CameraKXLayout
import com.mozhimen.camerak.camerax.CameraKXLayout.Companion.DEBUG
import com.mozhimen.camerak.camerax.annors.AAspectRatio
import com.mozhimen.camerak.camerax.annors.ACameraKXFacing
import com.mozhimen.camerak.camerax.annors.ACameraKXFormat
import com.mozhimen.camerak.camerax.annors.ACameraKXRotation
import com.mozhimen.camerak.camerax.commons.ICameraKX
import com.mozhimen.camerak.camerax.commons.ICameraKXCaptureListener
import com.mozhimen.camerak.camerax.commons.ICameraKXListener
import com.mozhimen.camerak.camerax.commons.ICameraXKFrameListener
import com.mozhimen.camerak.camerax.cons.ECameraKXTimer
import com.mozhimen.camerak.camerax.mos.CameraKXConfig
import com.mozhimen.camerak.camerax.temps.OtherCameraFilter
import com.mozhimen.camerak.camerax.utils.CameraKXUtil
import com.mozhimen.camerak.camerax.utils.imageProxyJpeg2bitmapJpeg
import com.mozhimen.camerak.camerax.utils.imageProxyRgba88882bitmapRgba8888
import com.mozhimen.camerak.camerax.utils.imageProxyYuv4208882bitmapJpeg
import com.mozhimen.libk.jetpack.camera.cons.CImageCapture
import kotlinx.coroutines.delay
import java.util.concurrent.ExecutionException
import kotlin.math.abs
import kotlin.properties.Delegates


/**
 * @ClassName CameraXKDelegate
 * @Description TODO
 * @Author mozhimen / Kolin Zhao
 * @Date 2022/1/3 1:17
 * @Version 1.0
 */
@OPermission_CAMERA
class CameraKXDelegate(private val _cameraKXLayout: CameraKXLayout) : ICameraKX, BaseUtilK() {

    private var _cameraXKListener: ICameraKXListener? = null
    private var _cameraXKCaptureListener: ICameraKXCaptureListener? = null
    private var _cameraXKFrameListener: ICameraXKFrameListener? = null
    private var _cameraXKTimer = ECameraKXTimer.OFF
    private var _imageFormatFrame: Int = ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
    private var _imageCaptureMode = CImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
    private var _isCameraSingle: Boolean = false
    private var _isCameraOpen: Boolean = false
    private var _isAutoFocus: Boolean = true

    //////////////////////////////////////////////////////////////////////////////////////////////

    private var _preview: Preview? = null
    private var _cameraSelectorFacing: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA//显示相机选择的选择器(正面或背面) Selector showing which camera is selected (front or back)
    private var _cameraSelectorHdr: CameraSelector? = null
    private var _zoomState: LiveData<ZoomState>? = null

    private var _imageCapture: ImageCapture? = null
    private var _imageCaptureBitmap: Bitmap? = null
    private var _imageAnalysis: ImageAnalysis? = null
    private var _handlerThreadAnalyzer: HandlerThread? = null
    private lateinit var _lifecycleOwner: LifecycleOwner

    //////////////////////////////////////////////////////////////////////////////////////////////

    @OptIn(OFieldCall_Close::class)
    private val _imageCaptureCallback by lazy {
        object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                "onCaptureSuccess: ${image.format} ${image.width}x${image.height}".d(TAG)
                when (image.format) {
                    CImageFormat.YUV_420_888 -> _imageCaptureBitmap = image.imageProxyYuv4208882bitmapJpeg().also { "onCaptureSuccess: YUV_420_888".d(TAG) }
                    CImageFormat.JPEG -> _imageCaptureBitmap = image.imageProxyJpeg2bitmapJpeg().also { "onCaptureSuccess: JPEG".d(TAG) }
                    CImageFormat.FLEX_RGBA_8888 -> _imageCaptureBitmap = image.imageProxyRgba88882bitmapRgba8888().also { "onCaptureSuccess: FLEX_RGBA_8888".d(TAG) }
                }
                _imageCaptureBitmap?.let { _cameraXKCaptureListener?.onCaptureSuccess(it, image.imageInfo.rotationDegrees) }
                image.close()
            }

            override fun onError(e: ImageCaptureException) {
                "OnImageCapturedCallback onError ImageCaptureException ${e.message}".e(TAG)
                _cameraXKCaptureListener?.onCaptureFail()
                e.printStackTrace()
                e.message?.e(TAG)
            }
        }
    }

    private val _imageAnalysisAnalyzer: ImageAnalysis.Analyzer = ImageAnalysis.Analyzer { imageProxy ->
        _cameraXKFrameListener?.invoke(imageProxy)
    }

    private val _zoomRatioObserver: Observer<ZoomState?> = Observer { value ->
        value?.let { zoomRatio = it.zoomRatio }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    internal var camera: Camera? by Delegates.observable(null) { _, old, new ->
        if (old == new || new == null) return@observable
        _zoomState = camera!!.cameraInfo.zoomState.apply {
            maxZoomRatio = value!!.maxZoomRatio
            minZoomRatio = value!!.minZoomRatio
            zoomRatio = value!!.zoomRatio
            removeObserver(_zoomRatioObserver)
            observe(_lifecycleOwner, _zoomRatioObserver)
        }
    }

    internal val cameraControl: CameraControl?
        get() = camera?.cameraControl

    internal val cameraInfo: CameraInfo?
        get() = camera?.cameraInfo

    internal var maxZoomRatio = 0f
    internal var minZoomRatio = 0f
    internal var zoomRatio = 0f

    internal var isCameraOpening: Boolean by Delegates.observable(false) { _, old, new ->//选择器显示是否启用hdr(只有当设备的摄像头在硬件层面支持hdr时才会工作) Selector showing is hdr enabled or not (will work, only if device's camera supports hdr on hardware level)
        if (old == new) return@observable
        _isCameraOpen = !new
    }

    internal var isOpenHdr: Boolean by Delegates.observable(false) { _, old, new ->//选择器显示是否启用hdr(只有当设备的摄像头在硬件层面支持hdr时才会工作) Selector showing is hdr enabled or not (will work, only if device's camera supports hdr on hardware level)
        if (old == new) return@observable
        restartCameraKX()
    }

    internal var lensFacing: Int by Delegates.observable(CameraSelector.LENS_FACING_BACK) { _, old, new ->
        if (old == new) return@observable
        _cameraSelectorFacing = when (new) {
            ACameraKXFacing.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
            else -> CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    internal var flashMode: Int by Delegates.observable(ImageCapture.FLASH_MODE_OFF) { _, old, new ->//选择器显示所选择的闪光模式(开、关或自动) Selector showing which flash mode is selected (on, off or auto)
        if (old == new) return@observable
        _imageCapture?.flashMode = new
        when (new) {
            ImageCapture.FLASH_MODE_ON -> _cameraXKListener?.onCameraFlashOn()
            ImageCapture.FLASH_MODE_AUTO -> _cameraXKListener?.onCameraFlashAuto()
            else -> _cameraXKListener?.onCameraFlashOff()
        }
    }

    internal var rotation: Int by Delegates.observable(ACameraKXRotation.ROTATION_90) { _, old, new ->
        if (old == new) return@observable
        Log.d(TAG, "rotation: $new")
        _preview?.targetRotation = new
    }

    internal var aspectRatio: Int = AAspectRatio.RATIO_DEFAULT

    internal var resolution: Size = Size(0, 0)

    //////////////////////////////////////////////////////////////////////////////////////////////

    //region open fun
    override fun initCameraKX(owner: LifecycleOwner, config: CameraKXConfig) {
        _lifecycleOwner = owner
        _imageFormatFrame = when (config.format) {
            ACameraKXFormat.RGBA_8888 -> ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
            else -> ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
        }
        _imageCaptureMode = config.captureMode
        _isAutoFocus = config.isAutoFocus
        lensFacing = config.facing
        aspectRatio = config.aspectRatio
        if (config.resolutionWidth > 0 && config.resolutionHeight > 0) {
            resolution = Size(config.resolutionWidth, config.resolutionHeight)
        }
    }

    override fun initCameraKX(owner: LifecycleOwner) {
        initCameraKX(owner, CameraKXConfig())
    }

    @SuppressLint("NewApi")
    override fun restartCameraKX() {

        isCameraOpening = true
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(_context)
            cameraProviderFuture.addListener(
                {
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //获取相机信息
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get() ?: throw IllegalStateException("Camera initialization failed.")

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //回调帧 //图像分析的配置 The Configuration of image analyzing
                    _imageAnalysis = ImageAnalysis.Builder().apply {
                        if (resolution.width > 0 && resolution.height > 0) {
                            setTargetResolution(this@CameraKXDelegate.resolution)
                        } else {
                            setTargetAspectRatio(this@CameraKXDelegate.aspectRatio) // set the analyzer aspect ratio
                        }
                    }
                        .setTargetRotation(this.rotation) // set the analyzer rotation
                        .setOutputImageRotationEnabled(true)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // in our analysis, we care about the latest image
                        .setOutputImageFormat(_imageFormatFrame.also { Log.d(TAG, "restartCameraKX: _imageFormatFrame $_imageFormatFrame") })
                        .build()
                        .also {
                            setCameraXKAnalyzer(it)
                        }
                    if (cameraProvider.isBound(_imageAnalysis!!))
                        return@addListener

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //预览
                    Log.d(TAG, "restartCameraKX: ${resolution.width} x ${resolution.height}")
                    _preview = Preview.Builder().apply {
                        if (resolution.width > 0 && resolution.height > 0) {
                            setTargetResolution(this@CameraKXDelegate.resolution)
                        } else {
                            UtilKLogWrapper.d(TAG, "restartCameraKX: setTargetAspectRatio")
                            setTargetAspectRatio(this@CameraKXDelegate.aspectRatio) // set the camera aspect ratio
                        }
                    }
                        .setTargetRotation(this@CameraKXDelegate.rotation) // set the camera rotation
                        .build().apply {
                            setSurfaceProvider(_cameraKXLayout.previewView!!.surfaceProvider)// Attach the viewfinder's surface provider to preview use case
                        }//摄像头预览的配置 The Configuration of camera preview

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //拍照
                    _imageCapture = ImageCapture.Builder()//图像捕获的配置 The Configuration of image capture
                        .setTargetAspectRatio(this@CameraKXDelegate.aspectRatio) // set the capture aspect ratio
                        .setTargetRotation(this@CameraKXDelegate.rotation) // set the capture rotation
                        .setCaptureMode(_imageCaptureMode) // setting to have pictures with highest quality possible (may be slow)
                        .setFlashMode(this.flashMode) // set capture flash
                        .build()

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //Hdr
                    //checkForHdrExtensionAvailability(cameraProvider)

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //绑定生命周期
                    val processCameraProvider: ProcessCameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
                    processCameraProvider.unbindAll()// Unbind the use-cases before rebinding them
                    bindToLifecycle(processCameraProvider, _preview!!, _cameraKXLayout.slider!!, _cameraKXLayout.seekBar!!)// Bind all use cases to the camera with lifecycle
                },
                ContextCompat.getMainExecutor(_context)
            )
            isCameraOpening = false
        } catch (e: InterruptedException) {
            _cameraXKListener?.onCameraStartFail(e.message ?: "").also { "startCamera InterruptedException ${e.message ?: ""}".e(TAG) }
        } catch (e: ExecutionException) {
            _cameraXKListener?.onCameraStartFail(e.message ?: "").also { "startCamera ExecutionException ${e.message ?: ""}".e(TAG) }
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.e(TAG)
        }
    }

    override fun setCameraXListener(listener: ICameraKXListener) {
        _cameraXKListener = listener
    }

    override fun setCameraXCaptureListener(listener: ICameraKXCaptureListener) {
        _cameraXKCaptureListener = listener
    }

    override fun setCameraXFrameListener(listener: ICameraXKFrameListener) {
        _cameraXKFrameListener = listener
    }

    override fun changeHdr(isOpen: Boolean) {
        if (isOpenHdr == isOpen) return
        this.isOpenHdr = isOpen
    }

    override fun changeFlashMode(@ImageCapture.FlashMode flashMode: Int) {
        if (this.flashMode == flashMode) return
        this.flashMode = flashMode
    }

    override fun changeFlash(isOpen: Boolean) {
        cameraControl?.enableTorch(isOpen)
    }

    override fun changeCountDownTimer(timer: ECameraKXTimer) {
        if (_cameraXKTimer == timer) return
        _cameraXKTimer = timer
    }

    override fun changeRotation(rotation: Int) {
        if (this.rotation == rotation) return
        this.rotation = rotation
    }

    override fun changeFacing(@ACameraKXFacing facing: Int) {
        if (facing == lensFacing || _isCameraSingle) return
        lensFacing = facing
        restartCameraKX()
    }

    override fun changeZoomRatio(ratio: Float) {
        cameraControl?.setZoomRatio((/*zoomRatio * scaleFactor*/ratio).constraint(minZoomRatio, maxZoomRatio).also {
            if (DEBUG)
                UtilKLogWrapper.d(TAG, "onScale: ratio $it minZoomRatio $minZoomRatio maxZoomRatio $maxZoomRatio")
        })
    }

    override fun startCapture() {
        _lifecycleOwner.runOnMainScope {
            when (_cameraXKTimer) {// Show a timer based on user selection
                ECameraKXTimer.S3 -> for (i in 3 downTo 1) delay(1000)
                ECameraKXTimer.S10 -> for (i in 10 downTo 1) delay(1000)
                else -> {}
            }
            _imageCapture?.takePicture(ContextCompat.getMainExecutor(_context), _imageCaptureCallback)// the executor, on which the task will run)
        }
    }

    override fun isCameraKXStart(): Boolean {
        return _isCameraOpen
    }

    override fun stopCameraKX() {
        if (_handlerThreadAnalyzer != null && !_handlerThreadAnalyzer!!.isInterrupted) {
            _handlerThreadAnalyzer?.interrupt()
            _handlerThreadAnalyzer = null
        }
        _zoomState?.removeObserver(_zoomRatioObserver)
        _zoomState = null
        _isCameraOpen = false
    }
    //endregion

    //////////////////////////////////////////////////////////////////////////////////////////////

    //region private fun
    @SuppressLint("RestrictedApi")
    @Throws(Exception::class)
    private fun bindToLifecycle(localCameraProvider: ProcessCameraProvider, preview: Preview, slider: Slider, seekBar: AppCompatSeekBar) {
        if (localCameraProvider.availableCameraInfos.size == 1) {
            Log.d(TAG, "bindToLifecycle: availCamera size = localCameraProvider.availableCameraInfos.size")
            _isCameraSingle = true
            val cameraInfo: Camera2CameraInfoImpl = (localCameraProvider.availableCameraInfos[0] as Camera2CameraInfoImpl)
                .also { Log.d(TAG, "bindToLifecycle: cameraInfo $it _lensFacing ${it.cameraSelector.lensFacing} id ${it.cameraId}") }
            _cameraSelectorFacing.cameraFilterSet.clear()
            _cameraSelectorFacing.cameraFilterSet.add(OtherCameraFilter(cameraInfo.cameraId))
        }
        camera = localCameraProvider.bindToLifecycle(
            _lifecycleOwner, // current lifecycle owner
            /*_hdrCameraSelector ?: */
            _cameraSelectorFacing, // either front or back facing
            preview, // camera preview use case
            _imageCapture!!, // image capture use case
            _imageAnalysis!!, // image analyzer use case
        )

        cameraControl?.let {
            if (!_isAutoFocus) {
                CameraKXUtil.disableAutofocus(it)
                if (cameraInfo != null) {
                    initSeekbarFocusDistance(seekBar, cameraInfo!!, it)
                }
            }
        }

        // Init camera exposure control
        cameraInfo?.let {
            initSliderExposureCompensation(slider, it)
        }

//            Log.d(TAG, "bindToLifecycle: getSupportedResolutions ${CameraKXUtil.getSupportedResolutions(this)}")
    }

    private fun initSeekbarFocusDistance(seekBar: AppCompatSeekBar, cameraInfo: CameraInfo, cameraControl: CameraControl) {
        val minAndMaxFocusDistance = CameraKXUtil.getFocusDistanceRange(cameraInfo)
        if (minAndMaxFocusDistance != null) {
            seekBar.progress = 50
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (seekBar != null) {
                        val distance = (seekBar.progress.toFloat() / 100f) * abs(minAndMaxFocusDistance.first - minAndMaxFocusDistance.second)
                        CameraKXUtil.changeFocusDistance(cameraControl, distance)
                    }
                }

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                }
            })
            val distance = 0.5f * abs(minAndMaxFocusDistance.first - minAndMaxFocusDistance.second)
            CameraKXUtil.changeFocusDistance(cameraControl, distance)
        }
    }

    private fun initSliderExposureCompensation(slider: Slider, cameraInfo: CameraInfo) {
        val lower = cameraInfo.exposureState.exposureCompensationRange.lower
        val upper = cameraInfo.exposureState.exposureCompensationRange.upper

        slider.valueFrom = lower.toFloat()
        slider.valueTo = upper.toFloat()
        slider.stepSize = 1f
        slider.value = cameraInfo.exposureState.exposureCompensationIndex.toFloat()
        slider.addOnChangeListener { _, value, _ ->
            cameraControl?.setExposureCompensationIndex(value.toInt())
        }
    }

    /**
     * 为HDR创建供应商扩展
     * Create a Vendor Extension for HDR
     */
    private fun checkForHdrExtensionAvailability(cameraProvider: CameraProvider) {
        val extensionsManagerFuture = ExtensionsManager.getInstanceAsync(_context, cameraProvider)
        extensionsManagerFuture.addListener(
            {
                val extensionsManager = extensionsManagerFuture.get() ?: return@addListener
                val isAvailable = extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.HDR)

                //检查是否有扩展可用 check for any extension availability
                Log.d(TAG, "checkForHdrExtensionAvailability: AUTO " + extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.AUTO))
                Log.d(TAG, "checkForHdrExtensionAvailability: HDR " + extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.HDR))
                Log.d(TAG, "checkForHdrExtensionAvailability: FACE RETOUCH " + extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.FACE_RETOUCH))
                Log.d(TAG, "checkForHdrExtensionAvailability: BOKEH " + extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.BOKEH))
                Log.d(TAG, "checkForHdrExtensionAvailability: NIGHT " + extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.NIGHT))
                Log.d(TAG, "checkForHdrExtensionAvailability: NONE " + extensionsManager.isExtensionAvailable(_cameraSelectorFacing, ExtensionMode.NONE))

                //检查分机是否在设备上可用 Check if the extension is available on the device
                if (!isAvailable) {
                    _cameraXKListener?.onCameraHDRCheck(false)
                } else if (isOpenHdr) {
                    //如果是，如果HDR是由用户打开的，则打开 If yes, turn on if the HDR is turned on by the user
                    _cameraXKListener?.onCameraHDROpen()
                    _cameraSelectorHdr = extensionsManager.getExtensionEnabledCameraSelector(_cameraSelectorFacing, ExtensionMode.HDR)
                }
            }, ContextCompat.getMainExecutor(_context)
        )
    }

    private fun setCameraXKAnalyzer(imageAnalysis: ImageAnalysis) {
        //使用工作线程进行图像分析，以防止故障 Use a worker thread for image analysis to prevent glitches
        _cameraXKFrameListener?.let {
            stopCameraKX()
            _handlerThreadAnalyzer = HandlerThread("CameraXKLuminosityAnalysis").apply { start() }
            imageAnalysis.setAnalyzer(BaseHandlerExecutor(Handler(_handlerThreadAnalyzer!!.looper)), _imageAnalysisAnalyzer)
        }
    }
    //endregion
}