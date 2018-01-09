package dcom.taneja.dhruv.floater

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import dcom.taneja.dhruv.floater.service.FloaterService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val requestCode = 123
    private var serviceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStartFloater.setOnClickListener {
            if (!needsPermission()) {
                startFloater()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivityForResult(intent, requestCode)
            }
        }

        buttonStopFloater.setOnClickListener {
            if (serviceIntent != null) {
                stopService(serviceIntent)
            }
        }
    }

    private fun startFloater() {
        serviceIntent = Intent(this, FloaterService::class.java)
        startService(serviceIntent)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun needsPermission() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(applicationContext)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                startFloater()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
