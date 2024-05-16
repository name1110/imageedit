package com.jinxuliang.photoalbum.view

import android.graphics.drawable.BitmapDrawable
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
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageContractOptions
import android.net.Uri
import java.io.ByteArrayOutputStream
import android.content.Context
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class ImageEditingActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editing_image)

        // 获取 Intent 中的照片数据
        val selectedImage = intent.getParcelableExtra<Bitmap>("image")

        // 获取 ImageView 对象并设置 Bitmap
        imageView = findViewById<ImageView>(R.id.imageedit)
        imageView.setImageBitmap(selectedImage)

        // 初始化裁剪图片的ActivityResultLauncher
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // 获取裁剪后的图片Uri
                val uriContent = result.uriContent
                // 将裁剪后的图片设置到ImageView
                imageView.setImageURI(uriContent)
            } else {
                // 处理错误情况
                val exception = result.error
                exception?.printStackTrace()
            }

            findViewById<Button>(R.id.btndone).setOnClickListener {
                // 将ImageView中的图片转换为Bitmap
                imageView.drawable?.let {
                    val bitmap = (it as BitmapDrawable).bitmap
                    val resultIntent = Intent().apply {
                        putExtra("edited_image", bitmap)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

        findViewById<Button>(R.id.btncut).setOnClickListener {
            // 启动裁剪界面
            selectedImage?.let {
                val uri = getImageUriFromBitmap(this, it)
                val options = CropImageOptions().apply{
                    guidelines = Guidelines.ON
                    activityTitle = "Crop Image"
                    outputCompressFormat = Bitmap.CompressFormat.PNG
                    fixAspectRatio = false
                    allowRotation = true
                    allowFlipping = true
                    allowCounterRotation = true
                    autoZoomEnabled = true
                    multiTouchEnabled = true
                    maxZoom = 4
                    showCropOverlay = true
                    showProgressBar = true
                    cropMenuCropButtonTitle = "Done"
                }


                cropImage.launch(CropImageContractOptions(uri, options))
            }
        }
    }

    // 辅助函数，用于将Bitmap转换为Uri
    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

}
