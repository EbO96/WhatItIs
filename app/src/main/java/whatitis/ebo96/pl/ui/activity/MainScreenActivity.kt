package whatitis.ebo96.pl.ui.activity

import android.arch.lifecycle.Observer
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main_screen.*
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.adapter.MainViewPagerAdapter
import whatitis.ebo96.pl.ui.fragment.AddQuestionFragment
import whatitis.ebo96.pl.ui.fragment.QuestionFragment
import whatitis.ebo96.pl.ui.fragment.QuestionsListFragment
import whatitis.ebo96.pl.ui.fragment.QuestionsSetFragment
import whatitis.ebo96.pl.view.LifecycleFragment
import whatitis.ebo96.pl.view.LifecycleFragmentCallback

class MainScreenActivity : AppCompatActivity(), LifecycleFragmentCallback {

    private val questionViewModel by lazy { QuestionFragment.getQuestionViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        setViews()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setupViewPager()
        } else {
            setupTwoPaneMode()
        }

        viewListeners()

        questionViewModel.deleteMode.observe(this, Observer { deleteMode ->
            if (deleteMode == null) {
                LifecycleFragment.currentStack(this)
            }
        })
    }

    private fun setViews() {
        setSupportActionBar(toolbar)
        orientationToPageTitle()
    }

    private fun viewListeners() {

    }

    private fun setupViewPager() {
        val adapter = MainViewPagerAdapter(supportFragmentManager)

        activity_main_screen_view_pager?.apply {
            this.adapter = adapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(position: Int) {

                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                override fun onPageSelected(position: Int) {
                    position.toPageTitle()
                }
            })
        }

    }

    private fun setupTwoPaneMode() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.activity_main_screen_question_list_container, QuestionsListFragment.newInstance(),
                    QuestionsListFragment::class.java.name)
            add(R.id.activity_main_screen_set_container, QuestionsSetFragment.newInstance(),
                    QuestionsSetFragment::class.java.name)
            commit()
        }
    }

    private fun Int.toPageTitle() {
        if (!questionViewModel.isInDeleteMode()) {
            supportActionBar?.title = when {
                this == 0 -> getString(R.string.your_list)
                this == 1 -> getString(R.string.set)
                else -> ""
            }
        }
    }

    private fun orientationToPageTitle() {
        supportActionBar?.title = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getString(R.string.your_list)
        } else {
            getString(R.string.app_name)
        }
    }

    private fun quitPhotoEditionMode() {
        if (AddQuestionFragment::class.java.findInStack() == null) {
            questionViewModel.questionToEdit.value = null
            questionViewModel.picketPhoto.value = null
        }
    }

    private fun isPortrait() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    override fun currentStack(top: Fragment?, second: Fragment?) {
        if (isPortrait()) {
            if (top is QuestionsListFragment)
                activity_main_screen_view_pager?.currentItem?.toPageTitle()
        } else {
            if (top is QuestionsListFragment && second is QuestionsSetFragment) {
                supportActionBar?.title = getString(R.string.app_name)
            }
        }
    }

    override fun created(fragment: Fragment) {

    }

    override fun destroyed(fragment: Fragment) {

    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                android.R.id.home -> {
                    quitPhotoEditionMode()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }


    override fun onBackPressed() {
        super.onBackPressed()
        quitPhotoEditionMode()
    }

    private fun <T : Fragment> Class<T>.findInStack(block: ((fragment: T?) -> Unit)? = null): Fragment? = supportFragmentManager?.fragments?.firstOrNull { fragment -> fragment.javaClass.isAssignableFrom(this) }.also { fragment ->
        @Suppress("UNCHECKED_CAST")
        block?.invoke(fragment as T)
    }

}
