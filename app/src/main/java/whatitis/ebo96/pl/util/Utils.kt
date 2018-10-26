package whatitis.ebo96.pl.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


fun Bitmap.asString(quality: Int = 100, format: Bitmap.CompressFormat) = let {
    val byteArrayOutStream = ByteArrayOutputStream()
    compress(format, quality, byteArrayOutStream)
    val bitmapBytes = byteArrayOutStream.toByteArray()
    Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
}

fun String.asBitmap(): Bitmap? {
    val byteArray = Base64.decode(this, Base64.DEFAULT)
    val stream = ByteArrayInputStream(byteArray)
    return BitmapFactory.decodeStream(stream)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun <T : View> ViewGroup.forEach(block: (view: T, index: Int) -> Unit) {
    for (index in 0 until this.childCount) {
        block(this.getChildAt(index) as T, index)
    }
}

fun Context.getRealPathFromURI(contentUri: Uri): String {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        for(index in 0 until cursor.columnCount){
            val d = cursor.getColumnName(index)
            d
        }
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    } catch (e: Exception) {
        ""
    } finally {
        cursor?.close()
    }
}