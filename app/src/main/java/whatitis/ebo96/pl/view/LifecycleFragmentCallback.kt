package whatitis.ebo96.pl.view

import android.support.v4.app.Fragment

interface LifecycleFragmentCallback {

    fun currentStack(top: Fragment?, second: Fragment?)

    fun destroyed(fragment: Fragment)

    fun created(fragment: Fragment)
}