package com.gojek.draftsman.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gojek.draftsman.Draftsman
import kotlinx.android.synthetic.main.launch_activity_layout.*

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.launch_activity_layout)

        enable.setOnClickListener {
            Draftsman.install(this)
            Toast.makeText(this, "Added Draftsman overlay", Toast.LENGTH_SHORT).show()
        }

        disable.setOnClickListener {
            Draftsman.uninstall(this)
            Toast.makeText(this, "Removed Draftsman overlay", Toast.LENGTH_SHORT).show()
        }
    }
}
