package whatitis.ebo96.pl.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View

open class LifecycleFragment : Fragment() {

    companion object {

        private var lifecycleFragmentCallback: LifecycleFragmentCallback? = null

        fun setToolbar(activity: AppCompatActivity?, enableHome: Boolean, title: String, subtitle: String? = null) {
            activity?.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(enableHome)
                this.title = title
                this.subtitle = subtitle
            }
        }

        fun currentStack(activity: AppCompatActivity?) {
            activity?.supportFragmentManager?.fragments?.also { fragments ->
                val fragmentsSize = fragments.size
                val stack = if (fragmentsSize >= 2) Pair(fragments[fragmentsSize - 2], fragments.last())
                else Pair(fragments.firstOrNull(), null)

                lifecycleFragmentCallback?.currentStack(stack.first, stack.second)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleFragmentCallback?.created(this)
        currentStack(activity as? AppCompatActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleFragmentCallback?.destroyed(this)
        currentStack(activity as? AppCompatActivity)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        lifecycleFragmentCallback = context as? LifecycleFragmentCallback
    }


    fun String.asToolbarTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = this
    }
}