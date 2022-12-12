package com.aitsuki.statelayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aitsuki.statelayout.databinding.ActivityStateBinding
import com.aitsuki.widget.StateLayout

class StateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.state_content -> binding.stateLayout.showContent()
                R.id.state_loading -> binding.stateLayout.showLoading()
                R.id.state_empty -> binding.stateLayout.showEmpty()
                R.id.state_error -> binding.stateLayout.showError()
                R.id.state_custom -> binding.stateLayout.showCustom(
                    StateLayout.State.EMPTY,
                    R.layout.layout_state_custom
                )
            }
            return@setOnMenuItemClickListener true
        }
        binding.stateLayout.setOnRetryClickListener {
            binding.stateLayout.showLoading()
        }
    }
}