package com.aitsuki.statelayout

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aitsuki.statelayout.databinding.ActivityStateRefreshBinding
import com.aitsuki.statelayout.helper.StateHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

class StateRefreshActivity : AppCompatActivity() {

    companion object {
        private val firstWords = arrayOf("White", "Black", "Blue", "Yellow", "Pink")
        private val secondWords = arrayOf("Dogs", "Cats", "Ducks", "Birds")
    }

    private lateinit var binding: ActivityStateRefreshBinding

    private var dataCache: String? = null
    private val stateHelper by lazy {
        StateHelper(binding.stateLayout, binding.refreshLayout) { dataCache != null }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateRefreshBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.clear_cache) {
                dataCache = null
                Toast.makeText(this, "Cache has been clear, please refresh.", Toast.LENGTH_LONG)
                    .show()
            }
            return@setOnMenuItemClickListener true
        }
        binding.refreshLayout.setOnRefreshListener { refreshData() }
        binding.stateLayout.setOnRetryClickListener { refreshData() }
        refreshData()
    }

    private fun refreshData() {
        lifecycleScope.launchWhenCreated {
            stateHelper.showLoadingOrRefreshing()
            val result = fetchData()
            if (result.isSuccess) {
                binding.contentText.text = result.getOrThrow()
                dataCache = result.getOrThrow()
                stateHelper.showContentOrEmpty()
            } else {
                result.exceptionOrNull()?.message?.let {
                    Toast.makeText(this@StateRefreshActivity, it, Toast.LENGTH_LONG).show()
                }
                stateHelper.showContentOrError()
            }
        }
    }

    suspend fun fetchData(): Result<String> {
        delay(3000)
        return if (Random.nextBoolean()) {
            Result.success(firstWords.random() + " " + secondWords.random())
        } else {
            Result.failure(RuntimeException("Something went wrong"))
        }
    }
}