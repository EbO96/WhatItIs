package whatitis.ebo96.pl.view


import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_add_question.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.data.QuestionInterface
import whatitis.ebo96.pl.model.Question

class AddQuestionFragment : LifecycleFragment() {

    private var questionInterface: QuestionInterface? = null

    var operation: String = ADD

    private var question: Question? = null

    var pickedPhotoId: Long = -1

    companion object {

        const val ADD = "ADD"
        const val EDIT = "EDIT"

        private const val OPERATION = "OPERATION"
        private const val QUESTION = "QUESTION"

        fun newInstance(operation: String): AddQuestionFragment {
            val args = Bundle().apply {
                putString(OPERATION, operation)
            }
            return AddQuestionFragment().apply { arguments = args }
        }

        private const val PICKED_PHOTO_ID = "picked_photo_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operation = arguments?.getString(OPERATION, ADD) ?: ""
        question = questionInterface?.getQuestion()
        pickedPhotoId = question?.questionId ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        pickPhoto.setOnClickListener {
            questionInterface?.pickPhoto(pickedPhotoImageView)
        }

        if (savedInstanceState != null) {
            question = savedInstanceState.getParcelable(QUESTION)
        }

        question?.apply {
            descriptionEditText?.setText(description)
        }

        val photoBitmap = questionInterface?.getPhoto("${question?.questionId}")
        pickedPhotoImageView.setImageBitmap(photoBitmap)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(QUESTION, question)
        super.onSaveInstanceState(outState)
    }

    private fun save() {
        val description = "${descriptionEditText.text}"

        if (description.isEmpty() || pickedPhotoId == -1L) {
            launch(UI) {
                Toast.makeText(context, getString(R.string.cannot_save_new_question), Toast.LENGTH_SHORT).show()
            }
            return
        }

        when (operation) {
            ADD -> {
                questionInterface?.addQuestion(Question(description, pickedPhotoId))
            }
            EDIT -> {
                question?.apply {

                    this.description = description
                    this.questionId = pickedPhotoId

                    questionInterface?.updateQuestion(this)

                } ?: kotlin.run {
                    launch(UI) {
                        Toast.makeText(context, getString(R.string.cannot_save), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        activity?.supportFragmentManager?.popBackStack()
    }


    override fun onDestroy() {
        super.onDestroy()
        questionInterface?.setQuestion(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.add_question_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save -> {
                launch {
                    save()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        questionInterface = (context as? MainActivity)?.questionsViewModel
    }
}
