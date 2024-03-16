package com.deksi.graduationquiz.slagalica.activities


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivityKoZnaZnaBinding
import com.deksi.graduationquiz.slagalica.api.KoZnaZnaApiService
import com.deksi.graduationquiz.slagalica.fragments.TutorialDialogFragment
import com.deksi.graduationquiz.slagalica.model.KoZnaZnaModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class KoZnaZna : AppCompatActivity(), TutorialDialogFragment.CloseButtonClickListener {

    private lateinit var binding: ActivityKoZnaZnaBinding
    private var questions: List<KoZnaZnaModel> = emptyList()
    private var currentQuestionIndex: Int = 0
    private var timeLeft: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var userAnswered: Boolean = false
    private var currentQuestionNumber = 1
    private var totalScore = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoZnaZnaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getRoundData()
        onCreateProgressDialog()
        setUpActionBar()
        startBackgroundMusic()

    }

    private fun displayData() {
        val question = binding.textViewQuestion
        val option1 = binding.buttonOption1
        val option2 = binding.buttonOption2
        val option3 = binding.buttonOption3
        val option4 = binding.buttonOption4

        val currentQuestion = questions[currentQuestionIndex]
        "$currentQuestionNumber/5".also { binding.textViewNumberOfQuestions.text = it }


        question.text = currentQuestion.question
        option1.text = currentQuestion.option1
        option2.text = currentQuestion.option2
        option3.text = currentQuestion.option3
        option4.text = currentQuestion.option4

        option1.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option1, option1)
        }

        option2.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option2, option2)
        }

        option3.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option3, option3)
        }

        option4.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option4, option4)
        }
        userAnswered = false

        binding.buttonNextQuestion.setOnClickListener {

            if (!userAnswered) {
//                Toast.makeText(this@KoZnaZna, "Please select an option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Reset the background of all buttons to their default state
            resetButtonBackgrounds()
            currentQuestionNumber++

            // Increment the index to move to the next question
            currentQuestionIndex++

            // Check if the index is within bounds
            if (currentQuestionIndex < questions.size) {
                // If within bounds, get the next question
                val nextQuestion = questions[currentQuestionIndex]

                // Display the next question
                displayData()
            } else {

                getSavedLanguageBySharedPreferences()

                val messageResourceId =
                    resources.getIdentifier("koznazna_submit", "string", packageName)
                val message = if (messageResourceId != 0) {
                    getString(messageResourceId)
                } else {
                    getString(R.string.koznazna_submit)
                }

                userAnswered = true
                stopTimer()
                val buttonNext = binding.buttonNextQuestion
                buttonNext.text = message

                buttonNext.setOnClickListener {
                    showProgressDialogOnGameEnd()
                    moveToTheNextActivityWithDelay()

                }
            }
        }
    }

    private fun handleButtonClick(
        question: KoZnaZnaModel,
        selectedOption: String,
        clickedButton: Button
    ) {
        if (userAnswered) {
            // Ignore the click if the user has already answered
            return
        }

        if (selectedOption == question.answer) {
            // Correct answer, update UI (change color to green)
            clickedButton.setBackgroundResource(R.drawable.round_green_reveal)
            totalScore += 10
        } else {
            // Incorrect answer, update UI (change color to red)
            clickedButton.setBackgroundResource(R.drawable.round_red)

            // Display the correct answer (change color to green)
            when (question.answer) {
                question.option1 -> binding.buttonOption1.setBackgroundResource(R.drawable.round_green_reveal)
                question.option2 -> binding.buttonOption2.setBackgroundResource(R.drawable.round_green_reveal)
                question.option3 -> binding.buttonOption3.setBackgroundResource(R.drawable.round_green_reveal)
                question.option4 -> binding.buttonOption4.setBackgroundResource(R.drawable.round_green_reveal)


            }
            totalScore -= 5
        }
        userAnswered = true
    }

    private fun resetButtonBackgrounds() {
        binding.buttonOption1.setBackgroundResource(R.drawable.round_white)
        binding.buttonOption2.setBackgroundResource(R.drawable.round_white)
        binding.buttonOption3.setBackgroundResource(R.drawable.round_white)
        binding.buttonOption4.setBackgroundResource(R.drawable.round_white)

        userAnswered = false
    }


    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(25000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                showProgressDialogOnTimeout()
                moveToTheNextActivityWithDelay()
            }
        }
        (timeLeft as CountDownTimer).start()
    }


    private fun startTimerForProgressDialog() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (totalTime - millisUntilFinished).toInt()

                val messageResourceId =
                    resources.getIdentifier("timer_score", "string", packageName)
                val messageScore = if (messageResourceId != 0) {
                    getString(messageResourceId)
                } else {
                    getString(R.string.timer_score)
                }


                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining     $messageScore: $totalScore"
                progressDialog?.setMessage(message)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun stopTimer() {
        timeLeft?.cancel()
    }

    private fun moveToTheNextActivityWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            saveTotalScoreToLocalPreferences()
            val intent = Intent(this@KoZnaZna, Spojnice::class.java)
            startActivity(intent)

            finish()
        }, delayMilis)
    }

    private fun onCreateProgressDialog() {
        getSavedLanguageBySharedPreferences()

        val titleResourceId =
            resources.getIdentifier("koznazna_title_on_create", "string", packageName)
        val title = if (titleResourceId != 0) {
            getString(titleResourceId)
        } else {
            getString(R.string.koznazna_title_on_create)
        }

        val messageResourceId =
            resources.getIdentifier("koznazna_message_on_create", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.koznazna_message_on_create)
        }

        progressDialog = ProgressDialog.show(this, title, message, true, false)
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnTimeout() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId =
            resources.getIdentifier("koznazna_message_time_up", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.koznazna_message_time_up)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerForProgressDialog()
    }

    private fun showProgressDialogOnGameEnd() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId =
            resources.getIdentifier("koznazna_message_on_finish", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.koznazna_message_on_finish)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerForProgressDialog()
    }

    private fun saveTotalScoreToLocalPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the existing total score
        val currentTotalScore = sharedPreferences.getInt("totalScore", 0)

        // Add the local total score to the overall total score
        val newTotalScore = currentTotalScore + totalScore

        // Save the updated total score
        editor.putInt("totalScore", newTotalScore)
        editor.apply()
    }


    private fun clearTotalScoreFromPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the totalScore key from SharedPreferences
        editor.remove("totalScore")
        editor.apply()
    }

    private fun getRoundData() {

        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"

        val username = intent.getStringExtra("username").toString()
        val tutorialSharedPreferences = getSharedPreferences("TutorialPreferences", Context.MODE_PRIVATE)

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.9:8080/api/koznazna/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val koZnaZnaApiService = retrofit.create(KoZnaZnaApiService::class.java)

        val call = koZnaZnaApiService.getRandomRounds(savedLanguage)

        call.enqueue(object : Callback<List<KoZnaZnaModel>> {
            override fun onResponse(
                call: Call<List<KoZnaZnaModel>>,
                response: Response<List<KoZnaZnaModel>>
            ) {
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                    val isTutorialShown = tutorialSharedPreferences.getBoolean(username, false)
                    Log.d("KoZnaZna", "Is tutorial shown for $username: $isTutorialShown")
                    if (!isTutorialShown) {
                        showTutorialDialog(object :
                            TutorialDialogFragment.CloseButtonClickListener {
                            override fun onCloseButtonClicked() {
                                initTimer()
                                displayData()
                                tutorialSharedPreferences.edit().putBoolean(username, true).apply()
                            }
                        }, username)
                    } else {
                        // Tutorial already shown for this email, proceed with existing logic
                        initTimer()
                        displayData()
                    }
                    dismissProgressDialog()

                } else {
                    Toast.makeText(this@KoZnaZna, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<KoZnaZnaModel>>, t: Throwable) {
                Toast.makeText(this@KoZnaZna, "API call failed", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onCloseButtonClicked() {
        initTimer()
        displayData()
    }

    private fun showTutorialDialog(listener: TutorialDialogFragment.CloseButtonClickListener, username: String?) {
        getSavedLanguageBySharedPreferences()

        /** Welcome text **/
        val welcomeTitleResourceId = resources.getIdentifier("welcome_title", "string", packageName)
        val welcomeTitle = if (welcomeTitleResourceId != 0) {
            getString(welcomeTitleResourceId)
        } else {
            getString(R.string.welcome_title)
        }

        val welcomeMessageResourceId =
            resources.getIdentifier("welcome_message", "string", packageName)
        val welcomeMessage = if (welcomeMessageResourceId != 0) {
            getString(welcomeMessageResourceId)
        } else {
            getString(R.string.welcome_message)
        }

        /** KoZnaZna text **/
        val koznaznaTitleResourceId =
            resources.getIdentifier("koznazna_title", "string", packageName)
        val koznaznaTitle = if (koznaznaTitleResourceId != 0) {
            getString(koznaznaTitleResourceId)
        } else {
            getString(R.string.koznazna_title)
        }

        val koznaznaMessageResourceId =
            resources.getIdentifier("koznazna_message", "string", packageName)
        val koznaznaMessage = if (koznaznaMessageResourceId != 0) {
            getString(koznaznaMessageResourceId)
        } else {
            getString(R.string.koznazna_message)
        }

        /** Spojnice text **/
        val spojniceTitleResourceId =
            resources.getIdentifier("spojnice_title", "string", packageName)
        val spojniceTitle = if (spojniceTitleResourceId != 0) {
            getString(spojniceTitleResourceId)
        } else {
            getString(R.string.spojnice_title)
        }

        val spojniceMessageResourceId =
            resources.getIdentifier("spojnice_message", "string", packageName)
        val spojniceMessage = if (spojniceMessageResourceId != 0) {
            getString(spojniceMessageResourceId)
        } else {
            getString(R.string.spojnice_message)
        }

        /** Asocijacije text **/
        val asocijacijeTitleResourceId =
            resources.getIdentifier("asocijacije_title", "string", packageName)
        val asocijacijeTitle = if (asocijacijeTitleResourceId != 0) {
            getString(asocijacijeTitleResourceId)
        } else {
            getString(R.string.asocijacije_title)
        }

        val asocijacijeMessageResourceId =
            resources.getIdentifier("asocijacije_message", "string", packageName)
        val asocijacijeMessage = if (asocijacijeMessageResourceId != 0) {
            getString(asocijacijeMessageResourceId)
        } else {
            getString(R.string.asocijacije_message)
        }

        /** Skocko text **/
        val skockoTitleResourceId = resources.getIdentifier("skocko_title", "string", packageName)
        val skockoTitle = if (skockoTitleResourceId != 0) {
            getString(skockoTitleResourceId)
        } else {
            getString(R.string.skocko_title)
        }

        val skockoMessageResourceId =
            resources.getIdentifier("skocko_message", "string", packageName)
        val skockoMessage = if (skockoMessageResourceId != 0) {
            getString(skockoMessageResourceId)
        } else {
            getString(R.string.skocko_message)
        }

        /** Korak po korak text **/
        val korakpokorakTitleResourceId =
            resources.getIdentifier("korakpokorak_title", "string", packageName)
        val korakpokorakTitle = if (korakpokorakTitleResourceId != 0) {
            getString(korakpokorakTitleResourceId)
        } else {
            getString(R.string.korakpokorak_title)
        }

        val korakpokorakMessageResourceId =
            resources.getIdentifier("korakpokorak_message", "string", packageName)
        val korakpokorakMessage = if (korakpokorakMessageResourceId != 0) {
            getString(korakpokorakMessageResourceId)
        } else {
            getString(R.string.korakpokorak_message)
        }

        /** Moj broj text **/
        val mojbrojTitleResourceId = resources.getIdentifier("mojbroj_title", "string", packageName)
        val mojbrojTitle = if (mojbrojTitleResourceId != 0) {
            getString(mojbrojTitleResourceId)
        } else {
            getString(R.string.mojbroj_title)
        }

        val mojbrojMessageResourceId =
            resources.getIdentifier("mojbroj_message", "string", packageName)
        val mojbrojMessage = if (mojbrojMessageResourceId != 0) {
            getString(mojbrojMessageResourceId)
        } else {
            getString(R.string.mojbroj_message)
        }

        /** List of titles and messages **/
        val textList = listOf(
            Pair(welcomeTitle, welcomeMessage),
            Pair(koznaznaTitle, koznaznaMessage),
            Pair(spojniceTitle, spojniceMessage),
            Pair(asocijacijeTitle, asocijacijeMessage),
            Pair(skockoTitle, skockoMessage),
            Pair(korakpokorakTitle, korakpokorakMessage),
            Pair(mojbrojTitle, mojbrojMessage)

        )
        val dialogFragment = TutorialDialogFragment.newInstance(textList, listener, username ?: "")
        dialogFragment.show(supportFragmentManager, "TutorialDialogFragment")
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title)

        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Ko zna zna"
        titleTextView?.gravity = Gravity.CENTER
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
    }

    override fun onBackPressed() {

        val messageResourceId =
            resources.getIdentifier("on_back_pressed_game_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.on_back_pressed_game_message)
        }

        val yesResourceId = resources.getIdentifier("yes", "string", packageName)
        val yes = if (yesResourceId != 0) {
            getString(yesResourceId)
        } else {
            getString(R.string.yes)
        }

        val noResourceId = resources.getIdentifier("no", "string", packageName)
        val no = if (noResourceId != 0) {
            getString(noResourceId)
        } else {
            getString(R.string.no)
        }

        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(yes) { _, _ ->
                super.onBackPressed()
                clearTotalScoreFromPreferences()
            }
            .setNegativeButton(no) { _, _ ->
                // Do nothing
            }
            .show()

    }

    private fun startBackgroundMusic() {
        MediaPlayerManager.initMediaPlayer(this, R.raw.sample_music)

        MediaPlayerManager.start()
    }

}