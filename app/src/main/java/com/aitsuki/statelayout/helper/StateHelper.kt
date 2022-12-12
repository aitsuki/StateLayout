package com.aitsuki.statelayout.helper

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aitsuki.widget.StateLayout

class StateHelper(
    private val stateLayout: StateLayout,
    private val refreshLayout: SwipeRefreshLayout,
    private val hasData: () -> Boolean,
) {
    fun showContentOrEmpty() {
        refreshLayout.isRefreshing = false
        if (hasData()) {
            refreshLayout.isEnabled = true
            stateLayout.showContent()
        } else {
            refreshLayout.isEnabled = false
            stateLayout.showEmpty()
        }
    }

    fun showContentOrError() {
        refreshLayout.isRefreshing = false
        if (hasData()) {
            refreshLayout.isEnabled = true
            stateLayout.showContent()
        } else {
            refreshLayout.isEnabled = false
            stateLayout.showError()
        }
    }

    fun showLoadingOrRefreshing() {
        if (hasData()) {
            refreshLayout.isRefreshing = true
            refreshLayout.isEnabled = true
        } else {
            refreshLayout.isRefreshing = false
            refreshLayout.isEnabled = false
            stateLayout.showLoading()
        }
    }
}