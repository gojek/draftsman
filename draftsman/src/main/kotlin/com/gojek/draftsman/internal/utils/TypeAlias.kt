package com.gojek.draftsman.internal.utils

import android.view.View

internal typealias DimensionConfigChange = (Boolean) -> Unit

internal typealias OnLayeredViewSelection = (View) -> Unit

internal typealias ExitListener = () -> Unit

internal typealias InfoDismissListener = () -> Unit