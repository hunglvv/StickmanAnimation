package com.testarossa.template.library.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.testarossa.template.library.utils.extension.isBuildLargerThan

fun Map<String, Boolean>.isPermissionGranted(): Boolean {
    var result = true
    keys.forEach { permission ->
        if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (!isBuildLargerThan(Build.VERSION_CODES.Q) && this[permission] == false) {
                result = false
            }
        } else {
            if (this[permission] == false) {
                result = false
            }
        }
    }
    return result
}


fun Context.goToSettingsApplication() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .setData(Uri.fromParts("package", packageName, null))
    startActivity(intent)
}