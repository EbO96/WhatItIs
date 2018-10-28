package whatitis.ebo96.pl.ui.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_quiz_scores.*
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.model.QuizScore
import whatitis.ebo96.pl.view.LifecycleFragment

class QuizScoresFragment : LifecycleFragment() {

    private var score: QuizScore? = null

    private lateinit var displayScore: String

    companion object {
        private val SCORE = "score"

        fun newInstance(quizScore: QuizScore) = QuizScoresFragment().apply {
            arguments = Bundle().apply {
                putParcelable(SCORE, quizScore)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        score = arguments?.getParcelable(SCORE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_scores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        displayScore = "${score?.points}/${score?.maxPoints}"

        fragment_quiz_scores_percent_text_view.text = score?.getPercent()

        viewListeners()

        score?.setScoreCounter()
    }

    private fun QuizScore.setScoreCounter() {
        context?.apply {
            if (points == maxPoints) {
                fragment_quiz_scores_score_text_view.also { scoreTextView ->
                    scoreTextView.text = displayScore
                    scoreTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
                }
            } else {
                val spannableString = SpannableString(displayScore)

                val maxPointsStartIndex = displayScore.indexOf("$maxPoints")
                val maxPointsEndIndex = displayScore.length

                val dividerIndexStart = displayScore.indexOf("/")
                val dividerIndexEnd = dividerIndexStart + 1

                val pointsStartIndex = displayScore.indexOf("$points")
                val pointsEndIndex = "$points".length

                val greenColor = ContextCompat.getColor(this, R.color.green)

                spannableString.setSpan(
                        ForegroundColorSpan(greenColor),
                        maxPointsStartIndex, maxPointsEndIndex,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_gray))
                        , dividerIndexStart, dividerIndexEnd,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.red))
                        , pointsStartIndex, pointsEndIndex,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                fragment_quiz_scores_score_text_view.setText(spannableString, TextView.BufferType.SPANNABLE)
            }
        }
    }

    private fun viewListeners() {
        fragment_quiz_show_quiz_scores.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}
