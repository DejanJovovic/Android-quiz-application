package com.deksi.graduationquiz.slagalica.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivitySkockoBinding
import java.util.Random

class Skocko : AppCompatActivity() {

    private lateinit var binding: ActivitySkockoBinding
    private var results = arrayOfNulls<TextView>(6)
    private var slots = Array(6) {
        arrayOfNulls<ImageView>(
            4
        )
    }
    private var guesses = Array(6) {
        IntArray(
            4
        )
    }
    private var row = 0
    private var column = 0
    private var solution: IntArray = intArrayOf()
//    private lateinit var resultTextArray: Array<TextView?>
//    private lateinit var resultLayoutArray: Array<LinearLayout?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkockoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initResult()
        initSlots()
        initButtons()
        generateSolution()
//        initArrays()
    }

//    private fun initArrays() {
//        resultTextArray = arrayOf(
//            findViewById(R.id.result0),
//            findViewById(R.id.result1),
//            findViewById(R.id.result2),
//            findViewById(R.id.result3),
//            findViewById(R.id.result4),
//            findViewById(R.id.result5),
//            findViewById(R.id.result6),
//            findViewById(R.id.result7)
//        )
//
//        resultLayoutArray = arrayOf(
//            findViewById(R.id.linear_layout_fields_0),
//            findViewById(R.id.linear_layout_fields_1),
//            findViewById(R.id.linear_layout_fields_2),
//            findViewById(R.id.linear_layout_fields_3),
//            findViewById(R.id.linear_layout_fields_4),
//            findViewById(R.id.linear_layout_fields_5),
//            findViewById(R.id.linear_layout_fields_6),
//            findViewById(R.id.linear_layout_fields_7)
//        )
//    }

    private fun initResult() {
        results[0] = binding.result0
        results[1] = binding.result1
        results[2] = binding.result2
        results[3] = binding.result3
        results[4] = binding.result4
        results[5] = binding.result5
    }

    private fun initSlots() {
        slots[0][0] = binding.slot00
        slots[0][1] = binding.slot01
        slots[0][2] = binding.slot02
        slots[0][3] = binding.slot03
        slots[1][0] = binding.slot10
        slots[1][1] = binding.slot11
        slots[1][2] = binding.slot12
        slots[1][3] = binding.slot13
        slots[2][0] = binding.slot20
        slots[2][1] = binding.slot21
        slots[2][2] = binding.slot22
        slots[2][3] = binding.slot23
        slots[3][0] = binding.slot30
        slots[3][1] = binding.slot31
        slots[3][2] = binding.slot32
        slots[3][3] = binding.slot33
        slots[4][0] = binding.slot40
        slots[4][1] = binding.slot41
        slots[4][2] = binding.slot42
        slots[4][3] = binding.slot43
        slots[5][0] = binding.slot50
        slots[5][1] = binding.slot51
        slots[5][2] = binding.slot52
        slots[5][3] = binding.slot53
        for (i in 0..5) {
            for (j in 0..3) {
                guesses[i][j] = -1
            }
        }
    }

    private fun handleButtonClick(d: Drawable?, id: Int) {
        if (row >= slots.size) {
            return
        }
        slots[row][column]?.setImageDrawable(d)
        guesses[row][column] = id
        column++
        if (column >= 4) {
            column = 0
            checkMatch()
            row++
        }
        if (row == slots.size) {
            showResult()

            moveToNextActivityWithDelay()
        }
    }

    private fun moveToNextActivityWithDelay() {

        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this@Skocko, KorakPoKorak::class.java)
            startActivity(intent)

            finish()
        }, delayMilis)

    }

    private fun handleDelete() {
        if (column == 0) {
            return
        }
        slots[row][column - 1]!!.setImageDrawable(getDrawable(R.drawable.frame))
        guesses[row][column - 1] = -1
        if (column > 0) {
            column--
        }
    }

    // needs fixing

    private fun checkMatch() {
        var correct = 0
        var misplaced = 0
        val confirmed = booleanArrayOf(false, false, false, false)
        for (i in 0..3) {
            if (guesses[row][i] == solution[i]) {
                correct++
                confirmed[i] = true
            }
        }
        for (i in 0..3) {
            if (confirmed[i]) {
                continue
            }
            for (j in 0..3) {
                if (confirmed[j]) {
                    continue
                }
                if (guesses[row][i] == solution[j]) {
                    misplaced++
                    confirmed[j] = true
                    break
                }
            }
        }
        "correct:$correct\nmisplaced:$misplaced".also { results[row]!!.text = it }
        if (correct == 4) {
            showResult()
            showScore()

            moveToNextActivityWithDelay()

        }
    }


//    private fun checkMatch() {
//        val correctColor = ContextCompat.getColor(this, R.color.correctColor)
//        val misplacedColor = ContextCompat.getColor(this, R.color.misplacedColor)
//
//        val indicatorSize = resources.getDimensionPixelSize(R.dimen.indicator_size)
//
//        for (i in 0 until 4) {
//            // Check correct guesses and set the indicator color
//            if (guesses[row][i] == solution[i]) {
//                "correct:${results[row]?.text?.toString()?.toInt()?.plus(1)}\nmisplaced:${results[row]?.text?.toString()?.substringAfter(":")}".also { results[row]?.text = it }
//                setIndicatorColor(resultLayoutArray[row]!!, i, correctColor, indicatorSize)
//            }
//        }
//
//        for (i in 0 until 4) {
//            if (guesses[row][i] != solution[i]) {
//                for (j in 0 until 4) {
//                    if (guesses[row][i] == solution[j]) {
//                        setIndicatorColor(resultLayoutArray[row]!!, i, misplacedColor, indicatorSize)
//                    }
//                }
//            }
//        }
//
//        if (results[row]?.text?.toString()?.startsWith("correct:4") == true) {
//            showResult()
//            showScore()
//        }
//    }
//
//    private fun setIndicatorColor(resultLayout: LinearLayout, position: Int, color: Int, size: Int) {
//        val indicator = View(this)
//        indicator.layoutParams = LinearLayout.LayoutParams(size, size)
//        indicator.setBackgroundColor(color)
//        resultLayout.addView(indicator, position)
//    }

    private fun initButtons() {
        val tref = binding.imageViewTref
        val herc = binding.imageViewherc
        val karo = binding.imageViewKaro
        val pik = binding.imageViewPik
        val cd = binding.imageViewCd
        val zvezda = binding.imageViewZvezda
        val delete = binding.imageViewDelete
        tref.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                0
            )
        }
        herc.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                1
            )
        }
        karo.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                2
            )
        }
        pik.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                3
            )
        }
        cd.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                4
            )
        }
        zvezda.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                5
            )
        }
        delete.setOnClickListener { handleDelete() }
    }


    private fun generateSolution() {
        solution = IntArray(4)
        val generator = Random()
        for (i in 0..3) {
            val number = generator.nextInt(6)
            solution[i] = number
        }
    }

    private fun showScore() {
        var score = 0
        if (row / 2 == 0) {
            score = 20
        } else if (row / 2 == 1) {
            score = 15
        } else if (row / 2 == 2) {
            score = 10
        }
        Toast.makeText(applicationContext, " $score points!", Toast.LENGTH_LONG).show()
    }

    private fun showResult() {
        val slots = arrayOfNulls<ImageView>(4)
        slots[0] = binding.slot70
        slots[1] = binding.slot71
        slots[2] = binding.slot72
        slots[3] = binding.slot73
        for (i in 0..3) {
            when (solution[i]) {
                0 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.tref))
                1 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.herc))
                2 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.karo))
                3 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.pik))
                4 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.cd))
                5 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.zvezda))
            }
        }

        // i need to add buttons for next game
//        val nextGame = findViewById<Button>(R.id.nextGame)
//        nextGame.visibility = View.VISIBLE
    }

}