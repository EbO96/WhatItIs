package whatitis.ebo96.pl.view

import android.support.v4.app.Fragment

interface LifecycleFragmentCallback {

    fun destroyed(fragment: Fragment)

    fun created(fragment: Fragment)
}