package com.dmitriy.android.geoquiz

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "QuizActivity"
        private const val KEY_INDEX = "KEY_INDEX"
        private const val KEY_CHEATER = "KEY_CHEATER"
        private const val REQUEST_CODE_CHEAT = 0
        private const val POSITION_OF_CHEATER = "POSITION_OF_CHEATER"
        private const val COUNT_OF_HELP = "COUNT_OF_HELP"
    }

    private var currentIndex = 0
    private var question  = 0
    private var correctAnswers = 0
    private var isCheater = false
    private var positionOfCheater = -1
    private var countOfHelp = 3

    private val questionBank : Array<Question> = arrayOf(
         Question(R.string.question_australia, true),
         Question(R.string.question_oceans, true),
         Question(R.string.question_mideast, false),
         Question(R.string.question_africa, false),
         Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle) called")
        setContentView(R.layout.activity_quiz)

        currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: 0
        isCheater = savedInstanceState?.getBoolean(KEY_CHEATER) ?: false
        positionOfCheater = savedInstanceState?.getInt(POSITION_OF_CHEATER) ?: -1
        countOfHelp = savedInstanceState?.getInt(COUNT_OF_HELP) ?: 3
        updateQuestion()

        trueBtn.setOnClickListener(::doSome)
        falseBtn.setOnClickListener(::doSome)
        nextBtn.setOnClickListener(::doSome)
        prevBtn.setOnClickListener(::doSome)
        questionTextView.setOnClickListener(::doSome)
        cheatButton.setOnClickListener(::doSome)
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        Log.d(TAG, "onPause() called")
        super.onPause()
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX,currentIndex)
        outState.putBoolean(KEY_CHEATER,isCheater)
        outState.putInt(POSITION_OF_CHEATER,positionOfCheater)
        outState.putInt(COUNT_OF_HELP,countOfHelp)
        super.onSaveInstanceState(outState)
    }



    private fun doSome(it: View) {
       when(it.id){
            R.id.trueBtn -> { checkAnswer(true, positionOfCheater) }
            R.id.falseBtn -> { checkAnswer(false, positionOfCheater) }
            R.id.nextBtn,
            R.id.questionTextView -> {
                currentIndex = (currentIndex + 1) % questionBank.size
                updateQuestion()
                isCheater = false
                changeButtonStatus(true)
                checkQuestionStatus(currentIndex)
            }
           R.id.prevBtn -> {
               currentIndex = (currentIndex - 1) % questionBank.size
               updateQuestion()
           }
           R.id.cheatButton -> {
               val answerIsTrue = questionBank[currentIndex].answerTrue
               val intent = CheatActivity.getIntent(this,answerIsTrue, currentIndex, countOfHelp)
               startActivityForResult(intent,REQUEST_CODE_CHEAT)
           }
       }
    }

    private fun checkQuestionStatus(currentIndex: Int) {
       if(currentIndex == 0 ){
           val result  = ( 100.0 / questionBank.size ) * correctAnswers
           Math.round(result)
           val answerResult = String.format("%(.2f",result)
           Toast.makeText(this,"result: " + answerResult+ "%",Toast.LENGTH_SHORT).show()
           correctAnswers = 0
        }

    }

    private fun updateQuestion(){
        question = questionBank[currentIndex].textResId
        questionTextView.setText(question)
    }

    private fun checkAnswer(userPressedTrue : Boolean, positionOfCheater: Int) {
        val answerIsTrue = questionBank[currentIndex].answerTrue
        var messageResId =
        if(isCheater ||positionOfCheater == currentIndex){ R.string.judgment_toast}
        else {
             when (userPressedTrue == answerIsTrue) {
                true -> {
                    correctAnswers++
                    R.string.correct_toast
                }
                false -> R.string.incorrect_toast
        }
    }
        changeButtonStatus(false)
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show()
    }

    private fun changeButtonStatus(status: Boolean) {
        trueBtn.isEnabled = status
        falseBtn.isEnabled = status
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK) return
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null) return
            isCheater = CheatActivity.wasAnswerShown(data)
            positionOfCheater = CheatActivity.answerShownPosition(data)
            countOfHelp = CheatActivity.getCountOfHelp(data)
        }
    }
}



