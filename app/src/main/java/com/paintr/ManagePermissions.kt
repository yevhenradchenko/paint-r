package com.paintr

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManagePermissions(private val activity: MainActivity,
                        private val PERMISSIONS: List<String>,
                        private val PERMISSIONS_REQUEST_CODE: Int) {

    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            showAlert()
        } else {
        }
    }

    fun isPermissionsGranted(): Int {
        //PERMISSION_GRANTED : Const 0
        //PERMISSION_DENIED : Const 1
        var counter = 0
        for (permission in PERMISSIONS) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }

    private fun deniedPermission(): String {
        // Looking for first denied permission
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED)
                return permission
        }
        return ""
    }

    private fun showAlert() {
        AlertDialog.Builder(activity)
            .setTitle("Need permission(s)")
            .setMessage("Some permissions are required to save, open and share your drawing")
            .setPositiveButton("OK") { dialog, which -> requestPermissions() }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            activity.toast("We need to get permissions to allow you yo use all functionality.")
        } else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }
    }
}