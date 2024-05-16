package com.jinxuliang.photoalbum.view

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.jinxuliang.photoalbum.R
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.ViewType


class DrawTextActivity : AppCompatActivity() {

    private lateinit var photoEditor: PhotoEditor
    private lateinit var photoEditorView: PhotoEditorView
    private lateinit var etText: EditText
    private lateinit var tvTextSize: TextView
    private lateinit var backgroundImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_text)

        // 从意图中获取背景图像
        backgroundImage = intent.getParcelableExtra<Bitmap>("image")!!

        // 初始化视图
        photoEditorView = findViewById(R.id.photoEditorView)
        etText = findViewById(R.id.et_text)

        val scaledBitmap = Bitmap.createScaledBitmap(backgroundImage, (backgroundImage.width * 2.5).toInt(), (backgroundImage.height * 2.5).toInt(), true)

        // 初始化 PhotoEditor
        photoEditor = PhotoEditor.Builder(this, photoEditorView)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .setDefaultTextTypeface(Typeface.DEFAULT)
            .build()

        photoEditorView.source.setImageBitmap(scaledBitmap)
        // 处理添加文本按钮点击事件
        findViewById<Button>(R.id.btn_draw).setOnClickListener {
            val inputText = etText.text.toString()
            val colorCode = getSelectedColor()
            photoEditor.addText(inputText, colorCode)
        }

        // 处理保存按钮点击事件
        findViewById<Button>(R.id.btn_save).setOnClickListener {
        }

        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
                // 处理编辑文本回调
                etText.setText(text)
            }

            override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
                // 不需要添加任何逻辑，但是需要实现该方法
            }

            override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
                // 不需要添加任何逻辑，但是需要实现该方法
            }

            override fun onStartViewChangeListener(viewType: ViewType) {
                // 不需要添加任何逻辑，但是需要实现该方法
            }

            override fun onStopViewChangeListener(viewType: ViewType) {
                // 不需要添加任何逻辑，但是需要实现该方法
            }

            override fun onTouchSourceImage(event: MotionEvent) {
                // 不需要添加任何逻辑，但是需要实现该方法
            }
        })


        // 处理文本大小拖动条变化事件

    }



    private fun getSelectedColor(): Int {
        val radioGroup = findViewById<RadioGroup>(R.id.rg_color)
        return when (radioGroup.checkedRadioButtonId) {
            R.id.rb_red -> Color.RED
            R.id.rb_green -> Color.GREEN
            R.id.rb_blue -> Color.BLUE
            R.id.rb_yellow -> Color.YELLOW
            R.id.rb_purple -> Color.MAGENTA
            else -> Color.BLACK
        }
    }
}
