package com.example.submissionaplikasistory.utils

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.example.submissionaplikasistory.BuildConfig
import com.example.submissionaplikasistory.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {

    companion object {

        private val FILENAME_FORMAT = "yyyMMdd_HHmmss"
        private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        private const val MAXIMAL_SIZE = 1000000

        fun dialogInstance(applicationContext: Context): Dialog {
            val dialog = Dialog(applicationContext)

            val window: Window? = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.background_dialog
                )
            )
            window?.attributes?.gravity = Gravity.BOTTOM

            dialog.setCancelable(false)

            return dialog
        }

        fun getHeader(token: String): Map<String, String> {
            val header = mutableMapOf<String, String>()
            header["Authorization"] = "Bearer $token"
            return header
        }

        fun getImageUri(context: Context): Uri {
            var uri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValue = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
                }
                uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValue
                )
            }

            return uri ?: getImageUriForPreQ(context)
        }

        private fun getImageUriForPreQ(context: Context): Uri {
            val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File(fileDir, "/MyCamera/$timeStamp.jpg")
            if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()

            return FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.fileProvider",
                imageFile
            )
        }

        private fun createCustomTempFile(context: Context): File {
            val fileDir = context.externalCacheDir
            return File.createTempFile(timeStamp, ".jpg", fileDir)
        }

        fun uriToFile(imageUri: Uri, context: Context): File {
            val myFile = createCustomTempFile(context)
            val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
            val outputStream = FileOutputStream(myFile)
            val buffer = ByteArray(1000)
            var length: Int

            while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
            outputStream.close()
            inputStream.close()
            return myFile
        }

        fun File.reduceImage(): File {
            val file = this
            val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
            var compressQuality: Int = 100
            var streamLength: Int

            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > MAXIMAL_SIZE)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
            return file
        }

        fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
            val orientation = ExifInterface(file).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
            )
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
                ExifInterface.ORIENTATION_NORMAL -> this
                else -> this
            }
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height, matrix, true
            )
        }
    }
}