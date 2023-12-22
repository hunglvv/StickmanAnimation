package com.hunglvv.stickmananimation.library.utils.helper

import android.content.Context
import com.hunglvv.stickmananimation.library.utils.formatTimePattern
import com.hunglvv.stickmananimation.library.utils.getOutputFileDirectory
import java.io.File

fun Context.writeLog(message: String) {
    try {
        val file = File(getOutputFileDirectory(this), "log.txt")
        val currentTime = formatTimePattern(System.currentTimeMillis(), "dd/MM/yyyy - hh:mm")
        file.printWriter().use { out ->
            out.println("$currentTime : $message")
        }
    } catch (_: Exception) {
    }
}
