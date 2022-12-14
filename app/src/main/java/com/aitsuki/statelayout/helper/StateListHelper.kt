package com.aitsuki.statelayout.helper


import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aitsuki.widget.StateLayout

class StateListHelper(
    private val stateLayout: StateLayout,
    private val refreshLayout: SwipeRefreshLayout,
    private val customEmptyLayoutId: Int = 0,
    private val customEmptyView: View? = null,
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

    fun showContentOrEmpty() {
        isRefreshing = false
        refreshLayout.isRefreshing = false
        refreshLayout.isEnabled = true
        if (hasCache()) {
            stateLayout.showContent()
        } else {
            if (customEmptyView != null) {
                stateLayout.showCustom(StateLayout.State.EMPTY, customEmptyView)
            } else if (customEmptyLayoutId != 0) {
                stateLayout.showCustom(StateLayout.State.EMPTY, customEmptyLayoutId)
            } else {
                stateLayout.showEmpty()
            }
        }
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