package com.jinxuliang.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //为方便绘图，自定义了一个Path类，封装了颜色和画笔尺寸信息
    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ) : Path()

    //用于引用当前的绘图路径对象
    private var mDrawPath: CustomPath? = null

    //这个集合包容了当前绘图表面上的所有图形
    private val mPaths = ArrayList<CustomPath>()

    //绘图表面
    private var canvas: Canvas? = null

    //绘图表面关联的位图对象
    private var mCanvasBitmap: Bitmap? = null

    //画刷
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()

    // 默认绘图颜色
    private var color = Color.BLACK

    init {
        setUpDrawing()
    }

    //初始化绘图相关配置参数
    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mDrawPaint?.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, wprev: Int, hprev: Int) {
        super.onSizeChanged(w, h, wprev, hprev)
        //当View的尺寸改变时，重新生成一张新画布
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //将bitmap对象绘制在Canvas上
        mCanvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mCanvasPaint)
        }

        //遍历Path数组，绘制每个Path，从而实现（以前）图形的绘制
        for (p in mPaths) {
            mDrawPaint?.strokeWidth = p.brushThickness
            mDrawPaint?.color = p.color
            canvas.drawPath(p, mDrawPaint!!)
        }
        //绘制当前的Path
        if (!mDrawPath!!.isEmpty) {
            mDrawPaint?.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint?.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when (event?.action) {
            //手指按下，设定为当前path的起始点
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)
            }
            //手指移动，向当前Path添加“直线段”
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null && touchY != null) {
                    mDrawPath!!.lineTo(touchX, touchY)
                }
            }
            //手指松开，将当前Path添加到图形集合中
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        //立即重绘，它将导致onDraw方法的调用执行
        invalidate()
        return true
    }

    //设置画笔尺寸
    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize,
            resources.displayMetrics
        )
        mDrawPaint?.strokeWidth = mBrushSize
    }

    //设置画笔颜色，注意这里的参数值，来自于colors.xml，
    //是"#35a79c"这样格式的
    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint?.color = color
    }

    //用于保存“Undo”的Path对象
    private val mUndoPaths = ArrayList<CustomPath>()
    fun onClickUndo() {
        if (mPaths.size > 0) {
            //先移除，再追加到Undo集合中
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate() // 重绘界面
        }
    }

}