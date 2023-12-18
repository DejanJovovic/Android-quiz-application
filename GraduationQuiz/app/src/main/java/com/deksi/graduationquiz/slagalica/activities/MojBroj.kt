package com.deksi.graduationquiz.slagalica.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.databinding.ActivityMojBrojBinding
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import java.util.LinkedList
import java.util.Random

class MojBroj : AppCompatActivity() {

    private lateinit var binding: ActivityMojBrojBinding
    private var input: MutableList<Button?> = LinkedList()
    private var generated = false
    private var currentNumber = 0
    private var timeLeft: CountDownTimer? = null
    private var generateTimer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMojBrojBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        init()
        initTimer()
        setUpFinalResultButtons()

    }

    private fun setUpFinalResultButtons() {
        val result = binding.buttonFinalResult
        val finish = binding.buttonDone
        val stopBtn = binding.buttonStopDigits

        finish.setOnClickListener{
            timeLeft?.cancel()
            val solution = binding.textViewUserInput
            val guess = evalSolution(solution.text.toString())
            val actual = result.text.toString().toDouble()

            if (actual == guess) {
                Toast.makeText(applicationContext, "Osvojili ste 20 poena", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Osvojili ste 5 poena", Toast.LENGTH_LONG).show()

            }

        }

        stopBtn.setOnClickListener {
            generateNumbers()
            generateTimer?.cancel()
            generateTimer?.start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
    }

    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {}
        }
        (timeLeft as CountDownTimer).start()
    }


    private fun init() {
        val firstSingleDigit = binding.buttonFirstSingleDigit
        val secondSingleDigit = binding.buttonSecondSingleDigit
        val thirdSingleDigit = binding.buttonThirdSingleDigit
        val fourthSingleDigit = binding.buttonFourthSingleDigit
        val firstMediumDigit = binding.buttonFirstMediumDigit
        val secondMediumDigit = binding.buttonSecondMediumDigit
        val minus = binding.buttonMinus
        val plus = binding.buttonPlus
        val multiply = binding.buttonMultiply
        val divide = binding.buttonDivide
        val openBracket = binding.buttonOpenBrackets
        val closedBracket = binding.buttonCloseBrackets
        val delete = binding.buttonDelete
        firstSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.isEnabled = false
        }
        secondSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        thirdSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        fourthSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        firstMediumDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        secondMediumDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        plus.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        minus.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        multiply.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        divide.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        openBracket.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        closedBracket.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        delete.setOnClickListener { v: View? ->
            if (input.size == 0) {
            } else {
                input.get(input.size - 1)?.isEnabled = true
                input.removeAt(input.size - 1)
                displayInput()
            }
        }
    }

    private fun displayInput() {
        val buffer = StringBuffer()
        for (value in input) {
            buffer.append(value?.text.toString())
        }
        val inputText = binding.textViewUserInput
        inputText.text = buffer.toString()
    }


    private fun generateNumbers() {
        if (generated) return
        val singleDigits = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        val firstMediumDigits = arrayOf("10", "15", "20")
        val secondMediumDigits = arrayOf("25", "50", "75", "100")
        var idx = 0
        var generatedNumber: String? = null
        if (currentNumber == 0) {
            generatedNumber = "" + (Random().nextInt(998) + 2)
        } else if (currentNumber in 1..4) {
            idx = Random().nextInt(singleDigits.size)
            generatedNumber = singleDigits[idx]
        } else if (currentNumber == 5) {
            idx = Random().nextInt(firstMediumDigits.size)
            generatedNumber = firstMediumDigits[idx]
        } else if (currentNumber == 6) {
            idx = Random().nextInt(secondMediumDigits.size)
            generatedNumber = secondMediumDigits[idx]
        }
        setNumber(generatedNumber, currentNumber)

        currentNumber++
    }


    private fun setNumber(number: String?, idx: Int) {
        val firstSingleDigit = binding.buttonFirstSingleDigit
        val secondSingleDigit = binding.buttonSecondSingleDigit
        val thirdSingleDigit = binding.buttonThirdSingleDigit
        val fourthSingleDigit = binding.buttonFourthSingleDigit
        val firstMediumDigit = binding.buttonFirstMediumDigit
        val secondMediumDigit = binding.buttonSecondMediumDigit
        val finalResult = binding.buttonFinalResult
        when (idx) {
            0 -> {
                finalResult.text = number
            }

            1 -> {
                firstSingleDigit.text = number
            }

            2 -> {
                secondSingleDigit.text = number
            }

            3 -> {
                thirdSingleDigit.text = number
            }

            4 -> {
                fourthSingleDigit.text = number
            }

            5 -> {
                firstMediumDigit.text = number
            }

            6 -> {
                enableButtons()
                generated = true
                secondMediumDigit.text = number
            }
        }
    }


    private fun enableButtons() {
        val firstSingleDigit = binding.buttonFirstSingleDigit
        val secondSingleDigit = binding.buttonSecondSingleDigit
        val thirdSingleDigit = binding.buttonThirdSingleDigit
        val fourthSingleDigit = binding.buttonFourthSingleDigit
        val firstMediumDigit = binding.buttonFirstMediumDigit
        val secondMediumDigit = binding.buttonSecondMediumDigit
        val minus = binding.buttonMinus
        val plus = binding.buttonPlus
        val multiply = binding.buttonMultiply
        val divide = binding.buttonDivide
        val openBracket = binding.buttonOpenBrackets
        val closedBracket = binding.buttonCloseBrackets
        val delete = binding.buttonDelete
        firstSingleDigit.isEnabled = true
        secondSingleDigit.isEnabled = true
        thirdSingleDigit.isEnabled = true
        fourthSingleDigit.isEnabled = true
        firstMediumDigit.isEnabled = true
        secondMediumDigit.isEnabled = true
        minus.isEnabled = true
        plus.isEnabled = true
        multiply.isEnabled = true
        divide.isEnabled = true
        openBracket.isEnabled = true
        closedBracket.isEnabled = true
        delete.isEnabled = true
    }

    private fun evalSolution(solution: String?): Double? {
        val rhino: Context = Context.enter()
        rhino.optimizationLevel = -1
        return try {
            val scope: Scriptable = rhino.initStandardObjects()
            val result: Any = rhino.evaluateString(scope, solution, "JavaScript", 1, null)
            Context.toString(result).toDouble()
        } finally {
            Context.exit()
        }
    }

}