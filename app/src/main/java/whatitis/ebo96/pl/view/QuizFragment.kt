package whatitis.ebo96.pl.view

import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.*
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.adapter.QuizAdapter
import whatitis.ebo96.pl.util.hide
import whatitis.ebo96.pl.util.show

class QuizFragment : LifecycleFragment() {

    private var quizCallback: QuizCallback? = null

    private lateinit var ids: LongArray

    companion object {

        private const val IDS = "ids"

        fun newInstance(): QuizFragment {
            return QuizFragment().apply {
                arguments = Bundle()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLongArray(IDS, ids)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        quizLoading.show()

        val quizAdapter = QuizAdapter(activity?.supportFragmentManager
                ?: childFragmentManager, ids)

        quizViewPager.apply {

            launch(UI) {
                adapter = quizAdapter
                quizLoading.hide()
            }

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                override fun onPageSelected(position: Int) {
                    quizCallback?.changeQuizPageIndicator(position + 1)
                }
            })

            quizCallback?.changeQuizPageIndicator(quizViewPager.currentItem + 1)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.quiz_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.checkQuiz -> {
                quizCallback?.checkQuiz()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        quizCallback = context as? QuizCallback
    }
}
