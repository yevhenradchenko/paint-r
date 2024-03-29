package com.paintr

import android.R.attr.bitmap
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.math.abs


class CanvasCustomView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    var drawingColor: Int = ResourcesCompat.getColor(resources, R.color.colorBlack, null)
    var strokeDrawWidth: Float = 12f

    private var path = Path()

    private val paths = ArrayList<Triple<Path, Int, Float>>()
    private val undonePaths = ArrayList<Triple<Path, Int, Float>>()

    private var bitmapBackground: Bitmap? = null

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private val paint = Paint().apply {
        color = drawingColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = strokeDrawWidth
    }

    fun loadCanvasBackground(bitmap: Bitmap) {
        bitmapBackground = bitmap
        invalidate()
    }

    fun saveCanvasDrawing() : String {
        canvasCustomView.isDrawingCacheEnabled = true
        val extraBitmap: Bitmap = canvasCustomView.drawingCache
        return MediaStore.Images.Media.insertImage(context.contentResolver, extraBitmap, "drawing", "Paint R")
    }

    fun resetCanvasDrawing() {
        path.reset()
        paths.clear()
        invalidate()
    }

    fun undoCanvasDrawing() {
        if (paths.size > 0) {
            undonePaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        } else {
            Log.d("UNDO_ERROR", "Something went wrong with UNDO action")
        }
    }

    fun redoCanvasDrawing() {
        if (undonePaths.size > 0) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        } else {
            Log.d("REDO_ERROR", "Something went wrong with REDO action")
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bitmapBackground != null) {
            canvas?.drawBitmap(bitmapBackground!!, 0f, 0f, paint)
        }

        for (p in paths) {
            paint.strokeWidth = p.third
            paint.color = p.second
            canvas?.drawPath(p.first, paint)
        }

        paint.color = drawingColor
        paint.strokeWidth = strokeDrawWidth
        canvas?.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return false

        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                undonePaths.clear()
                path.reset()
                path.moveTo(motionTouchEventX, motionTouchEventY)
                currentX = motionTouchEventX
                currentY = motionTouchEventY
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                val distanceX = abs(motionTouchEventX - currentX)
                val distanceY = abs(motionTouchEventY - currentY)

                if (distanceX >= touchTolerance || distanceY >= touchTolerance) {
                    path.quadTo(
                        currentX,
                        currentY,
                        (motionTouchEventX + currentX) / 2,
                        (currentY + motionTouchEventY) / 2)
                    currentX = motionTouchEventX
                    currentY = motionTouchEventY
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(currentX, currentY)
                paths.add(Triple(path, drawingColor, strokeDrawWidth))
                path = Path()
            }
        }
        return true
    }

    override fun isSaveEnabled(): Boolean {
        return true
    }
}

