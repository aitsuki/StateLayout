package com.aitsuki.widget

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children

class StateLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    companion object {
        var loadingLayout = R.layout.layout_state_loading
        var errorLayout = R.layout.layout_state_error
        var emptyLayout = R.layout.layout_state_empty
        var retryIds = intArrayOf(R.id.retry)
    }

    private var cachedViews = SparseArray<View?>()
    private var retryClickListener: OnClickListener? = null
    private var contentView: View? = null
    var currentState = State.CONTENT
        private set

    fun setOnRetryClickListener(listener: OnClickListener?) {
        this.retryClickListener = listener
    }

    fun setContentView(view: View?) {
        this.contentView = view
    }

    fun showContent() {
        currentState = State.CONTENT
        val view = checkNotNull(contentView) { "No content view" }
        showStateView(view)
    }

    fun showLoading() {
        currentState = State.LOADING
        showStateView(R.layout.layout_state_loading)
    }

    fun showError() {
        currentState = State.ERROR
        showStateView(R.layout.layout_state_error)
    }

    fun showEmpty() {
        currentState = State.EMPTY
        showStateView(R.layout.layout_state_empty)
    }

    fun showCustom(state: State, @LayoutRes layoutId: Int) {
        currentState = state
        showStateView(layoutId)
    }

    fun showCustom(state: State, view: View) {
        currentState = state
        showStateView(view)
    }

    private fun showStateView(@LayoutRes layoutId: Int): View {
        var view = cachedViews[layoutId]
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutId, this, false)
            cachedViews[layoutId] = view
        }
        return showStateView(view!!)
    }

    private fun showStateView(view: View): View {
        ensureViewAdded(view)
        for (child in children) {
            if (child != view) {
                // contentView 使用 INVISIBLE 可以避免很多奇怪的 UI 问题
                child.visibility = if (child == contentView) View.INVISIBLE else View.GONE
            }
        }
        if (view != contentView) {
            for (retryId in retryIds) {
                view.findViewById<View>(retryId)?.setOnClickListener(retryClickListener)
            }
        }
        view.visibility = View.VISIBLE
        return view
    }

    private fun ensureViewAdded(view: View) {
        if (!children.contains(view)) {
            addView(view)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        var lp = params ?: child.layoutParams ?: generateDefaultLayoutParams()
        if (lp !is LayoutParams) {
            lp = LayoutParams(lp)
        }
        lp.topToTop = LayoutParams.PARENT_ID
        lp.leftToLeft = LayoutParams.PARENT_ID
        lp.rightToRight = LayoutParams.PARENT_ID
        lp.bottomToBottom = LayoutParams.PARENT_ID
        super.addView(child, index, params)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            error("StateLayout can host only one content view")
        } else if (childCount == 1) {
            setContentView(getChildAt(0))
        }
    }

    enum class State {
        CONTENT, EMPTY, LOADING, ERROR
    }
}