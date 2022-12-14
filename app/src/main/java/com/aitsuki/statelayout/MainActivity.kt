package com.aitsuki.statelayout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aitsuki.statelayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 展示各个状态的切换
        binding.state.setOnClickListener {
            startActivity(Intent(this, StateActivity::class.java))
        }

        // SwipeRefreshLayout 与 StateLayout 协作
        binding.stateRefresh.setOnClickListener {
            startActivity(Intent(this, StateRefreshActivity::class.java))
        }

        // SwipeRefreshLayout + StateLayout + Paging3 协作
        binding.stateRefreshPaging.setOnClickListener {
            startActivity(Intent(this, StateRefreshListActivity::class.java))
        }
    }
}