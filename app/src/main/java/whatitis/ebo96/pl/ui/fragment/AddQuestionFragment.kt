package whatitis.ebo96.pl.ui.fragment


import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_question.*
import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.ui.activity.MainActivity
import java.io.ByteArrayOutputStream

class AddQuestionFragment : QuestionFragment() {

    private val questionViewModel by lazy { getQuestionViewModel(activity as AppCompatActivity) }

    companion object {

        fun newInstance(): AddQuestionFragment {
            return AddQuestionFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setViews()

        questionViewModel.questionToEdit.observe(this, Observer { question ->
            if (question != null) {
                fragment_add_question_photo_image_view.setImageBitmap(question.photo)
                fragment_add_question_description_edit_text.setText(question.description)
                questionViewModel.picketPhoto.value = question.photo
            }
        })

        questionViewModel.picketPhoto.observe(this, Observer { photoBytes ->
            if (photoBytes != null) {
                fragment_add_question_photo_image_view?.setImageBitmap(questionViewModel.picketPhoto.value)
            }
        })

        viewListeners()
    }

    private fun setViews() {
    }

    private fun viewListeners() {
        pickPhoto.setOnClickListener {
            pickPhoto()
        }
    }

    private fun save() {
        val description = "${fragment_add_question_description_edit_text.text}"
        val photoBytes = questionViewModel.picketPhoto.value
        if (photoBytes != null && description.isNotEmpty()) {

            val questionToEdit = questionViewModel.questionToEdit.value

            val question = Question(description).also { question ->
                questionViewModel.picketPhoto.value?.let { bitmap -> question.photo = bitmap }
                question.photoId = questionToEdit?.photoId ?: "${System.currentTimeMillis()}"
                question.id = questionToEdit?.id ?: 0
            }

            //Save photo
            context?.openFileOutput(question.photoId, Context.MODE_PRIVATE).use { fileOutputStream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                question.photo?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                fileOutputStream?.write(byteArrayOutputStream.toByteArray())
            }

            //Insert question
            val databaseOperation = if (questionToEdit == null) questionViewModel.insert(question)
            else questionViewModel.update(question)

            databaseOperation
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess {
                        activity?.onBackPressed()
                    }
                    .subscribe()
                    .addToDisposables()
        } else {
            Toast.makeText(context, getString(R.string.cannot_save_new_question), Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, MainActivity.PICK_PHOTO_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.add_question_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save -> {
                save()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MainActivity.PICK_PHOTO_CODE -> {
                if (resultCode == Activity.RESULT_OK) {

                    val photoUri = data?.data

                    if (photoUri != null) {
                        //Save picket photo as file
                        activity?.contentResolver?.openInputStream(photoUri)?.use { inputStream ->
                            val photoBytes = inputStream.readBytes(inputStream.available())
                            questionViewModel.picketPhotoBytes.value = photoBytes
                            questionViewModel.picketPhoto.value = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                        }
                    }
                }
            }
        }
    }

    private fun Disposable.addToDisposables() {
        questionViewModel.compositeDisposable.add(this)
    }

    override fun onResume() {
        super.onResume()
        val title = if (questionViewModel.questionToEdit.value == null)
            getString(R.string.add_question) else getString(R.string.edit_question)

        title.asToolbarTitle()
    }
}

