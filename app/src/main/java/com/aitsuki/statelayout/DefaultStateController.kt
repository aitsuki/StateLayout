package com.aitsuki.statelayout

import com.aitsuki.widget.StateLayout

fun defaultBuilder(stateLayout: StateLayout): StateController.Builder {
    return StateController.Builder(stateLayout)
        .setLoading(R.layout.state_loading)
        .setError(R.layout.state_error)
        .setEmpty(R.layout.state_empty_1)
        .setClickToReloadIds(R.id.retry)
}