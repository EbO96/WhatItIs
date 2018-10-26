package whatitis.ebo96.pl.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View

open class LifecycleFragment : Fragment() {

    private var lifecycleFragmentCallback: LifecycleFragmentCallback? = null

    companion object {
        fun setToolbar(activity: AppCompatActivity?, enableHome: Boolean, title: String, subtitle: String? = null) {
            activity?.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(enableHome)
                this.title = title
                this.subtitle = subtitle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleFragmentCallback?.created(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleFragmentCallback?.destroyed(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        lifecycleFragmentCallback = context as? LifecycleFragmentCallback
    }
}