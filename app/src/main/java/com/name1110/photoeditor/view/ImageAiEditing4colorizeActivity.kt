package com.name1110.photoeditor.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.name1110.photoeditor.R
import com.name1110.photoeditor.javatool.Base64Util
import com.name1110.photoeditor.javatool.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*
import com.name1110.photoeditor.getaccesstoken.BaiduAuth
import java.io.IOException

class ImageAiEditing4colorizeActivity : AppCompatActivity() {
    private val INTERNET_PERMISSION_CODE = 1
    private lateinit var backgroundImage: Bitmap // 用于存储从 Intent 中获取的图像的成员变量
    private var processedBitmap: Bitmap? = null // 用于存储处理后的 Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_editing_image4colorize)

        backgroundImage = intent.getParcelableExtra<Bitmap>("image")!!

        val imageView: ImageView = findViewById(R.id.imageedit)
        imageView.setImageBitmap(backgroundImage)

        findViewById<Button>(R.id.btnprocess).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                performNetworkOperation4colorize(backgroundImage) { processedBitmap ->
                    // 处理完bitmap后可以在这里进行展示或者其他操作
                    // 例如：displayProcessedBitmap(processedBitmap)
                    imageView.setImageBitmap(processedBitmap)
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.INTERNET),
                    INTERNET_PERMISSION_CODE
                )
            }
        }
        findViewById<Button>(R.id.btndone).setOnClickListener {
            if (processedBitmap != null) {
                // 将处理后的位图传回上一个活动
                val resultIntent = Intent()
                resultIntent.putExtra("processedImage", processedBitmap)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "没有处理后的图像可用", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performNetworkOperation4colorize(backgroundImage) { processedBitmap ->
                    // 处理完bitmap后可以在这里进行展示或者其他操作
                    // 例如：displayProcessedBitmap(processedBitmap)
                    val imageView: ImageView = findViewById(R.id.imageedit)
                    imageView.setImageBitmap(processedBitmap)
                }
            } else {
                Toast.makeText(this, "没有网络权限，无法进行网络操作", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performNetworkOperation4colorize(backgroundImage: Bitmap, callback: (Bitmap) -> Unit) {
        lifecycleScope.launch {
            try {
                val url = "https://aip.baidubce.com/rest/2.0/image-process/v1/colourize"
                val imgStr = bitmapToBase64(backgroundImage)
                val imgParam = URLEncoder.encode(imgStr, "UTF-8")
                val param = "image=$imgParam"
                val clientid = "xxxxxxx"
                val clientsecret = "xxxxxxxxx"

                val baiduAuth = BaiduAuth(clientid, clientsecret)
                val accessToken = try {
                    baiduAuth.getAccessToken()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ImageAiEditing4colorizeActivity, "Failed to get access token", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                println("Access Token: $accessToken")

                val result = withContext(Dispatchers.IO) {
                    HttpUtil.post(url, accessToken, param)
                }

                // 处理结果中的image字段（假设返回的是JSON）
                val base64Image = extractBase64Image(result)
                if (base64Image != null) {
                    processedBitmap = base64ToBitmap(base64Image)
                    if (processedBitmap != null) {
                        callback(processedBitmap!!)
                    } else {
                        Toast.makeText(
                            this@ImageAiEditing4colorizeActivity,
                            "无法将Base64字符串转换为Bitmap",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ImageAiEditing4colorizeActivity,
                        "返回结果中没有找到base64编码的图像数据",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 处理异常情况
                Toast.makeText(this@ImageAiEditing4colorizeActivity, "网络操作失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractBase64Image(result: String): String? {
        // 假设返回的是一个JSON字符串，需要从中提取base64编码的图像数据
        // 这里假设result是一个JSON字符串，包含一个名为image的字段，值是base64编码的图片数据
        // 这里根据实际返回的JSON结构来解析
        // 假设返回的JSON格式如下：
        // {"image": "base64_encoded_string"}
        return try {
            // 解析JSON，提取image字段
            // 假设返回的是 {"image": "base64_encoded_string"}
            val jsonObject = JSONObject(result)
            jsonObject.getString("image")
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }

    private fun base64ToBitmap(base64Image: String): Bitmap? {
        val imageBytes = Base64.getDecoder().decode(base64Image)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64Util.encode(byteArray)
    }
}
