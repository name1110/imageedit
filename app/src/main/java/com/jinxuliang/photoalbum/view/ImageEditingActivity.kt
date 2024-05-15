package com.jinxuliang.photoalbum.view

import android.widget.Button
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.launch
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.jinxuliang.photoalbum.R


const val REQUEST_CROP = 1
class ImageEditingActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.editing_image)

        // 获取 Intent 中的照片数据
        val selectedImage = intent.getParcelableExtra<Bitmap>("image")

        // 获取 ImageView 对象并设置 Bitmap
        val imageView = findViewById<ImageView>(R.id.imageedit)
        imageView.setImageBitmap(selectedImage)
        findViewById<Button>(R.id.btncut).setOnClickListener {
            Log.d("ImageEditingActivity", "Button clicked")
            val intent = Intent(this, cropactivity::class.java).apply {
                putExtra("image", selectedImage)
                Log.d("CropActivity", "Button Clicked")
            }
            startActivityForResult(intent, REQUEST_CROP)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val croppedImage = data?.getParcelableExtra<Bitmap>("cropped_image")
            if (croppedImage != null) {
                // 在这里处理从裁剪操作返回的裁剪后的图像数据
                imageView.setImageBitmap(croppedImage)
                // 更新 ImageView 或执行其他操作
            }
        }
    }
}