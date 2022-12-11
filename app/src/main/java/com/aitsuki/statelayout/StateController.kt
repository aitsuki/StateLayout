package com.aitsuki.statelayout

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aitsuki.widget.StateLayout

class StateController(private val stateLayout: StateLayout) {

    private var p = Params(stateLayout)

    private constructor(p: Params) : this(p.stateLayout) {
        this.p = p
        p.swipeRefreshLayout?.setOnRefreshListener { p.onLoadingListener() }
    }

    var currentState = State.IDLE
        private set

    val isLoading = currentState == State.LOADING

    fun showContent() {
        currentState = State.IDLE
        updateRefreshState(false)
        if (p.dataExistsObserver(currentState) || p.emptyView == null) {
            stateLayout.showContent()
        } else {
            stateLayout.showState(p.emptyView!!).setReloadClickListener()
        }
    }

    fun showLoading() {
        if (p.loadingView == null) return
        currentState = State.LOADING
        if (p.swipeRefreshLayout != null && p.dataExistsObserver(currentState)) {
            updateRefreshState(true)
        } else {
            updateRefreshState(false)
            stateLayout.showState(p.loadingView!!)
        }
    }

    fun showError() {
        if (p.errorView == null) return
        currentState = State.ERROR
        updateRefreshState(false)
        if (p.dataExistsObserver(currentState)) {
            stateLayout.showContent()
        } else {
            stateLayout.showState(p.errorView!!).setReloadClickListener()
        }
    }

    private fun View.setReloadClickListener() {
        for (clickToReloadId in p.clickToReloadIds) {
            findViewById<View>(clickToReloadId)?.setOnClickListener {
                p.onLoadingListener()
            }
        }
    }

    private fun updateRefreshState(isRefreshing: Boolean) {
        p.swipeRefreshLayout?.isRefreshing = isRefreshing
        p.swipeRefreshLayout?.isEnabled = p.swipeRefreshEnabled.contains(currentState)
    }

    class Builder(stateLayout: StateLayout) {
        private val p = Params(stateLayout)
        private val inflate = fun(resId: Int) = LayoutInflater.from(p.stateLayout.context)
            .inflate(resId, p.stateLayout, false)

        fun setLoading(view: View?): Builder {
            p.loadingView = view
            return this
        }

        fun setLoading(@LayoutRes resId: Int): Builder {
            p.loadingView = inflate(resId)
            return this
        }

        fun setError(view: View?): Builder {
            p.errorView = view
            return this
        }

        fun setError(@LayoutRes resId: Int): Builder {
            p.errorView = inflate(resId)
            return this
        }

        fun setEmpty(view: View?): Builder {
            p.emptyView = view
            return this
        }

        fun setEmpty(@LayoutRes resId: Int): Builder {
            p.emptyView = inflate(resId)
            return this
        }

        fun setClickToReloadIds(vararg ids: Int): Builder {
            p.clickToReloadIds = ids
            return this
        }

        fun setSwipeRefreshLayout(
            view: SwipeRefreshLayout?,
            swipeRefreshEnabled: Array<State> = arrayOf(State.IDLE, State.EMPTY)
        ): Builder {
            p.swipeRefreshLayout = view
            p.swipeRefreshEnabled = swipeRefreshEnabled
            return this
        }

        fun setDataExistsObserver(observer: (state: State) -> Boolean): Builder {
            p.dataExistsObserver = observer
            return this
        }

        fun setOnLoadingListener(listener: () -> Unit): Builder {
            p.onLoadingListener = listener
            return this
        }

        fun build(): StateController {
            return StateController(p)
        }
    }

    private class Params(
        var stateLayout: StateLayout,
        var loadingView: View? = null,
        var errorView: View? = null,
        var emptyView: View? = null,
        var clickToReloadIds: IntArray = intArrayOf(),
        var swipeRefreshLayout: SwipeRefreshLayout? = null,
        var swipeRefreshEnabled: Array<State> = arrayOf(State.IDLE, State.EMPTY),
        var dataExistsObserver: (state: State) -> Boolean = { false },
        var onLoadingListener: () -> Unit = {}
    )

    enum class State {
        IDLE, LOADING, ERROR, EMPTY
    }
}