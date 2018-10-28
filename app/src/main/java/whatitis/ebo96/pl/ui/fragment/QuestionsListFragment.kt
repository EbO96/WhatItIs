package whatitis.ebo96.pl.ui.fragment

import android.arch.lifecycle.Observer
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_questions_list.*
import kotlinx.android.synthetic.main.question_item.view.*
import kotlinx.coroutines.experimental.launch
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.util.adapter

class QuestionsListFragment : QuestionFragment() {

    private val questionViewModel by lazy { getQuestionViewModel(activity as AppCompatActivity) }

    private lateinit var menu: Menu

    companion object {
        fun newInstance() = QuestionsListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val adapter = fragment_questions_list_recycler.adapter(
                noItemsContainer = fragment_questions_list_root,
                item = R.layout.question_item,
                items = arrayListOf(),
                decoration = false,
                viewHolder = { question: Question, itemView: View ->
                    itemView.question_item_description_text_view.text = question.description
                    itemView.photoImageView.setImageBitmap(question.photo)
                    itemView.question_item_root_card.isSelected = question.selected
                },
                click = { question: Question, _: Int ->
                    if (questionViewModel.isInDeleteMode()) {
                        question.selected = !question.selected
                        questionViewModel.deleteMode.value?.query(question)
                        questionViewModel.update(question)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                    } else {
                        questionViewModel.questionToEdit.value = question
                        editPhoto()
                    }
                },
                longClick = { question ->
                    if (!questionViewModel.isInDeleteMode()) {
                        question.selected = !question.selected
                        questionViewModel.update(question)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doAfterSuccess {
                                    questionViewModel.startDeleteMode()
                                    questionViewModel.deleteMode.value?.query(question)
                                }
                                .subscribe()
                    }

                })

        questionViewModel.getQuestions().observe(this, Observer { questions ->
            if (questionViewModel.isInDeleteMode()) {
                //Selected items counter
                (activity as? AppCompatActivity)?.supportActionBar?.title =
                        "${questionViewModel.deleteMode.value?.photosIds?.size
                                ?: 0}/${questions?.size ?: 0}"
            }
            adapter.items = questions.appendPhotos()
        })

        viewsListeners()
    }

    private fun viewsListeners() {
        fragment_question_list_add_question_button.setOnClickListener {
            showAddQuestionFragment()
        }
    }

    private fun showAddQuestionFragment() {
        val addPhotoFragment = AddQuestionFragment.newInstance()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(R.id.container, addPhotoFragment, AddQuestionFragment::class.java.name)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun deletionMenu(enabled: Boolean) {

        menu.setGroupVisible(R.id.deletionMenu, enabled)

        if (enabled) {
            fragment_question_list_add_question_button.hide()
        } else
            fragment_question_list_add_question_button.show()
    }

    private fun deselectSelected() {
        questionViewModel
                .deselectSelected()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe()
    }

    private fun Single<Int>.performDelete(all: Boolean = false) {
        subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess { deletedRows ->
                    if (deletedRows != 0) {
                        val photosIds = questionViewModel.deleteMode.value?.photosIds
                                ?: arrayListOf()

                        if (photosIds.isEmpty() || all) {
                            val savedPhotosNames = context?.fileList()
                            if (savedPhotosNames == null || savedPhotosNames.isEmpty()) return@doAfterSuccess
                            photosIds.addAll(savedPhotosNames)
                        }

                        photosIds.forEach { photoId ->
                            launch {
                                context?.deleteFile(photoId)
                            }
                        }
                        questionViewModel.stopDeleteMode()
                    }
                }.subscribe()
    }

    private fun List<Question>?.appendPhotos(): ArrayList<Question> = (this?.map { question ->
        question.addPhoto()
    } as? ArrayList<Question>) ?: arrayListOf()

    private fun Question.addPhoto(): Question = also {
        val photoBytes = context?.openFileInput(photoId)?.readBytes()
        photo = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes?.size ?: 0)
    }

    private fun editPhoto() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(activity?.container?.id
                ?: -1, AddQuestionFragment.newInstance(), AddQuestionFragment::class.java.name)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.questions_list_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        if (menu != null) {
            this@QuestionsListFragment.menu = menu
        }
        questionViewModel.deleteMode.observe(this, Observer { deletionMode ->
            deletionMenu(deletionMode != null)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                R.id.deleteAll -> {
                    questionViewModel
                            .deleteAll()
                            .performDelete(true)
                    true
                }
                R.id.delete -> {
                    questionViewModel
                            .deleteMode
                            .value
                            ?.delete()
                            ?.performDelete()
                    true
                }
                R.id.cancel -> {
                    deselectSelected()
                    questionViewModel.stopDeleteMode()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
