package com.jinxuliang.photoalbum.view

import android.view.MotionEvent
import android.graphics.Color
import android.graphics.Path
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.jinxuliang.photoalbum.R


class cropactivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置布局和初始化视图
        setContentView(R.layout.image_crop)

        // 处理从 ImageEditingActivity 传递过来的原始图像数据
        val originalImage: Bitmap? = intent.getParcelableExtra("image")
        val imageView: ImageView = findViewById(R.id.imageView)
        if (originalImage == null) {
            println("wrong")
        }
        imageView.setImageBitmap(originalImage)

        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 按下时记录起始坐标
                    startX = event.x
                    startY = event.y
                    Log.d("CropActivity", "ACTION_DOWN: startX=$startX, startY=$startY")
                    true // 返回 true 表示事件已被消费
                }

                MotionEvent.ACTION_MOVE -> {
                    // 移动时绘制裁剪框
                    endX = event.x
                    endY = event.y
                    Log.d("CropActivity", "ACTION_MOVE: endX=$endX, endY=$endY")

                    // 重新绘制ImageView
                    imageView.setImageBitmap(
                        drawRectangleOnBitmap(
                            originalImage,
                            startX,
                            startY,
                            endX,
                            endY
                        )
                    )

                    true // 返回 true 表示事件已被消费
                }

                MotionEvent.ACTION_UP -> {
                    // 抬起时记录结束坐标并执行裁剪操作
                    endX = event.x
                    endY = event.y
                    Log.d("CropActivity", "ACTION_UP: endX=$endX, endY=$endY")
                    // 执行裁剪操作
                    val croppedImage: Bitmap? =
                        performCrop(originalImage, startX, startY, endX, endY)
                    // 将裁剪后的图像数据返回给 ImageEditingActivity
                    val resultIntent = Intent()
                    resultIntent.putExtra("cropped_image", croppedImage)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    true // 返回 true 表示事件已被消费
                }

                else -> false // 返回 false 表示事件未被消费
            }
        }
    }

    private fun performCrop(
        originalImage: Bitmap?,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ): Bitmap? {
        if (originalImage == null) {
            return null
        }

        // 计算裁剪区域的宽度和高度
        val width = (endX - startX).toInt()
        val height = (endY - startY).toInt()

        // 创建一个与裁剪区域相同大小的位图
        val croppedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 创建一个用于绘制的画布
        val canvas = Canvas(croppedBitmap)

        // 绘制裁剪区域的部分到新的位图中
        val srcRect = Rect(startX.toInt(), startY.toInt(), endX.toInt(), endY.toInt())
        val destRect = Rect(0, 0, width, height)
        canvas.drawBitmap(originalImage, srcRect, destRect, null)

        // 返回裁剪后的图像数据
        return croppedBitmap
    }

    private fun drawRectangleOnBitmap(
        originalImage: Bitmap?,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ): Bitmap? {
        if (originalImage == null) {
            return null
        }

        // 创建一个新的位图用于绘制
        val bitmapWithRectangle = originalImage.copy(Bitmap.Config.ARGB_8888, true)

        // 创建画布和画笔
        val canvas = Canvas(bitmapWithRectangle)
        val paint = Paint().apply {
            color = Color.RED // 设置画笔颜色为红色
            style = Paint.Style.STROKE
            strokeWidth = 5f // 设置画笔宽度
        }

        // 绘制矩形
        canvas.drawRect(startX, startY, endX, endY, paint)

        return bitmapWithRectangle
    }


}