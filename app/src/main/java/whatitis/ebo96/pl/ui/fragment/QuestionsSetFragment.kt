package whatitis.ebo96.pl.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import whatitis.ebo96.pl.R
import whatitis.ebo96.pl.ui.presenter.QuestionViewModel
import whatitis.ebo96.pl.view.LifecycleFragment

class QuestionsSetFragment : QuestionFragment(){

    companion object {
        fun newInstance() = QuestionsSetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questions_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
    }

}
