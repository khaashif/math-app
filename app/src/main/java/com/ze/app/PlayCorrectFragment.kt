package com.ze.app

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ze.app.databinding.FragmentCorrectBinding


class PlayCorrectFragment : Fragment() {

    var i = 0;
    fun loadData(): Int {
        val pref = this.activity?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        i = pref!!.getInt("level", i)
        return i
    }

    var anim = ScaleAnimation(
        1F,
        1.1F,
        1F,
        1.1F,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )

    var animNextButton = ScaleAnimation(
        1F,
        1.03F,
        1F,
        1.03F,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )

    @SuppressLint("WrongConstant", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val binding: FragmentCorrectBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_correct, container, false
        )

        i = loadData();

        val myColor: Int = Color.parseColor("#FFDF00")
        val colLime: Int = Color.parseColor("#00FF00")

//        binding.correctText.setShadowLayer(30F, 0F, 0F, colLime)

        anim.duration = 800
        anim.repeatCount = Animation.INFINITE
        anim.repeatMode = Animation.REVERSE


        val col: Int = Color.parseColor("#0A002E")

        var nextColor: ObjectAnimator =
            ObjectAnimator.ofInt(binding.nextButton, "textColor", col, Color.WHITE)
        nextColor.setEvaluator(android.animation.ArgbEvaluator())

        nextColor.repeatCount = Animation.INFINITE
        nextColor.repeatMode = Animation.REVERSE
        nextColor.duration = 1000

//        val typeface: Typeface? = ResourcesCompat.getFont(activity!!,
//            R.font.agencyfb_normal
//        )

        val handler = object : Handler() {};
//        val runnable = Runnable {
//
//            var startTime: Long = System.currentTimeMillis()
//            binding.quote.setCharacterDelay(30)
//            binding.quote.setText("")
//            binding.quote.gravity = Gravity.CENTER
//            var random: String = quotes.random()
//            binding.quote.animateText(random)
//            binding.quote.setTypeface(typeface, Typeface.ITALIC)
//
//            var endTime: Long = System.currentTimeMillis()
//            var runTime: Long = startTime - endTime
//        }
//
//        handler.postDelayed(runnable, 400)
//
        val nextRunnable = Runnable {
            binding.nextButton.visibility = View.VISIBLE
            nextColor.start()
        }

        handler.postDelayed(nextRunnable, 1500)

//        var trans = binding.glowLayout.background as TransitionDrawable
//        trans.startTransition(1750)
//        trans.reverseTransition(1750)


        val prefMain = this.activity?.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)

        var topic = prefMain!!.getInt("selectedTopic", 1)
        var level = prefMain!!.getInt("selectedLevel", 1)
        var max: Int = 1
        var topicText = ""
        MainActivity().updateLevel(requireActivity())

        var db = ScoreDataBaseHandler(requireActivity())

        var xpObtained = db.retrieveXP(topic, level)

        binding.xpBar.progress = 99
        binding.xpBar.max = max

        binding.xpLvlCompleted.text = "Completion " + (xpObtained.xp + xpObtained.xp).toString()
        binding.xpNoHint.text = "No Hint " + xpObtained.xpNoHint.toString()
        binding.xpNoAns.text = "No Answer " + xpObtained.xpNoAnswer.toString()
        binding.xpIncorrectAttempts.text =
            "Incorrect Attempts -" + (xpObtained.xp - xpObtained.xpIncorrectAttempts).toString()
        binding.xpTotal.text = "Total XP " + xpObtained.xpTotal.toString() + "XP"

        binding.nextButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}
