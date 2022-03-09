package com.gojek.draftsman.internal.utils

import com.gojek.draftsman.R
import com.gojek.draftsman.internal.constants.*
import com.gojek.draftsman.internal.model.DrawerHeadingItem
import com.gojek.draftsman.internal.model.DrawerItem
import com.gojek.draftsman.internal.model.DrawerSelectionItem
import com.gojek.draftsman.internal.model.DrawerToggleItem

internal fun getDrawerLayoutList(): List<DrawerItem> {
    return listOf(
        DrawerHeadingItem("Toggles"),
        DrawerToggleItem(R.drawable.draftsman_ic_margin, "Margin", MARGIN_VISUALIZER, true),
        DrawerToggleItem(R.drawable.draftsman_ic_padding, "Padding", PADDING_VISUALIZER, true),
        DrawerHeadingItem("Ruler/Grid"),
        DrawerToggleItem(R.drawable.draftsman_ic_grid, "Enable", GRID_VISUALIZER, false),
        DrawerHeadingItem("Root Element"),
        DrawerSelectionItem(
            R.drawable.draftsman_ic_change_root,
            "Activity Layout",
            INSPECTION_ROOT_SELECTION
        ),
        DrawerHeadingItem("Overlay"),
        DrawerSelectionItem(R.drawable.draftsman_ic_place_overlay, "Place overlay", OVERLAY_SELECTION)
    )
}