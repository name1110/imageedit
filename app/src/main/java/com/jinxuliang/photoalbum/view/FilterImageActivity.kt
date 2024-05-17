package com.jinxuliang.photoalbum.view


import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.jinxuliang.photoalbum.R
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.ViewType
import java.io.ByteArrayOutputStream
import androidx.annotation.NonNull
import androidx.lifecycle.lifecycleScope
import ja.burhanrashid52.photoeditor.PhotoFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterImageActivity : AppCompatActivity(){

    private lateinit var photoEditor: PhotoEditor
    private lateinit var photoEditorView: PhotoEditorView
    private lateinit var backgroundImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_filter)

        // 从意图中获取背景图像
        backgroundImage = intent.getParcelableExtra<Bitmap>("image")!!

        // 初始化视图
        photoEditorView = findViewById(R.id.photoEditorView)

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
            applyFilter()
        }

        // 处理保存按钮点击事件
        findViewById<Button>(R.id.btn_save).setOnClickListener {
            saveImage()
        }
    }

    private fun applyFilter() {
        val selectedFilterId = getSelectedfilter()
        when (selectedFilterId) {
            R.id.BLACK_WHITE -> {
                photoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS)
            }
            R.id.LOMISH -> {
                photoEditor.setFilterEffect(PhotoFilter.LOMISH)
            }
            R.id.FISH_EYE -> {
                photoEditor.setFilterEffect(PhotoFilter.FISH_EYE)
            }
            R.id.NEGATIVE -> {
                photoEditor.setFilterEffect(PhotoFilter.NEGATIVE)
            }
            R.id.SHARPEN -> {
                photoEditor.setFilterEffect(PhotoFilter.SHARPEN)
            }
            else -> {
                // 处理未选择或未知滤镜的情况
            }
        }
    }

    private fun getSelectedfilter(): Int {
        val radioGroup = findViewById<RadioGroup>(R.id.rg_color)
        return radioGroup.checkedRadioButtonId
    }

    private fun saveImage() {
        lifecycleScope.launch {
            val result = photoEditor.saveAsBitmap()
            // 将绘制的 Bitmap 放置到 Intent 中
            val scaledBitmap = Bitmap.createScaledBitmap(result, (result.width / 2.5).toInt(), (result.height / 2.5).toInt(), true)
            val resultIntent = Intent().apply {
                putExtra("edited_image", scaledBitmap)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}