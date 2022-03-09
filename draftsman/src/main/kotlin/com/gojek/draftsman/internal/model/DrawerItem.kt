package com.gojek.draftsman.internal.model

sealed class DrawerItem

internal class DrawerHeadingItem(val title: String) : DrawerItem()

internal class DrawerToggleItem(
    val icon: Int,
    val title: String,
    val toggleId: Int,
    val enabled: Boolean
) : DrawerItem()

internal data class DrawerRangeItem(
    val title: String,
    val rangeId: Int,
    val minValue: Int,
    val maxValue: Int,
    val enabled: Boolean = false
) : DrawerItem()

internal data class DrawerSelectionItem(
    val icon: Int,
    val selection: String,
    val selectionId: Int
) : DrawerItem()