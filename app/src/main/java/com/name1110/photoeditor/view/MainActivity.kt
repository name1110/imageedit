package com.name1110.photoeditor.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.name1110.photoeditor.R
import com.name1110.photoeditor.adapter.MyImagesAdapter
import com.name1110.photoeditor.databinding.ActivityMainBinding
import com.name1110.photoeditor.viewmodel.MyImagesViewModel




class MainActivity : AppCompatActivity() {
    lateinit var myImagesViewModel: MyImagesViewModel
    lateinit var mainBinding: ActivityMainBinding
    lateinit var myImagesAdapter: MyImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化视图绑定
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        //实例化ViewModel
        myImagesViewModel = ViewModelProvider(this)[MyImagesViewModel::class.java]

        //初始化RecyclerView
        mainBinding.recyclerView.layoutManager = GridLayoutManager(this,2)
        myImagesAdapter = MyImagesAdapter(this)
        mainBinding.recyclerView.adapter = myImagesAdapter

        //显示所有的列表
        myImagesViewModel.getAllImages().observe(this) { images ->
            myImagesAdapter.setImage(images)
        }

        mainBinding.floatingActionButton.setOnClickListener {
            //显示“收藏图片”Activity
            val intent = Intent(this, AddImageActivity::class.java)
            startActivity(intent)
        }

        //给RecyclerView添加手势支持
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }
            //当用户用手指左右滑动时，删除指定的图片
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myImagesViewModel.delete(
                    myImagesAdapter.returnItemGivenPosition(
                        viewHolder.adapterPosition
                    )
                )
            }
        }).attachToRecyclerView(mainBinding.recyclerView)
    }
}