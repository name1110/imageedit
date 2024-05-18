package com.jinxuliang.photoalbum.view

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.widget.Button
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.jinxuliang.photoalbum.R
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageContractOptions
import android.net.Uri
import java.io.ByteArrayOutputStream
import android.content.Context
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher


class ImageEditingActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    private var editimage: Bitmap? = null // 将 editedBitmap 声明在类级别

    companion object {
        const val DRAW_REQUEST_CODE = 1 // 定义一个请求码
        const val TEXT_REQUEST_CODE = 2
        const val FILTER_REQUEST_CODE = 3
        const val COLERIZED_REQUEST_CODE = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editing_image)

        // 获取 Intent 中的照片数据
        editimage = intent.getParcelableExtra<Bitmap>("image")

        // 获取 ImageView 对象并设置 Bitmap
        imageView = findViewById<ImageView>(R.id.imageedit)
        imageView.setImageBitmap(editimage)


        // 初始化裁剪图片的ActivityResultLauncher
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // 获取裁剪后的图片Uri
                val uriContent = result.uriContent
                // 将裁剪后的图片设置到ImageView
                imageView.setImageURI(uriContent)
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uriContent)
                editimage = bitmap
            } else {
                // 处理错误情况
                val exception = result.error
                exception?.printStackTrace()
            }




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
        findViewById<Button>(R.id.btndraw).setOnClickListener {
            editimage?.let {
                val intent = Intent(this, MyPaintToolsActivity::class.java).apply {
                    putExtra("image", it)
                }
                startActivityForResult(intent, DRAW_REQUEST_CODE)
            }
        }
        findViewById<Button>(R.id.btncolorize).setOnClickListener {
            editimage?.let {
                val intent = Intent(this, ImageAiEditing4colorizeActivity::class.java).apply {
                    putExtra("image", it)
                }
                startActivityForResult(intent, COLERIZED_REQUEST_CODE)
            }
        }
        findViewById<Button>(R.id.btnfilter).setOnClickListener {
            editimage?.let {
                val intent = Intent(this, FilterImageActivity::class.java).apply {
                    putExtra("image", it)
                }
                startActivityForResult(intent, FILTER_REQUEST_CODE)
            }
        }
        findViewById<Button>(R.id.btncut).setOnClickListener {
            // 启动裁剪界面
            editimage?.let {
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DRAW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 检查是否返回了编辑后的图像
            val editedImage = data?.getParcelableExtra<Bitmap>("edited_image")
            // 在这里处理编辑后的图像
            editedImage?.let {
                // 将编辑后的图像设置到ImageView或者进行其他操作
                imageView.setImageBitmap(it)
                editimage = it
            }
        }
        if (requestCode == TEXT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 检查是否返回了编辑后的图像
            val editedImage = data?.getParcelableExtra<Bitmap>("edited_image")
            // 在这里处理编辑后的图像
            editedImage?.let {
                // 将编辑后的图像设置到ImageView或者进行其他操作
                imageView.setImageBitmap(it)
                editimage = it
            }
        }
        if (requestCode == FILTER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 检查是否返回了编辑后的图像
            val editedImage = data?.getParcelableExtra<Bitmap>("edited_image")
            // 在这里处理编辑后的图像
            editedImage?.let {
                // 将编辑后的图像设置到ImageView或者进行其他操作
                imageView.setImageBitmap(it)
                editimage = it
            }
        }
        if (requestCode == COLERIZED_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 检查是否返回了编辑后的图像
            val editedImage = data?.getParcelableExtra<Bitmap>("processedImage")
            // 在这里处理编辑后的图像
            editedImage?.let {
                // 将编辑后的图像设置到ImageView或者进行其他操作
                imageView.setImageBitmap(it)
                editimage = it
            }
        }
    }

}
