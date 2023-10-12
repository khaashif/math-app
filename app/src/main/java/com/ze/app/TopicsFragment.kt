package com.ze.app


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.transition.Transition
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_drawing_page.view.*
import com.ze.app.databinding.FragmentTopicsBinding

/**
 * A simple [Fragment] subclass.
 */
class TopicsFragment : Fragment() {

    @SuppressLint("SetTextI18n")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentTopicsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_topics, container, false
        )

        var db = ScoreDataBaseHandler(requireActivity())
        var maxLevelList =
            db.retrieveMaxLevel() // function to obtain user's highest level - this is then used to display topics user has unlocked
//        var maxTopic: Int = if (maxLevelList[0] == 1) {
//            2
//        } else {
//            maxLevelList[0]
//        }
        var maxTopic: Int = maxLevelList[0]
        println("maxTopic: " + maxTopic)
        var maxLevel = maxLevelList[1]

        var displayMetrics = DisplayMetrics()
        requireActivity().windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)

        var screenHeight = displayMetrics.heightPixels.toFloat()

        var childlist = binding.grid.children.toList()

        var pref = requireActivity().getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
        var editor = pref.edit()


        if (pref.getInt("selectedTopic", 0) > maxTopic) {
            maxTopic = pref.getInt("selectedTopic", 0)
        }

        if (pref.getBoolean("topicComplete", false)) {
            //user arrived to this fragment via topic complete fragment
            println("tf: here")

            var topicButton: Button = binding.grid.getChildAt(maxTopic - 1) as Button

            topicButton.background =
                resources.getDrawable(R.drawable.transition_bg_light)
            var transBG = topicButton.background as TransitionDrawable

            transBG.startTransition(1000)

            editor.putBoolean("topicComplete", false)
            editor.apply()
        } else {
            //user arrived to this fragment via usual route
            println("tf: there")
        }

        for (k in 0..binding.grid.childCount - 1) {
            var child: Button = (binding.grid.getChildAt(k) as Button)
            println("k is " + k + " maxTopic is " + maxTopic)
            if (k + 1 <= maxTopic) {
                child.setOnClickListener {
                    editor.putInt("selectedTopic", k + 1)
                    println("step 1: selectedTopic = " + (k + 1).toString())
                    editor.apply()
                    findNavController().navigate(R.id.action_topicsFragment_to_topicsLevels)
                }
            } else {
                child.background = resources.getDrawable(R.drawable.button_topics_locked)
                child.setTextColor(Color.GRAY)
            }
        }

        for (k in childlist) {
            k.layoutParams.height = (screenHeight / 7).toInt()
//            k.foreground = resources.getDrawable(R.drawable.padlock)
        }

//        binding.classic.foreground = resources.getDrawable(R.drawable.ripple_keyboard)
//        binding.arithmetic.foreground = resources.getDrawable(R.drawable.ripple_keyboard)


        binding.classic.setOnClickListener {
            editor.putInt("selectedTopic", 1)
            editor.apply()
            findNavController().navigate(R.id.action_topicsFragment_to_levelsFragment)
        }

//        binding.homeButton.setOnClickListener { findNavController().popBackStack() }

        return binding.root
    }


}
