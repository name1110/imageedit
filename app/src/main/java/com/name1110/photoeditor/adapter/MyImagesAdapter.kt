package com.name1110.photoeditor.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.name1110.photoeditor.databinding.ImageItemBinding
import com.name1110.photoeditor.model.MyImages
import com.name1110.photoeditor.util.ConvertImage

class MyImagesAdapter(val activity: Activity) :
    RecyclerView.Adapter<MyImagesAdapter.MyImagesViewHolder>() {

    var imageList: List<MyImages> = ArrayList()

    fun setImage(images: List<MyImages>) {
        imageList = images
        notifyDataSetChanged()
    }

    class MyImagesViewHolder(val itemBinding: ImageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
        val view = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val myImage = imageList[position]
        with(holder) {
            val imageAsBitmap = ConvertImage.convertToBitmap(myImage.imageAsString)
            itemBinding.imageView.setImageBitmap(imageAsBitmap)
            itemBinding.cardView.setOnClickListener {
            }
        }
    }

    fun returnItemGivenPosition(position: Int): MyImages {
        return imageList[position]
    }
}
