package com.xxxxxxh.update

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weeboos.permissionlib.PermissionRequest
import com.example.weeboos.permissionlib.PermissionUtils
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    val permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        PermissionRequest.getInstance().build(this)
            .requestPermission(object : PermissionRequest.PermissionListener {
                override fun permissionGranted() {
                    Toast.makeText(this@BaseActivity, "permissionGranted", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun permissionDenied(permissions: ArrayList<String>?) {
                    Toast.makeText(this@BaseActivity, "permissionDenied", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun permissionNeverAsk(permissions: ArrayList<String>?) {
                    Toast.makeText(this@BaseActivity, "permissionNeverAsk", Toast.LENGTH_SHORT)
                        .show()
                    PermissionUtils.showAlertDialog(
                        this@BaseActivity,
                        PermissionUtils.translateArrayString(permissions)
                    )
                }

            }, permission)
    }


    abstract fun getLayout(): Int
}