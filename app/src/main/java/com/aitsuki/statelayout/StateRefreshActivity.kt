package com.aitsuki.statelayout

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aitsuki.statelayout.databinding.ActivityStateRefreshBinding
import com.aitsuki.statelayout.helper.StateHelper
import kotlinx.coroutines.delay

class StateRefreshActivity : AppCompatActivity() {

    companion object {
        private val firstWords =
            arrayOf("White", "Black", "Blue", "Yellow", "Pink", "Green", "Orange", "Red")
        private val secondWords = arrayOf("Dogs", "Cats", "Ducks", "Birds", "Pigs", "Cows", "Sheep")
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
        binding.toolbar.setOnMenuItemClickListener {
            dataCache = null
            refreshData()
            return@setOnMenuItemClickListener true
        }
        binding.refreshLayout.setOnRefreshListener { refreshData() }
        binding.stateLayout.setOnRetryClickListener { refreshData() }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        if (stateHelper.isRefreshing) return
        lifecycleScope.launchWhenCreated {
            stateHelper.showLoadingOrRefreshing()
            val result = fetchData()
            if (result.isSuccess) {
                binding.contentText.text = result.getOrThrow()
                dataCache = result.getOrThrow()
                stateHelper.showContent()
            } else {
                result.exceptionOrNull()?.message?.let {
                    Toast.makeText(this@StateRefreshActivity, it, Toast.LENGTH_LONG).show()
                }
                stateHelper.showErrorOrContent()
            }
        }
    }

    private var fetchCount = 0

    private suspend fun fetchData(): Result<String> {
        delay(1000)
        fetchCount++
        return if (fetchCount % 2 == 0) {
            Result.failure(RuntimeException("Something went wrong"))
        } else {
            Result.success(firstWords.random() + " " + secondWords.random())
        }
    }
}