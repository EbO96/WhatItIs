package whatitis.ebo96.pl.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import whatitis.ebo96.pl.R

class QuestionPhoto(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    private var onImageChangeListener: OnImageChangeListener? = null

    fun setOnImageChangeListener(onImageChangeListener: OnImageChangeListener) {
        this.onImageChangeListener = onImageChangeListener
    }

    override fun setImageURI(uri: Uri?) {
        if (validPhoto(uri)) {
            super.setImageURI(uri)
            onImageChangeListener?.onChanged(uri)
        } else setImageResource(R.drawable.ic_launcher_background)
    }

    private fun validPhoto(photoUri: Uri?): Boolean {
        return try {
            val ins = context.contentResolver.openInputStream(photoUri)
            ins.close()
            true
        } catch (e: Exception) {
            false
        }
    }

}