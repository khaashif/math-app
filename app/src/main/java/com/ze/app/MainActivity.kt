package com.ze.app

import android.app.LauncherActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.games.Games
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.ze.app.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    var mp: MediaPlayer = MediaPlayer()
    var mpWrong: MediaPlayer = MediaPlayer()
    var mpCorrect: MediaPlayer = MediaPlayer()

    var soundState: Boolean = true
    var vibrationState: Boolean = true
    var musicState: Boolean = true

    fun saveData() {
        val pref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = pref?.edit()
        editor?.putBoolean("sound", soundState)
        editor?.putBoolean("vibration", vibrationState)
        editor?.putBoolean("music", musicState)
        editor?.apply()
    }

    fun loadSound(): Boolean {
        val pref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        soundState = pref!!.getBoolean("sound", soundState)
        return soundState
    }

    fun loadVib(): Boolean {
        val pref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        vibrationState = pref!!.getBoolean("vibration", vibrationState)
        return vibrationState
    }

    fun loadMusic(): Boolean {
        val pref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        musicState = pref!!.getBoolean("music", musicState)
        return musicState
    }

    fun loadColor(): Boolean {
        val pref = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var darkMode = pref!!.getBoolean("DarkMode", true)
        return darkMode
    }

    lateinit var currentLevel: List<String>
    lateinit var currentPossibleAnswers: List<String>
    var currentAnswer: Int = 0
    var currentHint: Int = 0
    var currentQuestion: Int = 0
    var currentXP: Int = 0
    var topicID: Int = 1
    var levelProgress: Int = 0
    var topicAndLevel: IntArray = intArrayOf(1, 1)

    fun loadLevelInfo(context: Context): IntArray {
        val pref = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
//        var tempLevel = pref!!.getInt(
//            "tempLevel",
//            0
//        ) //If Temp Level is active (doesnt overwrite last saved level)

        topicID = pref.getInt("selectedTopic", 1) // Which topic was last used

        var savedlvlProg = pref.getInt("topic" + topicID, 500)

        levelProgress = pref.getInt("selectedLevel", 1)
        topicAndLevel[0] = topicID
        topicAndLevel[1] = levelProgress
        println("step 3:  topic = " + topicAndLevel[0] + " & level =  " + topicAndLevel[1])
        return topicAndLevel
    }

    fun updateLevel(context: Context) {
        val pref = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
        val editor = pref?.edit()
        var currentLevel = pref.getInt("selectedLevel", 0) //current level
        editor?.putInt("selectedLevel", currentLevel + 1) //updated level
        editor?.apply()

        //        var topicIDString = "topic" + topicAndLevel[0].toString()
//        var savedLevelProgress = pref?.getInt(topicIDString, levelProgress)
//        println("t: savedlvlprog = " + savedLevelProgress)
//        println("t: levelprog = " + levelProgress)
//
//        if (levelProgress >= savedLevelProgress!!) {
//            editor?.putInt("tempLevel", 0)
//            editor?.putInt(topicIDString, levelProgress)
//            editor?.apply()
//        } else {
//            editor?.putInt("tempLevel", levelProgress)
//        }
//        println("t: max level is = " + topicIDString + "   currentLevelProg = " + levelProgress + "  lastSavedLevel = " + savedLevelProgress)
    }


    fun updateTopic(context: Context) {
        val pref = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
        val editor = pref?.edit()
        var currentTopic = pref.getInt("selectedTopic", 0) //current level
        editor?.putInt("selectedTopic", currentTopic + 1) //updated level
        editor?.apply()
    }

    fun csv(topicID: Int, level: Int, context: Context): List<String> {
        val file = context.assets.open("questions_data_test.csv")
        val isr = InputStreamReader(file)
        val reader = BufferedReader(isr)
        var line = reader.readLine().toString()
        lateinit var myArray: List<String>

        if (topicID == 1 && level == 1) {
            myArray = line.split(',')
        } else {
            while (line != null) {
                myArray = line.split(',')
                println("stepp: line1 is = " + myArray)
                if (myArray[0] == topicID.toString() && myArray[1] == level.toString()) {
                    println("worked because " + myArray[0].toString() + " " + myArray[1] + " " + topicID.toString() + " " + level.toString())
                    break
                } else {
                    println("didnt work because " + myArray[0] + " " + myArray[1] + " " + topicID.toString() + " " + level.toString())
                }

                line = reader.readLine()
            }
        }
        println("step 4: data obtained from csv: " + myArray)
        return myArray
    }

    fun maxProgressXP(topicID: Int, level: Int, context: Context): List<Int>{
        val file = context.assets.open("questions_data_test.csv")
        val isr = InputStreamReader(file)
        val reader = BufferedReader(isr)

        //read lines, sum xp completion for topicID
        //return max xp obtainable for that topic as Int/list of Ints

    }

    fun nextLevel(context: Context) {
        var array = loadLevelInfo(context)
        currentLevel = csv(array[0], array[1], context)
        println("step 5: final data used to display q is = " + currentLevel)
        currentQuestion =
            context.resources.getIdentifier(currentLevel[2], "drawable", context.packageName)
        currentHint =
            context.resources.getIdentifier(currentLevel[3], "drawable", context.packageName)
        currentAnswer =
            context.resources.getIdentifier(currentLevel[4], "drawable", context.packageName)
        currentXP = currentLevel[5].toInt()
        currentPossibleAnswers = currentLevel.slice(IntRange(5, currentLevel.lastIndex))
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ze.app.xyzmath", "notification", importance).apply {
                description = "Go on, challenge yourself"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.statusBarColor = Color.WHITE

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)


        createNotificationChannel()
        val intent = Intent(this, LauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var builder = NotificationCompat.Builder(this, "ze.app.xyzmath")
            .setContentTitle("XYZ Math")
            .setContentText("Go on, challenge yourself!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent)
            .setAutoCancel(true)
//            .setSmallIcon(R.drawable.ic_launcher)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }

        var gso: GoogleSignInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent: Intent = mGoogleSignInClient.signInIntent

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

//        if (loadColor()) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }


//        loadLevelInfo(this)
//        nextLevel(this)

        loadVib()
        loadSound()
        loadMusic()

        mp = MediaPlayer.create(this, R.raw.app_music_1)
        mp.isLooping = true

        if (musicState) {
            mp.start()
        }

        mpWrong = MediaPlayer.create(this, R.raw.wrong)
        mpCorrect = MediaPlayer.create(this, R.raw.correct)


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    // Set the content to appear under the system bars so that the
//                    // content doesn't resize when the system bars hide and show.
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN)}

//        window.decorView.setOnSystemUiVisibilityChangeListener {
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    // Set the content to appear under the system bars so that the
//                    // content doesn't resize when the system bars hide and show.
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN)}

        var signInOptions: GoogleSignInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        var account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        //Check whether or not acc user signed in
        if (GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            //Already signed in
            //signed in acc stored in above "account" variable
            var signedInAccount: GoogleSignInAccount? = account;
            println("rrresult: already signed in")
        } else {
//            promptSignIn(this)
            var signInClient: GoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)

            signInClient.silentSignIn()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        val signedInAccount = task.result
                        println("rrresult: sign in successful")
                    } else {
                        //Manual sign in
                        var intent: Intent = signInClient.signInIntent
                        startActivityForResult(intent, 100)
                    }
                }
        }

        fun showLeaderboard() {

            Games.getLeaderboardsClient(
                this,
                GoogleSignIn.getLastSignedInAccount(this)!!
            )
                .getLeaderboardIntent(getString(R.string.leaderboard))
                .addOnSuccessListener(OnSuccessListener<Intent> {
                    @Override
                    fun onSuccess(intent: Intent) {
                        startActivityForResult(intent, 100)
                        println("rrresult is: leaderboard listened")
                    }
                })
        }


//        showLeaderboard()

    }

    fun showLeaderboard() {
        println("rrresult: function called")
        Games.getLeaderboardsClient(
            this,
            GoogleSignIn.getLastSignedInAccount(this)!!
        )
            .getLeaderboardIntent(getString(R.string.leaderboard))
            .addOnSuccessListener(OnSuccessListener<Intent>() {
                startActivityForResult(it, 100)
            }
            );
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            var result: GoogleSignInResult? = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                var signedInAccount: GoogleSignInAccount = result.signInAccount!!
                println("rrresult is: true")
            } else {
                println("rrresult is: false")
                println("rrresult is: " + result.status.statusCode.toString())
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    // Set the content to appear under the system bars so that the
//                    // content doesn't resize when the system bars hide and show.
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        }
    }

    override fun onStart() {
        super.onStart()
        loadVib()
        loadSound()
        loadMusic()
    }

    override fun onRestart() {
        super.onRestart()
        loadVib()
        loadSound()
        loadMusic()
    }

    override fun onResume() {
        super.onResume()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    // Set the content to appear under the system bars so that the
//                    // content doesn't resize when the system bars hide and show.
//                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    // Hide the nav bar and status bar
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
//        }

        loadVib()
        loadSound()
        loadMusic()
        if (musicState) {
            mp.start()
        }
    }


    override fun onPause() {
        super.onPause()
        if (mp.isPlaying) {
            musicState = true
            mp.pause()
        }
        saveData()
    }


    override fun onDestroy() {
        super.onDestroy()
        saveData()
        mp.stop()
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }
}



