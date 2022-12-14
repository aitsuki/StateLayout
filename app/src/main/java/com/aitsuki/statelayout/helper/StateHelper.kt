package com.aitsuki.statelayout.helper

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aitsuki.widget.StateLayout

class StateHelper(
    private val stateLayout: StateLayout,
    private val refreshLayout: SwipeRefreshLayout,
    private val hasCache: () -> Boolean,
) {
    var isRefreshing = false
        private set

    fun showLoadingOrRefreshing() {
        isRefreshing = true
        if (hasCache()) {
            refreshLayout.isRefreshing = true
            refreshLayout.isEnabled = true
        } else {
            refreshLayout.isRefreshing = false
            refreshLayout.isEnabled = false
            stateLayout.showLoading()
        }
    }

    fun showContent() {
        isRefreshing = false
        refreshLayout.isRefreshing = false
        refreshLayout.isEnabled = true
        stateLayout.showContent()
    }

    fun showErrorOrContent() {
        isRefreshing = false
        refreshLayout.isRefreshing = false
        if (hasCache()) {
            refreshLayout.isEnabled = true
            stateLayout.showContent()
        } else {
            refreshLayout.isEnabled = false
            stateLayout.showError()
        }
    }
}