package com.ze.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ze.app.databinding.FragmentTopicsLevelsBinding
import kotlin.properties.Delegates

class topicsLevels : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTopicsLevelsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_topics_levels, container, false
        )
        val pref = this.activity?.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)

        val editor = pref?.edit()

        var levelButtons = mutableListOf(
            (binding.b1), (binding.b2), (binding.b3), (binding.b4),
            (binding.b5), (binding.b6), (binding.b7), (binding.b8),
            (binding.b9), (binding.b10), (binding.b11), (binding.b12),
            (binding.b13), (binding.b14), (binding.b15), (binding.b16),
            (binding.b17), (binding.b18), (binding.b19), (binding.b20)
        )

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager!!.defaultDisplay!!.getMetrics(displayMetrics)

        var screenWidth: Float = displayMetrics.widthPixels.toFloat()

        var px: Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, resources.displayMetrics)

        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            0F,
            resources.displayMetrics
        ).toInt()

        val height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            60F,
            resources.displayMetrics
        ).toInt()

        val textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            9F,
            resources.displayMetrics
        )


        var topicID = pref!!.getInt(
            "selectedTopic",
            1
        ) //Retrieves topic user selected on previous page // TO DO..Pick this up in play page to set appropriate question (or just update through main activity)

        var db = ScoreDataBaseHandler(requireActivity())
        var maxLevelSelectedTopic =
            db.retrieveMaxLevelSelectedTopic(topicID) // Use this to displayyyyyyyyyyyyyyyyyyyy appropriate number of unlocked levels           --DONE
println("j: maxlvl for this topic is "+ maxLevelSelectedTopic)
        // TO DO...Depending on which number button clicked, appropriate topic + level combination used to obtain info in csv file to display in app                         --DONE

        for (k in 0..binding.mainGrid.childCount - 1) {
            var child: Button = binding.mainGrid.getChildAt(k) as Button
            child.layoutParams.height = ((screenWidth - px) / 5).toInt()
            if (k > maxLevelSelectedTopic) {
                child.background = resources.getDrawable(R.drawable.button_level_locked)
                child.setTextColor(Color.GRAY)
            } else {
                child.setOnClickListener {
                    editor!!.putInt("selectedLevel", k+1)
                    println("j: selectedlvlk+!: "+(k+1).toString())
                    editor.apply()
                    println("step 2: selectedLevel = " + (k + 1).toString())
                    findNavController().navigate(R.id.action_topicsLevels_to_playFragment) //pick up topic + level variables in play page
                }
            }
        }

//        for (button in levelButtons) {
//            button?.setBackgroundResource(lvlBtnBg)
//            button?.setShadowLayer(5F, 0F, 0F, Color.BLACK)
//            button?.textSize = textSize
//            button.height = ((screenWidth - px) / 5).toInt()
//            var index = levelButtons.indexOf(button)
//            button?.setOnClickListener {
//                if (index < levelProgress) {
//                    findNavController().navigate(R.id.action_topicsLevels_to_playFragment)
//                }
//                if (index< levelProgress-1){
//                    editor?.putInt("tempLevel", (index+1))
//                    editor?.apply()
//                }else{
//                    editor?.putInt("tempLevel", 0)
//                    editor?.apply()
//                }
//            }
//        }

        for (k in 0..19) {
            if (k > maxLevelSelectedTopic) {

            }
        }


        //////////////////////////////////////////////////// 2 CHECK IF DELETABLE START ///////////////////////////////////////////////////////
//        var n1: Int =
//            when (topicID) {
//                in IntRange(2, 6) -> 2
//                in IntRange(7, 11) -> 7
//                in IntRange(12, 16) -> 12
//                else -> 0
//            }
//        var n2: Int =
//            when (topicID) {
//                in IntRange(2, 6) -> 6
//                in IntRange(7, 11) -> 11
//                in IntRange(12, 16) -> 16
//                else -> 0
//            }


        val spanHome = SpannableString("<<<")
        var greenCol: Int = Color.parseColor("#FFC0FFBB")
        var yellowCol: Int = Color.parseColor("#FFF8F6B7")
        var redCol: Int = Color.parseColor("#F7ADAD")
        spanHome.setSpan(ForegroundColorSpan(greenCol), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanHome.setSpan(ForegroundColorSpan(yellowCol), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanHome.setSpan(ForegroundColorSpan(redCol), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //Tell home button to go to last page when pressed


        //////////////////////////////////////////////////// 2 CHECK IF DELETABLE END ///////////////////////////////////////////////////////

        return binding.root
    }


}
