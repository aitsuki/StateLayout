package com.aitsuki.statelayout.widget

import com.aitsuki.statelayout.R
import com.aitsuki.widget.StateLayout

fun defaultBuilder(stateLayout: StateLayout): StateController.Builder {
    return StateController.Builder(stateLayout)
//        .setLoading(R.layout.layout_state_loading)
        .setError(R.layout.layout_state_custom)
        .setClickToReloadIds(R.id.retry)
}