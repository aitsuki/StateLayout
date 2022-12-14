package com.aitsuki.statelayout

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aitsuki.statelayout.databinding.ActivityStateRefreshListBinding
import com.aitsuki.statelayout.helper.StateListHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

class StateRefreshListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStateRefreshListBinding

    private val adapter = TodoAdapter()
    private val stateListHelper by lazy {
        StateListHelper(binding.stateLayout, binding.refreshLayout, R.layout.layout_state_custom) {
            adapter.itemCount > 0
        }
    }
    private var dataSource: TodoPagingSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateRefreshListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setOnMenuItemClickListener {
            adapter.submitData(lifecycle, PagingData.empty())
            dataSource?.invalidate()
            return@setOnMenuItemClickListener true
        }

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.recyclerView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener { adapter.refresh() }
        binding.stateLayout.setOnRetryClickListener { adapter.refresh() }

        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> stateListHelper.showContentOrEmpty()
                LoadState.Loading -> stateListHelper.showLoadingOrRefreshing()
                is LoadState.Error -> stateListHelper.showErrorOrContent()
            }
        }

        lifecycleScope.launchWhenCreated {
            Pager(config = PagingConfig(20)) {
                TodoPagingSource().also { dataSource = it }
            }.flow.collect { adapter.submitData(it) }
        }
    }
}

class TodoPagingSource : PagingSource<Int, String>() {

    companion object {
        private var loadTime = 0
    }

    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        Log.d("getRefreshKey", "state: $state")
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        delay(1000)
        loadTime++
        if (params.key == null && loadTime % 3 < 2) {
            return if (loadTime % 2 == 0) {
                LoadResult.Error(RuntimeException("Fake error"))
            } else {
                LoadResult.Page(emptyList(), null, null)
            }
        }
        val key = params.key ?: 1
        val data = (key until key + params.loadSize).map { it.toString() }
        return LoadResult.Page(data, params.key, key + params.loadSize + 1)
    }
}

class TodoAdapter : PagingDataAdapter<String, TodoViewHolder>(TodoDiff) {
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.textView.text = getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val textView = TextView(parent.context)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setPadding(48)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        return TodoViewHolder(textView)
    }
}

class TodoViewHolder(val textView: TextView) : ViewHolder(textView)

object TodoDiff : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}