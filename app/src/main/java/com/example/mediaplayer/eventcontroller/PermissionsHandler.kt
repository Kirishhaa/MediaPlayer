package com.example.mediaplayer.eventcontroller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity

class PermissionsHandler {

    private val INITIAL_PERMS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    )

    fun requestPermissions(context: Context) {
        (context as AppCompatActivity).requestPermissions(INITIAL_PERMS, 1)
    }

    fun isReadExternalStorageGranted(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}