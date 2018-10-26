package whatitis.ebo96.pl.view

import android.net.Uri

interface OnImageChangeListener {

    fun onChanged(uri: Uri?)
}