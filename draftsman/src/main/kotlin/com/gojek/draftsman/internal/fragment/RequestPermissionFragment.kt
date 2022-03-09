package com.gojek.draftsman.internal.fragment

import androidx.fragment.app.Fragment

private const val REQUEST_CODE = 1100

internal object RequestPermissionFragment : Fragment() {

    private var resultCallback: (Int) -> Unit = {}

    fun requestPermission(permission: String, callback: (Int) -> Unit) {
        resultCallback = callback
        requestPermissions(arrayOf(permission), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        resultCallback(grantResults[0])
    }
}