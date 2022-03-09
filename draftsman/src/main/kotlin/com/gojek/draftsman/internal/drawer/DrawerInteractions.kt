package com.gojek.draftsman.internal.drawer

import com.gojek.draftsman.internal.model.DrawerRangeItem
import com.gojek.draftsman.internal.model.DrawerSelectionItem
import com.gojek.draftsman.internal.model.DrawerToggleItem

internal interface DrawerInteractions {

    fun onSelectItem(item: DrawerSelectionItem)

    fun onToggleChange(item: DrawerToggleItem, isChecked: Boolean)

    fun onRangeChange(item: DrawerRangeItem, value: Int)
}