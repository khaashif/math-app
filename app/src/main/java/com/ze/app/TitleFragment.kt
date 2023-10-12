package com.ze.app


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.games.Games
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.fragment_title.*
import com.ze.app.databinding.FragmentTitleBinding


class TitleFragment : Fragment() {

    fun showLeaderboard() {
        println("rrresult: function called")
        Games.getLeaderboardsClient(
            this.requireActivity(),
            GoogleSignIn.getLastSignedInAccount(this.requireActivity())!!
        )
            .getLeaderboardIntent(getString(R.string.leaderboard))
            .addOnSuccessListener(OnSuccessListener<Intent>() {
                startActivityForResult(it, 100)
            }
            );
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTitleBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_title, container, false
            )

        var counter = 0
        val pref = this.activity?.getSharedPreferences("pref", Context.MODE_PRIVATE)

        fun isSignedIn(): Boolean {
            return (GoogleSignIn.getLastSignedInAccount(this.requireActivity()) != null)
        }

        var shadowFloat: Float

//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            binding.xyz.setImageResource(R.drawable.xyzplay_l)
//            binding.bg!!.setBackgroundResource(R.drawable.bg_light)
//            shadowFloat = 10f
//        } else {
//            binding.xyz.setImageResource(R.drawable.xyzplay_l)
//            binding.bg!!.setBackgroundResource(R.drawable.bg)
//            shadowFloat = 20F
//        }
//
        binding.privacyPolicy?.setOnClickListener {
            findNavController().navigate(R.id.action_titleFragment_to_webFragment)
        }

        binding.forward.setOnClickListener {
            when (counter) {
                0 -> {
                    counter += 1
                    playButton.visibility = View.INVISIBLE
                    leaderboardButton.visibility = View.VISIBLE
                }
                1 -> {
                    counter += 1
                    leaderboardButton.visibility = View.INVISIBLE
                    settingsButton.visibility = View.VISIBLE
                }
            }
        }


        binding.back.setOnClickListener {
            when (counter) {
                2 -> {
                    counter -= 1
                    settingsButton.visibility = View.INVISIBLE
                    leaderboardButton.visibility = View.VISIBLE
                }
                1 -> {
                    counter -= 1
                    leaderboardButton.visibility = View.INVISIBLE
                    playButton.visibility = View.VISIBLE
                }
            }
        }

        binding.settingsButton.setOnClickListener { findNavController().navigate(R.id.action_titleFragment_to_settingsFragment) }
        binding.playButton.setOnClickListener {
            findNavController().navigate(R.id.action_titleFragment_to_topicsFragment)
        }

//        binding.levelsButton.setOnClickListener { findNavController().navigate(R.id.action_titleFragment_to_levelsFragment) }
//        binding.topicsButton.setOnClickListener { findNavController().navigate(R.id.action_titleFragment_to_topicsFragment) }
//        binding.instaButton.setOnClickListener {
//            startActivity(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("http://www.instagram.com/xyz.math")
//                )
//            )
//        }

        var ma = MainActivity()
        var gso: GoogleSignInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        var mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent

        var muted: Boolean
        muted = pref!!.getBoolean("AppMute", false)

        if (muted) {
            (activity as MainActivity).mp.setVolume(0F, 0F)

        } else {
            (activity as MainActivity).mp.setVolume(1F, 1F)
        }

        fun promptSignIn(context: Context) {
            startActivityForResult(signInIntent, 100)
        }


        binding.leaderboardButton.setOnClickListener {
            if (isSignedIn()) {
                println("worked")
                showLeaderboard()
            } else {
//                promptSignIn(this.requireActivity())
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            var result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                var signedInAccount: GoogleSignInAccount = result.signInAccount!!
                showLeaderboard()
            } else {
                println("rrresult is: false")
                println("rrresult is: " + result.status.statusCode.toString())
            }
        }
    }
}