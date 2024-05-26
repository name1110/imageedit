package com.name1110.photoeditor.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.name1110.photoeditor.R
import java.io.File
import java.io.FileOutputStream

class MyPaintToolsActivity : AppCompatActivity() {

    private lateinit var paint: Paint
    private lateinit var canvas: Canvas
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var tvStroke: TextView
    private var startX = 0
    private var startY = 0
    private var endX = 0
    private var endY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_draw)

        tvStroke = findViewById(R.id.tv_stroke)
        val llLayout = findViewById<LinearLayout>(R.id.ll_layout)
        val rgColor = findViewById<RadioGroup>(R.id.rg_color)
        val backgroundImage = intent.getParcelableExtra<Bitmap>("image")!!
        // 循环遍历单选按钮
        for (i in 0 until rgColor.childCount) {
            val rb = rgColor.getChildAt(i) as RadioButton
            rb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    paint.color = buttonView.currentTextColor // 获取单选按钮颜色并将颜色设置
                }
            }
        }

        imageView = findViewById(R.id.imageview)
        Log.i("MyPaintToolsActivity", "${imageView.width} ${imageView.height}")
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        Log.i("MyPaintToolsActivity", "${point.x} ${point.y}")

        bitmap = Bitmap.createBitmap(backgroundImage.width, backgroundImage.height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawBitmap(backgroundImage, 0f, 0f, null)
        paint = Paint().apply {
            strokeWidth = 5f
            isAntiAlias = true
            color = Color.RED
        }
        canvas.drawBitmap(bitmap, Matrix(), paint) // 把灰色背景画在画布上
        imageView.setImageBitmap(bitmap) // 把图片加载到ImageView上

        // 注册触摸监听事件

        imageView.setOnTouchListener { v, event ->
            val scaleFactor = backgroundImage.width.toFloat() / v.width.toFloat() // 计算背景图片与ImageView的宽度比例
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = (event.x *scaleFactor).toInt()
                    startY = (event.y *scaleFactor-30).toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    endX = (event.x *scaleFactor).toInt()
                    endY = (event.y *scaleFactor-30).toInt()
                    canvas.drawLine(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), paint)
                    startX = (event.x*scaleFactor ).toInt()
                    startY = (event.y *scaleFactor-30).toInt()
                    imageView.setImageBitmap(bitmap)
                }
                MotionEvent.ACTION_UP -> {
                }
            }
            imageView.invalidate()
            true
        }
        // 清除
        val btnClear = findViewById<Button>(R.id.btn_clear)
        btnClear.setOnClickListener {
            // 清空画布的方法
            // 方法一
            // canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            // 方法二
            // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            // 方法三
            canvas.drawBitmap(backgroundImage, 0f, 0f, null) // Reset canvas with background image
            imageView.invalidate()
        }

        // 保存
        val btnSave = findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            try {
                // 将绘制的 Bitmap 放置到 Intent 中
                val resultIntent = Intent().apply {
                    putExtra("edited_image", bitmap)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finish() // 结束当前 Activity
        }



        // Progress进度条 ，调节画笔粗细
        val sbStroke = findViewById<SeekBar>(R.id.sb_stroke)
        sbStroke.progress = 5 // 进度条初始大小值为5
        sbStroke.max = 30
        sbStroke.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                paint.strokeWidth = progress.toFloat()
                tvStroke.text = "画笔粗度为: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


    }
}
