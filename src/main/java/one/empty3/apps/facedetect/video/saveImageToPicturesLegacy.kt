package one.empty3.apps.facedetect.video

import one.empty3.libs.Image
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

fun saveImageToPicturesLegacy(bitmap: Image, filename: String): Boolean {


    try {
        val directory = File(
            "Empty3_Mesh_Masks"
        )
        if (!directory.exists()) {
            directory.mkdirs() // Create the directory if it doesn't exist
        }
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") var file = File(directory, filename+directory.listFiles().size+"_${System.currentTimeMillis()}"+".jpg")
        Logger.getAnonymousLogger().info("saveImageToPicturesLegacy: ${file.absolutePath}")
        if(file.exists()) {
            return false
        }
        FileOutputStream(file).use { outputStream ->
            bitmap.saveFile(file)
            outputStream.flush()
        }
        return true
    } catch (e: RuntimeException) {
        Logger.getAnonymousLogger().log(Level.SEVERE, "SaveImageLegacy", "Error writing image: ${e.message}")
        return false
    }
}

