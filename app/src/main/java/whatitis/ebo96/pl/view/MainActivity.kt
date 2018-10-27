package whatitis.ebo96.pl.view

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_question.*
import kotlinx.android.synthetic.main.question_item.view.*
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.data.QuestionsViewModel
import whatitis.ebo96.pl.model.Answer
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.model.QuizScore
import whatitis.ebo96.pl.util.MySimpleAdapter
import whatitis.ebo96.pl.util.adapter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ActivityInteractions, LifecycleFragmentCallback,
        QuizGameCallback, QuizCallback {

    val questionsViewModel: QuestionsViewModel by inject { parametersOf(this) }

    private var quizQuestions: ArrayList<Pair<Question, ArrayList<Answer>>> = ArrayList()

    private var menu: Menu? = null

    private lateinit var questions: ArrayList<Question>

    private lateinit var allAnswers: ArrayList<Answer>

    private var deletedQuestion: Question? = null

    companion object {
        const val PICK_PHOTO_CODE: Int = 0
        private const val QUESTIONS = "questions"
        private const val ALL_ANSWERS = "all_answers"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.main_screen_title)

        addPhotoButton.setOnClickListener {
            addQuestion()
        }

        questions = savedInstanceState?.getParcelableArrayList(QUESTIONS) ?: arrayListOf()

        resolvePhotos()

        allAnswers = savedInstanceState?.getParcelableArrayList(ALL_ANSWERS) ?: arrayListOf()

        quizQuestions =
                questions.map { question -> question to allAnswers.filter { answer -> answer.questionId == question.questionId } as ArrayList }
                        as ArrayList

        photosRecycler.adapter(
                noItemsContainer = container,
                item = R.layout.question_item,
                items = arrayListOf(),
                spans = resources.configuration.orientation,
                decoration = false,
                viewHolder = { question: Question, view: View ->
                    view.apply {
                        descriptionTextView.text = question.description
                        photoImageView.setImageBitmap(question.photo)
                        //Read file from storage
                    }
                },
                click = { question: Question ->
                    editQuestion(question)
                })

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val question = (photosRecycler.adapter as? MySimpleAdapter<Question>)?.items
                        ?.get(viewHolder.adapterPosition)

                if (question != null) {
                    launch {
                        deletedQuestion = question
                        questionsViewModel.deleteQuestion(question)
                    }
                    makeUndoSnackbar()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(photosRecycler)

        questionsViewModel.getAllQuestions().observe(this, Observer { questionsList ->

            resolvePhotos()

            if (questionsList != null)
                addPhotosToQuestions(questionsList as ArrayList<Question>)

            (photosRecycler.adapter as? MySimpleAdapter<Question>)?.items = (questionsList
                    as? ArrayList<Question> ?: arrayListOf()).let { questions ->
                questions.sortBy { question -> question.questionId }
                questions.reverse()
                this@MainActivity.questions.apply {
                    clear()
                    addAll(questions)
                }
            }

            menu?.setGroupVisible(R.id.quizGroup, questionsList != null && questionsList.size > 1)
        })
    }

    private fun showQuizScores(score: QuizScore) {
        val quizScoresFragment = QuizScoresFragment.newInstance(score)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, quizScoresFragment, QuizScoresFragment::class.java.name)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun resolvePhotos() {
        questionsViewModel.photosMap = fileList().map { photoId ->
            (photoId to openFileInput(photoId).use { fileInput ->
                val size = fileInput.available()
                val photoBytes = fileInput.readBytes(size)
                BitmapFactory.decodeByteArray(photoBytes, 0, size)
            })
        }.toMap()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(QUESTIONS, questions)

        val allAnswers = ArrayList<Answer>()

        quizQuestions.map { pair -> allAnswers.addAll(pair.second) }

        outState?.putParcelableArrayList(ALL_ANSWERS, allAnswers)

        super.onSaveInstanceState(outState)
    }

    private fun addPhotosToQuestions(questions: ArrayList<Question>) {
        questions.forEach { question ->
            question.photo = questionsViewModel.photosMap?.get("${question.questionId}")
        }
    }

    private fun addQuestion() {
        val addPhotoFragment = AddQuestionFragment.newInstance(AddQuestionFragment.ADD)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, addPhotoFragment, AddQuestionFragment::class.java.name)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun editQuestion(question: Question) {
        questionsViewModel.setQuestion(question)
        val addPhotoFragment = AddQuestionFragment.newInstance(AddQuestionFragment.EDIT)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, addPhotoFragment, AddQuestionFragment::class.java.name)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun startQuiz() {
        makeQuestionsList()
        val quizFragment = QuizFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, quizFragment, QuizFragment::class.java.name)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun getQuestion(questionId: Long): Pair<Question, ArrayList<Answer>>? =
            quizQuestions.firstOrNull { pair -> pair.first.questionId == questionId }

    override fun getQuizQuestions(): ArrayList<Pair<Question, ArrayList<Answer>>> = quizQuestions

    private fun makeQuestionsList() {

        quizQuestions.clear()

        val listOfAnswersSize = questions.size

        if (listOfAnswersSize == 0) return

        questions.withIndex().forEach { q ->

            val question = q.value

            val questionAnswers = ArrayList<Answer>().apply {
                add(Answer(questionId = question.questionId, content = question.description).apply { correct = true })
            }

            val range = if (listOfAnswersSize > 4) 4 else listOfAnswersSize

            val drawn = arrayListOf<Int>()

            drawn.add(q.index)

            while (questionAnswers.size < range) {
                val rand = Random().nextInt(listOfAnswersSize)
                if (!drawn.contains(rand)) {
                    val drawnQuestion = questions[rand]
                    questionAnswers.add(Answer(questionId = question.questionId, content = drawnQuestion.description))
                    drawn.add(rand)
                }
            }

            questionAnswers.shuffle()

            questionAnswers.withIndex().forEach { answer ->
                answer.value.position = answer.index
            }

            quizQuestions.add(Pair(question, questionAnswers))
        }

        quizQuestions.shuffle()
    }

    override fun changeQuizPageIndicator(questionNumber: Int) {
        supportActionBar?.apply {
            subtitle = "${getString(R.string.question)} $questionNumber/${questions.size}"
        }
    }

    override fun checkQuiz() {
        var points = 0
        var maxPoints = quizQuestions.size

        quizQuestions.forEach { pair ->
            pair.second.firstOrNull { answer -> answer.correct && answer.selected }?.also { points++ }
        }

        val quizScore = QuizScore(System.currentTimeMillis(), points, maxPoints)
        //Show quiz score
        showQuizScores(quizScore)
    }

    override fun get(): AppCompatActivity = this

    override fun created(fragment: Fragment) {
        menu?.setGroupVisible(R.id.quizGroup, false)
        when (fragment) {
            is AddQuestionFragment -> {
                val title = when (fragment.operation) {
                    AddQuestionFragment.ADD -> getString(R.string.add_question)
                    else -> getString(R.string.edit_question)
                }
                LifecycleFragment.setToolbar(this, true, title)
            }
            is QuizFragment -> {
                LifecycleFragment.setToolbar(this, true, getString(R.string.quiz))
            }
            is QuizScoresFragment -> {
                LifecycleFragment.setToolbar(this, true, getString(R.string.scores))
            }

        }
    }

    override fun destroyed(fragment: Fragment) {
        if (fragment !is QuizQuestionFragment) {
            menu?.setGroupVisible(R.id.quizGroup, true)
            LifecycleFragment.setToolbar(this, false, getString(R.string.main_screen_title))
        }
    }

    private fun makeUndoSnackbar() {
        Snackbar.make(coordinatorLayout, getString(R.string.deleted), Snackbar.LENGTH_SHORT).also { snackbar ->
            snackbar.setAction(R.string.undo, UndoDeletedQuestion())

            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        deleteFile("${deletedQuestion?.questionId}")
                        deletedQuestion = null
                    }
                }
            })
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        this.menu = menu
        menu?.setGroupVisible(R.id.quizGroup, questions.size > 1)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            android.R.id.home -> {
                supportFragmentManager?.popBackStack()
                true
            }
            R.id.quiz -> {
                startQuiz()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_PHOTO_CODE -> {
                if (resultCode == Activity.RESULT_OK) {

                    val uri = data?.data
                    if (uri != null) {
                        //Save picket photo as file
                        var byteArray = byteArrayOf()
                        contentResolver.openInputStream(uri).use { inputStream ->
                            byteArray = inputStream.readBytes(inputStream.available())
                        }
                        val photoId = System.currentTimeMillis()

                        val fileName = "$photoId"

                        (supportFragmentManager?.findFragmentByTag(AddQuestionFragment::class.java.name)
                                as? AddQuestionFragment)?.apply {
                            pickedPhotoId = photoId

                            deleteFile("${questionsViewModel.getQuestion()?.questionId}")

                            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                                it.write(byteArray)
                            }

                            pickedPhotoImageView?.setImageURI(uri)
                        }
                    }
                }
            }
        }
    }

    private inner class UndoDeletedQuestion : View.OnClickListener {
        override fun onClick(p0: View?) {
            deletedQuestion?.let { question ->
                launch {
                    questionsViewModel.addQuestion(question)
                }
            }
        }

    }

}
