package com.jinxuliang.photoalbum.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageContractOptions
import com.jinxuliang.photoalbum.R

class CropImageActivity : AppCompatActivity() {

    private lateinit var cropImageView: CropImageView
    private lateinit var btnCrop: Button
    private lateinit var btnSave: Button
    private lateinit var content :Bitmap
    private lateinit var btnclear :Button
    private var cropped: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_crop)

        cropImageView = findViewById(R.id.cropImageView)
        btnCrop = findViewById(R.id.btncrop)
        btnSave = findViewById(R.id.btn_save)
        btnclear = findViewById(R.id.btnclear)

        // 从意图中获取背景图像
        content = intent.getParcelableExtra<Bitmap>("image")!!
        cropImageView.setImageBitmap(content)

        // 按钮点击监听器
        btnCrop.setOnClickListener {
            cropImageView.setImageBitmap(content)

        }
        btnCrop.setOnClickListener {
            cropImageView.setImageBitmap(content)

        }
        btnclear.setOnClickListener{
            cropped = cropImageView.getCroppedImage()
            cropImageView.setImageBitmap(cropped)
        }

        btnSave.setOnClickListener {
            try {
                // 将绘制的 Bitmap 放置到 Intent 中
                val resultIntent = Intent().apply {
                    putExtra("edited_image", cropped)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finish() // 结束当前 Activity
        }
    }


}
