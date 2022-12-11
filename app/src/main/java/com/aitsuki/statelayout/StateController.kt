package com.aitsuki.statelayout

import android.view.View
import androidx.annotation.LayoutRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aitsuki.widget.StateLayout

class StateController(
    private val stateLayout: StateLayout,
    private val refreshLayout: SwipeRefreshLayout? = null,
    private val configStrategy: ConfigStrategy
) {
    init {
        refreshLayout?.setOnRefreshListener { configStrategy.onLoading() }
    }

    var currentState = State.IDLE
        private set
    val isLoading = currentState == State.LOADING

    fun showContent() {
        currentState = State.IDLE
        updateRefreshState(false)
        if (configStrategy.hasData(currentState) || configStrategy.emptyLayoutResId == 0) {
            stateLayout.showContent()
        } else {
            val emptyView = stateLayout.showState(configStrategy.emptyLayoutResId)
            setReloadClickListener(emptyView)
        }
    }

    fun showLoading() {
        currentState = State.LOADING
        if (refreshLayout != null && configStrategy.hasData(currentState)) {
            updateRefreshState(true)
        } else if (configStrategy.loadingLayoutResId != 0) {
            updateRefreshState(false)
            stateLayout.showState(configStrategy.loadingLayoutResId)
        }
    }

    fun showError() {
        currentState = State.ERROR
        updateRefreshState(false)
        if (configStrategy.hasData(currentState)) {
            stateLayout.showContent()
        } else {
            val errorView = stateLayout.showState(configStrategy.errorLayoutResId)
            setReloadClickListener(errorView)
        }
    }

    private fun setReloadClickListener(view: View) {
        for (clickToReloadId in configStrategy.clickToReloadIds) {
            view.findViewById<View>(clickToReloadId)?.setOnClickListener {
                configStrategy.onLoading()
            }
        }
    }

    private fun updateRefreshState(isRefreshing: Boolean) {
        refreshLayout?.isRefreshing = isRefreshing
        refreshLayout?.isEnabled = configStrategy.swipeRefreshEnabled.contains(currentState)
    }

    abstract class ConfigStrategy {
        @LayoutRes
        open val loadingLayoutResId: Int = 0

        @LayoutRes
        open val errorLayoutResId: Int = 0

        @LayoutRes
        open val emptyLayoutResId: Int = 0

        open val clickToReloadIds: IntArray = intArrayOf()

        open val swipeRefreshEnabled: Array<State> = arrayOf(State.IDLE, State.EMPTY)

        abstract fun hasData(state: State): Boolean

        abstract fun onLoading()
    }

    enum class State {
        IDLE, LOADING, ERROR, EMPTY
    }
}