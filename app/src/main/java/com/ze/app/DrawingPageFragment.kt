package com.ze.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_drawing_page.*

/**
 * A simple [Fragment] subclass.
 */
class DrawingPageFragment : Fragment() {
    var textMode = false
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_drawing_page, null, false)
//        var drawPage: DrawingPage = DrawingPage(context!!, null, 0)

        var drawView = view.findViewById<DrawingPage>(R.id.drawView)
        var drawButton = view.findViewById<Button>(R.id.drawButton)

        view.findViewById<Button>(R.id.homeButton)
            .setOnClickListener { fragmentManager!!.popBackStack() }

        var textButton = view.findViewById<Button>(R.id.textButton)
//        var editTextView = view.findViewById<EditText>(R.id.editText)
        var textModeText = view.findViewById<TextView>(R.id.textMode)

        textButton.setOnClickListener {
            textMode = !(textMode)
            if (textMode) {
                textModeText.text = "Text Mode: On"
//                editTextView.requestFocus()
            } else {
                textModeText.text = "Text Mode: Off"
            }

            drawView.textMode(textMode)
        }

        return view
    }

    fun showKeyboard() {
        val inputMethodManager =
            activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
        println("called focus:")
    }

}
