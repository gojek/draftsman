package com.gojek.draftsman.internal.drawer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.gojek.draftsman.internal.Config
import com.gojek.draftsman.internal.DraftsmanCallback
import com.gojek.draftsman.internal.DraftsmanLayout
import com.gojek.draftsman.internal.ViewGroupCallback
import com.gojek.draftsman.internal.constants.*
import com.gojek.draftsman.internal.fragment.FileChooserFragment
import com.gojek.draftsman.internal.fragment.RequestPermissionFragment
import com.gojek.draftsman.internal.model.DrawerItem
import com.gojek.draftsman.internal.model.DrawerRangeItem
import com.gojek.draftsman.internal.model.DrawerSelectionItem
import com.gojek.draftsman.internal.model.DrawerToggleItem
import com.gojek.draftsman.internal.visualizers.GridVisualizer
import com.gojek.draftsman.internal.visualizers.OverlayVisualizer
import com.gojek.draftsman.internal.widgets.InspectionRootItemView

internal class DrawerOptionHandler(
    private val context: Context,
    private val viewGroupCallback: ViewGroupCallback,
    private val draftsmanCallback: DraftsmanCallback
) {

    var adapter: DrawerAdapter? = null

    private var gridVisualizer: GridVisualizer? = null

    private var overlayVisualizer: OverlayVisualizer? = null

    private var handler: Handler? = null

    fun getDrawerInteractions() = object : DrawerInteractions {

        override fun onSelectItem(item: DrawerSelectionItem) {
            onSelectItemInternal(item)
        }

        override fun onToggleChange(item: DrawerToggleItem, isChecked: Boolean) {
            onToggleChangeInternal(item, isChecked)
        }

        override fun onRangeChange(item: DrawerRangeItem, value: Int) {
            onRangeChangeInternal(item, value)
        }
    }

    fun cleanUp() {
        removeOverlay()
    }

    private fun onToggleChangeInternal(
        item: DrawerToggleItem,
        isChecked: Boolean
    ) {
        when (item.toggleId) {
            MARGIN_VISUALIZER -> updateGlobalConfig { Config.marginEnabled = isChecked }
            PADDING_VISUALIZER -> updateGlobalConfig { Config.paddingEnabled = isChecked }
            GRID_VISUALIZER -> toggleGrid(isChecked)
        }
    }

    private fun updateGlobalConfig(update: () -> Unit) {
        update()
        viewGroupCallback.invalidate()
    }

    private fun toggleGrid(isEnabled: Boolean) {
        Config.gridEnabled = isEnabled
        if (isEnabled) {
            if (null == gridVisualizer) {
                gridVisualizer = GridVisualizer(context).apply {
                    layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
                viewGroupCallback.addView(0, gridVisualizer)
            }

            val gridSizeRangeItem = DrawerRangeItem("Grid size", GRID_SIZE_SEEKER, 2, 8)
            val index = adapter?.getList()
                ?.indexOfFirst { it is DrawerToggleItem && it.toggleId == GRID_VISUALIZER }
                ?: 0
            adapter?.apply {
                getList().add(index + 1, gridSizeRangeItem)
                notifyItemRangeChanged(index + 1, getList().size - 1)
            }
        } else {
            val index = adapter?.getList()
                ?.indexOfFirst { it is DrawerRangeItem && it.rangeId == GRID_SIZE_SEEKER }
                ?: 0
            adapter?.apply {
                getList().removeAt(index)
                notifyItemRemoved(getList().lastIndex + 1)
                notifyItemRangeChanged(index, getList().lastIndex)
            }

        }
        gridVisualizer?.visibility = if (isEnabled) VISIBLE else GONE
    }

    private fun onRangeChangeInternal(item: DrawerRangeItem, value: Int) {
        when (item.rangeId) {
            GRID_SIZE_SEEKER -> gridVisualizer?.updateGridSize(value)
        }
    }

    private fun onSelectItemInternal(item: DrawerSelectionItem) {
        when (item.selectionId) {
            INSPECTION_ROOT_SELECTION -> showInspectionRootDialog()
            OVERLAY_SELECTION -> selectOverlay()
        }
    }

    private fun showInspectionRootDialog() {
        var alertDialog: AlertDialog? = null
        val rootView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        val root = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
        for (index in 0 until root.childCount) {
            val child = root.getChildAt(index)
            if (child !is DraftsmanLayout) {
                rootView.addView(InspectionRootItemView(context).apply {
                    setText(child.javaClass.simpleName)
                    setOnClickListener {
                        onClickView(child, index)
                        alertDialog?.dismiss()
                    }
                })
            }
        }
        alertDialog = AlertDialog.Builder(context)
            .setTitle("Select inspection root")
            .setView(rootView)
            .create().apply { show() }
    }

    private fun selectOverlay() {
        val storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        draftsmanCallback.closeDrawer()
        if (hasPermission(storagePermission)) {
            selectImage()
        } else {
            requestPermission(storagePermission) {
                if (it == PermissionChecker.PERMISSION_GRANTED) {
                    selectImage()
                } else if (!shouldShowPermissionRationale(storagePermission)) {
                    Toast.makeText(
                        context,
                        "Need storage permission to place overlay",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun onClickView(child: View, index: Int) {
        draftsmanCallback.run {
            closeDrawer()
            changeRootView(child)
        }
        val name = if (index == 0) {
            "Activity Layout"
        } else {
            child.javaClass.simpleName
        }

        val item =
            findDrawerItem<DrawerSelectionItem> { it is DrawerSelectionItem && it.selectionId == INSPECTION_ROOT_SELECTION }
        val index = adapter?.getList()?.indexOf(item) ?: -1
        adapter?.updateItemAtPosition(item.copy(selection = name), index)
    }

    private fun <T : DrawerItem> findDrawerItem(predicate: (DrawerItem) -> Boolean): T {
        return adapter?.getList()?.find { predicate(it) } as T
    }

    private fun hasPermission(permission: String): Boolean {
        return PermissionChecker.checkSelfPermission(
            context,
            permission
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, callback: (Int) -> Unit) {
        if (!RequestPermissionFragment.isAdded) {
            val activity = context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction().apply {
                replace(android.R.id.content, RequestPermissionFragment)
                commitNow()
            }
        }
        RequestPermissionFragment.requestPermission(permission, callback)
    }

    private fun shouldShowPermissionRationale(permission: String): Boolean {
        val activity = context as AppCompatActivity
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    private fun selectImage() {
        if (!FileChooserFragment.isAdded) {
            val activity = context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction().apply {
                replace(android.R.id.content, FileChooserFragment)
                commitNow()
            }
        }
        FileChooserFragment.selectFile {
            if (null != it) {
                showImage(it)
            }
        }
    }

    private fun showImage(uri: Uri) {
        if (null == handler) {
            val handlerThread =
                HandlerThread(DrawerOptionHandler::class.java.simpleName).apply { start() }
            handler = Handler(handlerThread.looper)
        }
        if (null == overlayVisualizer) {
            overlayVisualizer = OverlayVisualizer(context) { removeOverlay() }.apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                viewGroupCallback.addView(0, this)
            }
        }
        handler?.post {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            if (null != bitmap) {
                resizeBitmap(bitmap)
            }
        }
    }

    private fun resizeBitmap(bitmap: Bitmap) {
        val container = (context as Activity).findViewById<View>(android.R.id.content)
        val size = getBitmapSize(bitmap, container)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, size.first, size.second, true)
        overlayVisualizer?.setOverlayImage(resizedBitmap)
    }

    private fun getBitmapSize(bitmap: Bitmap, container: View): Pair<Int, Int> {
        var pair = Pair(0, 0)
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        if (bitmap.width >= bitmap.height) {
            pair = if (bitmap.width >= container.width) {
                val height = container.width * aspectRatio
                Pair(container.width, height.toInt())

            } else {
                Pair(bitmap.width, bitmap.height)
            }
        } else if (bitmap.height > bitmap.width) {
            pair = if (bitmap.height >= container.height) {
                val width = container.height * aspectRatio
                Pair(width.toInt(), container.height)
            } else {
                Pair(bitmap.width, bitmap.height)
            }
        }
        return pair
    }

    private fun removeOverlay() {
        viewGroupCallback.removeView(overlayVisualizer)
        (context as AppCompatActivity).supportFragmentManager.popBackStack()
    }
}
