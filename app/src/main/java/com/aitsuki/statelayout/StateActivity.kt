package com.aitsuki.statelayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aitsuki.statelayout.databinding.ActivityStateBinding

class StateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStateBinding

    private val stateController by lazy {
        defaultBuilder(binding.stateLayout).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}