package whatitis.ebo96.pl.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import whatitis.ebo96.pl.ui.fragment.QuestionsListFragment
import whatitis.ebo96.pl.ui.fragment.QuestionsSetFragment

class MainViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val ITEM_COUNT = 2

    private val ALL_QUSTIONS = 0

    private val SETS = 1

    override fun getCount(): Int = ITEM_COUNT

    override fun getItem(position: Int): Fragment? = when (position) {
        ALL_QUSTIONS -> {
            QuestionsListFragment.newInstance()
        }
        SETS -> {
            QuestionsSetFragment.newInstance()
        }
        else -> null
    }
}