package com.gojek.draftsman.internal.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.gojek.draftsman.R
import com.gojek.draftsman.internal.extensions.doOnProgressChange
import com.gojek.draftsman.internal.model.*
import com.gojek.draftsman.internal.utils.buildGridSize
import kotlin.math.max

private const val VIEW_TYPE_HEADER = 1

private const val VIEW_TYPE_TOGGLE_ITEM = 2

private const val VIEW_TYPE_RANGE_ITEM = 3

private const val VIEW_TYPE_SELECTION_ITEM = 4

internal class DrawerAdapter(
    private val interactions: DrawerInteractions
) : RecyclerView.Adapter<DrawerAdapter.AbsViewHolder>() {

    private val items = mutableListOf<DrawerItem>()

    fun setData(data: List<DrawerItem>) {
        items.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    fun updateItemAtPosition(item: DrawerItem, position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

    fun getList() = items

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsViewHolder {
        return when (viewType) {
            VIEW_TYPE_TOGGLE_ITEM -> ToggleViewHolder(
                getView(R.layout.draftsman_drawer_item_toggle, parent)
            )
            VIEW_TYPE_RANGE_ITEM -> RangeViewHolder(
                getView(R.layout.draftsman_drawer_item_range, parent)
            )
            VIEW_TYPE_SELECTION_ITEM -> SelectionViewHolder(
                getView(R.layout.draftsman_drawer_item_selection, parent)
            )
            else -> HeaderViewHolder(
                getView(R.layout.draftsman_drawer_item_header, parent)
            )
        }
    }

    override fun onBindViewHolder(holder: AbsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DrawerHeadingItem -> VIEW_TYPE_HEADER
            is DrawerToggleItem -> VIEW_TYPE_TOGGLE_ITEM
            is DrawerRangeItem -> VIEW_TYPE_RANGE_ITEM
            is DrawerSelectionItem -> VIEW_TYPE_SELECTION_ITEM
        }
    }

    private fun getView(layoutResId: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(
            layoutResId,
            parent,
            false
        )
    }

    internal abstract inner class AbsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(drawerItem: DrawerItem)
    }

    private inner class HeaderViewHolder(
        itemView: View
    ) : AbsViewHolder(itemView) {
        override fun bind(drawerItem: DrawerItem) {
            itemView.run {
                findViewById<TextView>(R.id.drawer_item_heading).text =
                    (drawerItem as DrawerHeadingItem).title
            }
        }
    }

    private inner class ToggleViewHolder(
        itemView: View
    ) : AbsViewHolder(itemView) {
        override fun bind(drawerItem: DrawerItem) {
            val item = drawerItem as DrawerToggleItem
            itemView.run {
                findViewById<ImageView>(R.id.drawer_item_toggle_icon).setImageResource(item.icon)
                findViewById<TextView>(R.id.drawer_item_toggle_label).text = item.title
                findViewById<SwitchCompat>(R.id.drawer_item_toggle).apply {
                    isChecked = item.enabled
                    setOnCheckedChangeListener { _, isChecked ->
                        interactions.onToggleChange(
                            item,
                            isChecked
                        )
                    }
                }
            }
        }
    }

    private inner class RangeViewHolder(
        itemView: View
    ) : AbsViewHolder(itemView) {
        private val rangeText = itemView.findViewById<TextView>(R.id.drawer_item_range_value)
        override fun bind(drawerItem: DrawerItem) {
            val item = drawerItem as DrawerRangeItem
            val seekbar = itemView.findViewById<AppCompatSeekBar>(R.id.drawer_item_seekbar).apply {
                max = item.maxValue
                doOnProgressChange {
                    rangeText.text = "${buildGridSize(it + item.minValue)}"
                    interactions.onRangeChange(item, it + item.minValue)
                }
            }
            rangeText.text =
                "${buildGridSize(max(item.minValue, seekbar.progress + item.minValue))}"
            itemView.findViewById<TextView>(R.id.drawer_item_range_title).text = item.title
        }
    }

    private inner class SelectionViewHolder(
        itemView: View
    ) : AbsViewHolder(itemView) {
        override fun bind(drawerItem: DrawerItem) {
            val item = drawerItem as DrawerSelectionItem
            itemView.findViewById<ImageView>(R.id.drawer_item_selection_icon)
                .setImageResource(item.icon)
            itemView.findViewById<TextView>(R.id.drawer_item_selection).text = item.selection
            itemView.setOnClickListener { interactions.onSelectItem(item) }
        }
    }
}