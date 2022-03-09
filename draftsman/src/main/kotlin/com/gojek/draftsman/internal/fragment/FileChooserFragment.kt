package com.gojek.draftsman.internal.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

private const val REQUEST_CODE = 1101

internal object FileChooserFragment : Fragment() {

    private var resultCallback: (Uri?) -> Unit = {}

    fun selectFile(callback: (Uri?) -> Unit) {
        resultCallback = callback
        startActivityForResult(
            Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
            REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && null != data) {
            resultCallback(data.data)
        }
    }
}
