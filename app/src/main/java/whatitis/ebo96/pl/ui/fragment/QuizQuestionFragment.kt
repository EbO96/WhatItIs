package whatitis.ebo96.pl.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.fragment_quiz_question.view.*
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.model.Answer
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.util.forEach
import whatitis.ebo96.pl.util.hide
import whatitis.ebo96.pl.view.LifecycleFragment
import whatitis.ebo96.pl.view.QuizGameCallback

class QuizQuestionFragment : LifecycleFragment() {

    private var quizGameCallback: QuizGameCallback? = null

    private var questionId = -1L

    var position: Int = 1

    private var pair: Pair<Question, ArrayList<Answer>>? = null

    companion object {

        private const val QUESTION_ID = "question_id"
        private const val POSITION = "position"

        fun newInstance(questionId: Long, position: Int): QuizQuestionFragment = QuizQuestionFragment().apply {
            arguments = Bundle().apply {
                putLong(QUESTION_ID, questionId)
                putInt(POSITION, position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionId = arguments?.getLong(QUESTION_ID) ?: -1L
        position = arguments?.getInt(POSITION) ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiz_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pair = quizGameCallback?.getQuestion(questionId)

        pair?.let { pair ->
            view.questionPhoto.setImageBitmap(pair.first.photo)
            view.answersGroup.let { group ->

                pair.second.forEach { indexedAnswer ->
                    (group.getChildAt(indexedAnswer.position) as RadioButton).also { radioButton ->
                        radioButton.text = indexedAnswer.content
                        radioButton.isChecked = indexedAnswer.selected
                    }
                }

                group.forEach<RadioButton> { radioButton, index ->
                    if (radioButton.text.isEmpty()) radioButton.hide()
                }

                group.setOnCheckedChangeListener { radioGroup, checkedId ->
                    val checkedAnswer = "${radioGroup.findViewById<RadioButton>(checkedId).text}"
                    pair.second.forEach { answer ->
                        answer.selected = answer.content == checkedAnswer
                    }
                }
            }
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        quizGameCallback = context as? QuizGameCallback
    }
}