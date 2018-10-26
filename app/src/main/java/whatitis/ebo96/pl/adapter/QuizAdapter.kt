package whatitis.ebo96.pl.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import whatitis.ebo96.pl.view.QuizQuestionFragment

class QuizAdapter(fragmentManager: FragmentManager,
                  private val ids: LongArray) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(index: Int): Fragment {
        return QuizQuestionFragment.newInstance(ids[index], index)
    }

    override fun getCount(): Int = ids.size

}