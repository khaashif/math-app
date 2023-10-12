package com.ze.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.SpannableString
import android.text.Spanned
import android.text.style.SuperscriptSpan
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.ze.app.R.drawable
import com.ze.app.R.layout
import com.ze.app.databinding.FragmentPlayBinding

@Suppress("UNREACHABLE_CODE")
class PlayFragment : Fragment(), RewardedVideoAdListener {

    var ma = MainActivity()
    var tf = TitleFragment()

    var value: Int = 0

    lateinit var mRewardedVideoAd: RewardedVideoAd
    var rewardWatched: Boolean = false
    var grantHint: Boolean = false
    var grantAnswer: Boolean = false
    var ansCount: Int = 0
    var hintCount: Int = 0

    //q1 base xp = 100
    var xp: Int = 0 //base xp =100
    var xpNoHint = 0 //             xp/4  = 100/4=25
    var xpNoAnswer = 0//            xp/2  = 100/2=50
    var xpIncorrectAttempts = 0//   xp - (xp/25)*a   where a=#incorrect answers = 100-(100/25)*1 = 100-(4)=96


    var alwaysOpen = false

    data class Questions(var question: Int)

    lateinit var questions: MutableList<Int>

    //Animation for hint and answer button pulsating effect
    val anim = ScaleAnimation(
        1F,
        1.03F,
        1F,
        1.2F,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )

    //Animation for shaking screen when answer is wrong
    val animShake = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, -0.01F,
        Animation.RELATIVE_TO_PARENT, 0.01f, Animation.RELATIVE_TO_PARENT, 0F,
        Animation.RELATIVE_TO_PARENT, 0F
    )


//    var scaleUp: ScaleAnimation = ScaleAnimation(
//        0.0f, 1.0f, 0f, 1f, Animation.ABSOLUTE, 0F,
//        Animation.RELATIVE_TO_SELF,
//        1F
//    );
//
//
//    var scaleDown: ScaleAnimation = ScaleAnimation(
//        1f, 0f, 1f, 0f, Animation.ABSOLUTE, 0F,
//        Animation.RELATIVE_TO_SELF,
//        1F
//    );

    var topicID = ""


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        anim.duration = 500
        anim.repeatCount = 5
        anim.repeatMode = Animation.REVERSE

        animShake.repeatCount = 10
        animShake.repeatMode = Animation.REVERSE
        animShake.duration = 50

        // Inflate layout using Data Binding class (links fragment to layout)

        var binding: FragmentPlayBinding = DataBindingUtil.inflate(
            inflater, layout.fragment_play, container, false
        )

        var incorrectCounter: Int = 0
        ma.nextLevel(requireActivity())
        xp = ma.currentXP
        xpNoHint = xp / 4
        xpNoAnswer = xp / 2
        xpIncorrectAttempts = xp - (xp / 25) * incorrectCounter
        println("j:: xp = " + xp)
        adMobsPart();


        // Setting colour for home button
//        val spanHome = SpannableString("<<<")
//        var greenCol: Int = Color.parseColor("#FFC0FFBB")
//        var yellowCol: Int = Color.parseColor("#FFF8F6B7")
//        var redCol: Int = Color.parseColor("#F7ADAD")

        var myColorKey: Int

//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            //LIGHT
//            binding.bg?.setBackgroundResource(R.drawable.transition_bg_light)
////            binding.questionImageId.setBackgroundResource(R.drawable.answer_box_light)
//            myColorKey = Color.TRANSPARENT
//            ma.currentQuestion = requireContext().resources.getIdentifier(
//                "l" + ma.currentLevel[2],
//                "drawable",
//                requireContext().packageName
//            )
////            greenCol = Color.parseColor("#085301")
////            yellowCol = Color.parseColor("#DBC600")
////            redCol = Color.parseColor("#BE0000")
////            binding.homeButton.setShadowLayer(5F, 0F, 0F, Color.BLACK)
//        } else {
//            //DARK
//            ma.currentQuestion = requireContext().resources.getIdentifier(
//                "l" + ma.currentLevel[2],
//                "drawable",
//                requireContext().packageName
//            )
//            binding.bg?.setBackgroundResource(R.drawable.transition_bg)
//            myColorKey = Color.WHITE
//
////            binding.homeButton.setShadowLayer(10F, 0F, 0f, myColorKey)
//        }


        var rightSlide = AnimationUtils.loadAnimation(this.requireContext(), R.animator.right_anim)
        var leftSlide = AnimationUtils.loadAnimation(this.requireContext(), R.animator.left_anim)

        var bulb = false

        binding.lightBulb.setOnClickListener {
            bulb = !bulb

            if (bulb) {
                binding.hintButton.startAnimation(rightSlide)
                binding.hintButton.visibility = View.VISIBLE
                binding.answerButton.startAnimation(rightSlide)
                binding.answerButton.visibility = View.VISIBLE
            } else {
                binding.hintButton.startAnimation(leftSlide)
                binding.hintButton.visibility = View.INVISIBLE
                binding.answerButton.startAnimation(leftSlide)
                binding.answerButton.visibility = View.INVISIBLE
            }
        }

        binding.infoButton.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_formulaPage)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.answerBox.showSoftInputOnFocus = false
        } else {
            binding.answerBox.setTextIsSelectable(false)
        }
        binding.drawButton!!.setOnClickListener {
            binding.answerBox.clearFocus()
            var transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.setCustomAnimations(
                R.animator.slow_scale_enter,
                R.animator.slow_scale_exit,
                R.animator.slow_scale_enter,
                R.animator.slow_scale_exit
            )
            transaction.add(R.id.myNavHost, DrawingPageFragment())
            transaction.addToBackStack("draw")
            transaction.commit()
        }

        //Update level number in top right corner of page
        binding.levelNo.text = (ma.levelProgress).toString()

        //Set cursor flashing on answer box
        (binding.answerBox as EditText).requestFocus()

        //Tell home button to go to last page ("pop back stack") when pressed

        var prefMain = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        topicID = prefMain!!.getInt("topic", 1).toString()

        var pref =
            requireActivity().getSharedPreferences("points" + topicID, Context.MODE_PRIVATE)

        var editor = pref.edit()
        println("value is " + value)
//        binding.score!!.text = value.toString() + " points"
        value = pref.getInt("points", 100)
        val hintButton = binding.hintButton

        var userInput = binding.answerBox as TextView
        var userInputText = if (userInput.text != null) {
            userInput.text.toString()
        } else {
            ""
        }

        //Set question image
        questionImage.setImageResource((ma.currentQuestion))

        // Function to handle what buttons do and how to handle cursor positioning when text box updated
        fun cursorPos(string: String) {
            var cursorPos = (binding.answerBox as EditText).selectionEnd
            userInputText =
                userInputText.substring(0, cursorPos) + string + userInputText.substring(
                    cursorPos,
                    userInputText.length
                )
            userInput.text = userInputText
            userInputText = userInput.text.toString()
            (binding.answerBox as EditText).setSelection(cursorPos + string.length)
        }

        //Number Buttons:
////////////////////////////////////////////////////////////////////////// write a function for this
        // Apply above function cursorPos() to each of the buttons
        binding.oneButton.setOnClickListener {
            cursorPos("1")
        }

        binding.twoButton.setOnClickListener {
            cursorPos("2")
        }

        binding.threeButton.setOnClickListener {
            cursorPos("3")
        }

        binding.fourButton.setOnClickListener {
            cursorPos("4")
        }
        binding.fiveButton.setOnClickListener {
            cursorPos("5")
        }
        binding.sixButton.setOnClickListener {
            cursorPos("6")
        }

        binding.sevenButton.setOnClickListener {
            cursorPos("7")
        }
        binding.eightButton.setOnClickListener {
            cursorPos("8")
        }
        binding.nineButton.setOnClickListener {
            cursorPos("9")
        }
        binding.zeroButton.setOnClickListener {
            cursorPos("0")
        }

        //Letter Buttons:
        binding.xButton.setOnClickListener {
            cursorPos("x")
        }

        binding.yButton.setOnClickListener {
            cursorPos("y")
        }

        binding.zButton.setOnClickListener {
            cursorPos("z")
        }

        //Basic Operation Buttons:
        binding.plusButton.setOnClickListener {
            cursorPos("+")
        }

        binding.minusButton.setOnClickListener {
            cursorPos("-")
        }

        binding.divideButton.setOnClickListener {
            cursorPos("/")
        }

        //Adv. Operation Buttons:
        binding.equalButton.setOnClickListener {
            cursorPos("=")
        }

        binding.powerButton.setOnClickListener {
            cursorPos("^")
        }

        binding.sqrtButton.setOnClickListener {
            cursorPos("√(")
        }

        //Trig Buttons:
        binding.sinButton.setOnClickListener {
            cursorPos("sin(")
        }

        binding.cosButton.setOnClickListener {
            cursorPos("cos(")
        }

        binding.tanButton.setOnClickListener {
            cursorPos("tan(")
        }

        //Bracket Buttons:
        binding.openBracket.setOnClickListener {
            cursorPos("(")
        }

        binding.closeBracket.setOnClickListener {
            cursorPos(")")
        }

        //Ln/e^x/pi Buttons:
        binding.lnButton.setOnClickListener {
            cursorPos("ln(")
        }

        binding.expButton.setOnClickListener {
            cursorPos("e^(")
        }

        binding.piButton.setOnClickListener {
            cursorPos("π")
        }

        println("currentQuestion play is: " + ma.currentLevel.toString())
        //Delete Button:
        binding.delButton.setOnClickListener {
            if (userInputText.isNotEmpty()) {
                var cursorPosition = (binding.answerBox as EditText).selectionStart
                var selectionEnd = (binding.answerBox as EditText).selectionEnd
                var diff: Int = (selectionEnd) - (cursorPosition)

                if (diff == userInput.text.length) {
                    userInput.text = ""
                    userInputText = ""
                }

                if (cursorPosition > 0) {

                    if (diff == 0) {
                        userInputText =
                            userInputText.removeRange(cursorPosition - 1, cursorPosition)
                        userInput.text = userInputText
                        (binding.answerBox as EditText).setSelection(cursorPosition - 1)
                        userInputText = userInput.text.toString()
                    }
                    if (diff > 0) {
                        userInput.text =
                            userInput.text.removeRange(selectionEnd - diff, selectionEnd)
                        (binding.answerBox as EditText).setSelection(cursorPosition)
                        userInputText = userInput.text.toString()
                    }
                }
            }
        }

        //Subscript power button
        val powerString = SpannableString("ab")
        powerString.setSpan(SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.powerButton.text = powerString


        var mpCorrect = (activity as MainActivity).mpCorrect
        var mpWrong = (activity as MainActivity).mpWrong

        val transitionBG = binding.bg?.background as TransitionDrawable

//        binding.hintButton!!.setBackgroundResource(drawable.transition_hint)
//        binding.answerButton.setBackgroundResource(drawable.transition_hint)

        var db = ScoreDataBaseHandler(requireActivity())

        if ((activity as MainActivity).vibrationState) {
            binding.vibrationButton.setImageResource(R.drawable.vibration)
        } else {
            binding.vibrationButton.setImageResource(R.drawable.vibration_off)
        }

        binding.vibrationButton.setOnClickListener {
            (activity as MainActivity).vibrationState = !(activity as MainActivity).vibrationState

            if ((activity as MainActivity).vibrationState) {
                binding.vibrationButton.setImageResource(R.drawable.vibration)
            } else {
                binding.vibrationButton.setImageResource(R.drawable.vibration_off)
            }
        }

        var muted: Boolean
        muted = prefMain.getBoolean("AppMute", false)
        println("App is muted: " + muted)

        if (muted) {
            binding.soundButton.setImageResource(R.drawable.sound_off)
            (activity as MainActivity).mp.setVolume(0F, 0F)
            mpCorrect.setVolume(0F, 0F)
            mpWrong.setVolume(0F, 0F)

        } else {
            binding.soundButton.setImageResource(R.drawable.sound)
            (activity as MainActivity).mp.setVolume(1F, 1F)
            mpCorrect.setVolume(1F, 1F)
            mpWrong.setVolume(1F, 1F)
        }

        var editorMain = prefMain.edit()
        binding.soundButton.setOnClickListener {
            muted = prefMain.getBoolean("AppMute", false)
            if (!muted) {
                //mute
                (activity as MainActivity).mp.setVolume(0F, 0F)
                mpCorrect.setVolume(0F, 0F)
                mpWrong.setVolume(0F, 0F)
                editorMain.putBoolean("AppMute", true).apply()
                println("muted     saved was " + muted)

                binding.soundButton.setImageResource(R.drawable.sound_off)
            } else {
                //unmute
                (activity as MainActivity).mp.setVolume(1F, 1F)
                mpCorrect.setVolume(1F, 1F)
                mpWrong.setVolume(1F, 1F)
                editorMain.putBoolean("AppMute", false).apply()
                println("unmuted      saved was " + muted)
                binding.soundButton.setImageResource(R.drawable.sound)
            }
        }

        // Enter Button:
        binding.enterButton.setOnClickListener {
            (binding.answerBox as EditText).clearFocus()

            //If answer is correct then:
            if (userInput.text.toString() != "" && userInput.text.toString() in ma.currentPossibleAnswers) {
                editor?.putInt("points", 100)
                editor?.apply()

                var totalXP = xp + xpNoHint + xpNoAnswer + xpIncorrectAttempts
                var score =
                    QuestionScore(
                        ma.topicAndLevel[0],
                        ma.topicAndLevel[1],
                        xp,
                        xpNoHint,
                        xpNoAnswer,
                        xpIncorrectAttempts,
                        totalXP
                    )
                db.insertData(score)
                db.returnDB()

                var totalScore = db.totalScoreforLeaderboard().toLong()

                if (
                    GoogleSignIn.getLastSignedInAccount(this.requireActivity()) != null
                ) {
                    println("leaderboard " + (GoogleSignIn.getLastSignedInAccount(this.requireActivity()) != null))
                    Games.getLeaderboardsClient(
                        this.requireActivity(),
                        GoogleSignIn.getLastSignedInAccount(this.requireActivity())!!
                    )
                        .submitScore(getString(R.string.leaderboard), totalScore)
                } else {
                    println("leaderboard " + (GoogleSignIn.getLastSignedInAccount(this.requireActivity()) != null))
                }

                clearList()
                clearText()

                value = 100

                //Play correct sound
                if ((activity as MainActivity).soundState) {
                    mpCorrect.start()
                }
                //Phone vibrates
                if ((activity as MainActivity).vibrationState) {
                    vibratePhone(200)
                }

                //Go to correct page
                if (ma.topicAndLevel[0] == 1 && ma.levelProgress == 100) {
                    println("zzz1")
                    findNavController().navigate(R.id.action_playFragment_to_topicCompleteFragment)
                } else if (ma.topicAndLevel[0] > 1 && ma.levelProgress == 20) {
                    println("zzz2")
                    findNavController().navigate(R.id.action_playFragment_to_topicCompleteFragment)
                } else {
                    findNavController().navigate(R.id.action_playFragment_to_playCorrectFragment)
                    hintCount = 0
                    ansCount = 0
                    (binding.answerBox as TextView).text = ""

                }


//                //Variables updated for next question
//                if (ma.levelProgress < 100) {
//
////                    ma.saveLevelData(requireContext())
//                }


            } else {
                //If answer is wrong:

                incorrectCounter = incorrectCounter + 1
                println("j:: incorrectCounter = " + incorrectCounter)
                println("j:: xpIncorrectAttempts = " + xpIncorrectAttempts)
                xpIncorrectAttempts = xp - (xp / 25) * incorrectCounter
                println("j:: xpIncorrectAttempts = " + xpIncorrectAttempts)
                value = if (value < 20) {
                    10
                } else {
                    (value - 10)
                }

                //Play wrong sound
                if ((activity as MainActivity).soundState) {
                    mpWrong.start()
                }

                // Vibrate phone
                if ((activity as MainActivity).vibrationState) {
                    vibratePhone(500)
                }

                //Shake screen
                binding.items.startAnimation(animShake)

                //Screen flashes red
                transitionBG.startTransition(1000)
                transitionBG.reverseTransition(1000)

                //If hint hasnt been pressed yet, user is prompted to press it
//                if (hintCount == 0) {
//                    binding.hintContainer.startAnimation(anim)
//                    trans();
//                } else {
//                    //If it has been pressed then user is prompted to press answer button instead
//                    binding.answerContainer.startAnimation(anim)
//                    var transitionAnswer = binding.answerButton.background as TransitionDrawable
//                    transitionAnswer.startTransition(3000)
//                    transitionAnswer.reverseTransition(3000)
//                }
                //Keeps user there
                userInputText = if (userInput.text == null) {
                    ""
                } else {
                    userInput.text.toString()
                }
            }
            //Stops cursor from disappearing
            binding.answerBox.requestFocus()
        }

        // Hint Button:
        binding.hintButton.setOnClickListener {
            if (hintCount > 0) {
                showHint()
            } else {
                if (mRewardedVideoAd.isLoaded) {
                    grantHint = true
                    mRewardedVideoAd.show()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Make sure you're connected to the internet & please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Answer Button:
//        binding.answerButton.setOnClickListener {
//            if (ansCount > 0) {
//                showAnswer()
//
//            } else {
//                if (hintCount == 0) {
//                    binding.hintContainer!!.startAnimation(anim)
//                    trans();
//                }
//                if (hintCount > 0 && mRewardedVideoAd.isLoaded) {
//                    grantAnswer = true
//                    mRewardedVideoAd.show()
//                }
//            }
//        }

        return binding.root

        // Full Screen Mode
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            activity?.window?.decorView?.systemUiVisibility =
//                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        }
    }

    override fun onStop() {
        super.onStop()
        var pref =
            requireActivity().getSharedPreferences("points" + topicID, Context.MODE_PRIVATE)
        var editor = pref?.edit()
        println("value saved is onstop" + value)
        editor?.putInt("points", value)
        editor?.apply()
    }

    override fun onPause() {
        super.onPause()
        var pref =
            requireActivity().getSharedPreferences("points" + topicID, Context.MODE_PRIVATE)
        var editor = pref?.edit()
        println("value saved is onpause" + value)
        editor?.putInt("points", value)
        editor?.apply()
    }

    fun showAnswer() {
        var dialog: Dialog = Dialog(this.context!!)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val trans: ColorDrawable = ColorDrawable(Color.TRANSPARENT)
        dialog.setContentView(R.layout.answer_popup)
        dialog.window!!.setBackgroundDrawable(trans)
        dialog.window!!.setDimAmount(0.95F)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setGravity(Gravity.CENTER_VERTICAL)
        val img = dialog.findViewById<ImageView>(R.id.answerImage)
        img.layoutParams.height = (ViewGroup.LayoutParams.WRAP_CONTENT)
        img.layoutParams.width = (ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        img.setImageResource(ma.currentAnswer)
        dialog.window!!.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)

        var height: Int = (displayMetrics.heightPixels * 0.5).toInt()

        img.layoutParams.height = (height)
        img.layoutParams.width = (ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
    }

    fun showHint() {
        var dialog: Dialog = Dialog(this.requireContext())
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val trans: ColorDrawable = ColorDrawable(Color.TRANSPARENT)
        dialog.setContentView(R.layout.hint_popup)
        dialog.window!!.setBackgroundDrawable(trans)
        dialog.window!!.setDimAmount(0.95F)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setGravity(Gravity.CENTER_VERTICAL)
        val img = dialog.findViewById<ImageView>(R.id.hintImage)
        img.setImageResource(ma.currentHint)
        dialog.window!!.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)

        var height: Int = (displayMetrics.heightPixels * 0.5).toInt()

        img.layoutParams.height = (height)
        img.layoutParams.width = (ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
    }

    override fun onRewarded(p0: RewardItem?) {
        rewardWatched = true
    }

    override fun onRewardedVideoAdClosed() {
        if (rewardWatched) {
            if (grantHint) {
                showHint()
                hintCount++
                grantHint = false
            }
            if (grantAnswer) {
                showAnswer()
                ansCount++
                grantAnswer = false
            }
        }

        rewardWatched = false
        mRewardedVideoAd.loadAd(
            "ca-app-pub-4385048141375400/2964529362",
            AdRequest.Builder().build()
        )
    }

    override fun onRewardedVideoAdLeftApplication() {
    }

    override fun onRewardedVideoAdLoaded() {
    }

    override fun onRewardedVideoAdOpened() {
    }

    override fun onRewardedVideoCompleted() {
    }

    override fun onRewardedVideoStarted() {
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
    }


    //Function created to setup adverts, insert advert links below
    fun adMobsPart() {

        MobileAds.initialize(
            context,
            "ca-app-pub-4385048141375400~9446021707"
        )
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
        mRewardedVideoAd.rewardedVideoAdListener = (this)
        mRewardedVideoAd.loadAd(
            "ca-app-pub-4385048141375400/2964529362",

            AdRequest.Builder().build()
        )

        if (mRewardedVideoAd.isLoaded) {
            Toast.makeText(requireActivity(), "Loaded", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        var pathList = ArrayList<Path>()
        var drawPageText = ""
    }

    fun saveUserCanvas(pList: ArrayList<Path>, text: String) {
        pathList = pList
        drawPageText = text
    }

    fun getSavedText(): String {
        return drawPageText
    }

    fun getList(): ArrayList<Path> {
        return pathList
    }

    fun clearList() {
        pathList.clear()
    }

    fun clearText() {
        drawPageText = ""
    }

    // Maintain fullscreen mode when returning to play page
    override fun onResume() {
        super.onResume()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            activity?.window?.decorView?.systemUiVisibility =
//                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        }
    }
}

// Create function to vibrate phone
fun Fragment.vibratePhone(int: Int) {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                int.toLong(),
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        vibrator.vibrate(int.toLong())
    }


}









