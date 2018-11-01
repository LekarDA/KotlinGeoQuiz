package com.dmitriy.android.geoquiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_cheat.*
import android.view.ViewAnimationUtils


class CheatActivity : AppCompatActivity() {

    private var answerIstrue = false
    private var isCheater = false
    private var positionOfCheaterQuestion = -1
    private var countOfHelp = 3

    companion object {
        private const val EXTRA_ANSWER_IS_TRUE = "EXTRA_ANSWER_IS_TRUE"
        private const val EXTRA_ANSWER_SHOWN = "EXTRA_ANSWER_SHOWN"
        private const val CHEATER_KEY = "CHEATER_KEY"
        private const val POSITION_OF_QUESTION = "POSITION_OF_QUESTION"
        private const val COUNT_OF_HELP = "COUNT_OF_HELP"


        fun getIntent(context: Context, answerIsTrue: Boolean, position: Int, countOfHelp: Int): Intent {
            val intent = Intent(context, CheatActivity::class.java)
            intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            intent.putExtra(POSITION_OF_QUESTION, position)
            intent.putExtra(COUNT_OF_HELP,countOfHelp)
            return intent
        }

        fun wasAnswerShown(result: Intent): Boolean {
            return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
        }

        fun answerShownPosition(result: Intent): Int {
            return result.getIntExtra(POSITION_OF_QUESTION, -1)
        }

        fun getCountOfHelp(result: Intent):Int {
            return result.getIntExtra(COUNT_OF_HELP,3)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        answerIstrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        positionOfCheaterQuestion = intent.getIntExtra(POSITION_OF_QUESTION, -1)
        countOfHelp = intent.getIntExtra(COUNT_OF_HELP,3)

        showAnswerButton.setOnClickListener(::showAnswer)

        if (savedInstanceState != null)
            setAnswerShownResult(
                savedInstanceState.getBoolean(CHEATER_KEY),
                savedInstanceState.getInt(POSITION_OF_QUESTION),
                savedInstanceState.getInt(COUNT_OF_HELP)
            )


    }

    private fun showAnswer(it: View) {
        if(countOfHelp != 0){
            countOfHelp--
            if (answerIstrue) answerTextView.setText(R.string.true_button)
            else answerTextView.setText(R.string.false_button)
            setAnswerShownResult(true, positionOfCheaterQuestion,countOfHelp)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                var cx = showAnswerButton.getWidth() / 2
                var cy = showAnswerButton.getHeight() / 2
                val radius = showAnswerButton.getWidth()
                val anim = ViewAnimationUtils.createCircularReveal(showAnswerButton, cx, cy, radius.toFloat(), 0.0f)

                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator?) {
                        super.onAnimationEnd(animator)
                        showAnswerButton.visibility = View.VISIBLE
                    }
                })
                anim.start()
            }else showAnswerButton.visibility = View.INVISIBLE
        }
        else {
            showAnswerButton.isEnabled = false
        }
        apiTextView.setText("Count of help = " + countOfHelp )

    }

    private fun setAnswerShownResult(isAnswerShown: Boolean, position: Int, countOfHelp: Int) {
        isCheater = isAnswerShown
        positionOfCheaterQuestion = position
        this.countOfHelp = countOfHelp
        val data = Intent()
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        data.putExtra(POSITION_OF_QUESTION, positionOfCheaterQuestion)
        data.putExtra(COUNT_OF_HELP,countOfHelp)
        setResult(Activity.RESULT_OK, data)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(CHEATER_KEY, isCheater)
        outState.putInt(POSITION_OF_QUESTION, positionOfCheaterQuestion)
        outState.putInt(COUNT_OF_HELP,countOfHelp)
        super.onSaveInstanceState(outState)
    }
}
