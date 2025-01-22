package com.mozhimen.camerak.opengl

import android.graphics.Rect
import android.graphics.RectF
import com.mozhimen.kotlin.utilk.kotlin.ranges.constraint
import kotlin.math.roundToInt

/**
 * @ClassName CameraKCameraOpenGLUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/3/22
 * @Version 1.0
 */
object CameraKOpenGLUtil {
    /**
     * 计算对焦和测光区域
     * @param coefficient        比率
     * @param originFocusCenterX 对焦中心点X
     * @param originFocusCenterY 对焦中心点Y
     * @param originFocusWidth   对焦宽度
     * @param originFocusHeight  对焦高度
     * @param previewViewWidth   预览宽度
     * @param previewViewHeight  预览高度
     */
    @JvmStatic
    fun focusMeteringArea(
        coefficient: Float,
        originFocusCenterX: Float, originFocusCenterY: Float,
        originFocusWidth: Int, originFocusHeight: Int,
        previewViewWidth: Int, previewViewHeight: Int
    ): Rect {
        val halfFocusAreaWidth = (originFocusWidth * coefficient / 2).toInt()
        val halfFocusAreaHeight = (originFocusHeight * coefficient / 2).toInt()
        val centerX = (originFocusCenterX / previewViewWidth * 2000 - 1000).toInt()
        val centerY = (originFocusCenterY / previewViewHeight * 2000 - 1000).toInt()
        val rectF = RectF(
            (centerX - halfFocusAreaWidth).constraint(-1000..1000).toFloat(),
            (centerY - halfFocusAreaHeight).constraint(-1000..1000).toFloat(),
            (centerX + halfFocusAreaWidth).constraint(-1000..1000).toFloat(),
            (centerY + halfFocusAreaHeight).constraint(-1000..1000).toFloat()
        )
        return Rect(rectF.left.roundToInt(), rectF.top.roundToInt(), rectF.right.roundToInt(), rectF.bottom.roundToInt())
    }
}