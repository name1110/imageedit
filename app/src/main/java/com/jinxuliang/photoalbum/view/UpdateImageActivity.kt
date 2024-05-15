package com.jinxuliang.photoalbum.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.jinxuliang.photoalbum.R
import com.jinxuliang.photoalbum.databinding.ActivityUpdateImageBinding
import com.jinxuliang.photoalbum.model.MyImages
import com.jinxuliang.photoalbum.util.ConvertImage
import com.jinxuliang.photoalbum.viewmodel.MyImagesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateImageActivity : AppCompatActivity() {
    lateinit var updateImageBinding: ActivityUpdateImageBinding
    var id = -1
    var imageAsString = ""
    lateinit var viewModel: MyImagesViewModel
    lateinit var activityResultLauncherForSelectImage: ActivityResultLauncher<Intent>
    lateinit var selectedImage:Bitmap
    var control=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateImageBinding = ActivityUpdateImageBinding.inflate(layoutInflater)
        setContentView(updateImageBinding.root)

        viewModel = ViewModelProvider(this)[MyImagesViewModel::class.java]

        getAndSetData()
        registerActivityForSelectImage()
        updateImageBinding.imageViewUpdateImage.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncherForSelectImage.launch(intent)
        }

        updateImageBinding.btnUpdate.setOnClickListener {
            updateImageBinding.btnUpdate.text = "正在保存……"
            updateImageBinding.btnUpdate.isEnabled = false

            GlobalScope.launch(Dispatchers.IO) {
                val updatedTitle=updateImageBinding.edtUpdateTitle.text.toString()
                val updatedDescription=updateImageBinding.edtUpdateDescription.text.toString()
                if(control){
                    val newImageAsString=ConvertImage.convertToString(selectedImage)
                    if(newImageAsString!=null){
                        imageAsString=newImageAsString
                    }else{
                        Toast.makeText(applicationContext,"出错了",Toast.LENGTH_SHORT).show()
                    }
                }

                val myUpdatedImage=MyImages(updatedTitle,updatedDescription,imageAsString)
                myUpdatedImage.imageId=id
                viewModel.update(myUpdatedImage)
                finish()
            }


        }

        updateImageBinding.toolbarUpdateImage.setNavigationOnClickListener {
            finish()
        }
    }

    fun getAndSetData() {
        id = intent.getIntExtra("id", -1)
        if (id != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val myImage = viewModel.getItemById(id)
                withContext(Dispatchers.Main) {
                    updateImageBinding.edtUpdateTitle.setText(myImage.imageTitle)
                    updateImageBinding.edtUpdateDescription.setText(myImage.imageDescription)
                    imageAsString = myImage.imageAsString
                    var imageAsBitmap = ConvertImage.convertToBitmap(imageAsString)
                    updateImageBinding.imageViewUpdateImage.setImageBitmap(imageAsBitmap)
                }
            }

        }
    }

    fun registerActivityForSelectImage() {
        activityResultLauncherForSelectImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val resultCode = result.resultCode
                val imageData = result.data
                if (resultCode == RESULT_OK && imageData != null) {

                    val imageUri = imageData.data

                    imageUri?.let {
                        selectedImage = if (Build.VERSION.SDK_INT >= 28) {
                            val imageSource =
                                ImageDecoder.createSource(this.contentResolver, imageUri)
                            ImageDecoder.decodeBitmap(imageSource)
                        } else {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        }
                        updateImageBinding.imageViewUpdateImage.setImageBitmap(selectedImage)
                        control = true

                    }

                }

            }
    }
}