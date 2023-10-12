package com.ze.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ze.app.databinding.FragmentTopicCompleteBinding
import com.ze.app.databinding.FragmentWinBinding


class TopicCompleteFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTopicCompleteBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_topic_complete, container, false
        )

        //display score for level 20

        val prefMain = this.activity?.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)

        var topic = prefMain!!.getInt("selectedTopic", 1)
        var level = prefMain.getInt("selectedLevel", 1)
        var max: Int = 1
        var topicText = ""


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

        //display topic completion progression bar
        //next button redirects to topics page, see brief animation for new topic unlocked

        var param0 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f)
        var param4 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 4.0f)

        binding.nextButton.setOnClickListener {
            binding.levelSummary.visibility = View.INVISIBLE
            binding.levelSummary.layoutParams = param0

            binding.topicSummary.visibility = View.VISIBLE
            binding.topicSummary.layoutParams = param4
        }

        binding.topicProgressBar.max = 100 //calc topic progress from csv
        binding.topicProgressBar.progress = 77 //fun to calculate topic xp obtained
        var editor = prefMain.edit()
        binding.nextButton1.setOnClickListener {
            editor.putBoolean("topicComplete", true)
            editor.apply()

            var newTopicEntry : QuestionScore = QuestionScore(topic+1, 0, 0,0,0,0,0) //this row will be read when TopicsFragment page opens and displays correct number of unlocked topics
            db.insertData(newTopicEntry)

            MainActivity().updateTopic(requireActivity())
            //update topic and level variables in main ac
            findNavController().popBackStack(R.id.topicsFragment, false)
        }

        return binding.root

    }
}