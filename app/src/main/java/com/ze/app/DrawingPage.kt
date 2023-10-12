package com.ze.app

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


class DrawingPage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var brush: Paint = Paint()
    var path: Path = Path()
    var text: String = " "
    var textPaint: Paint = TextPaint()
    var textMode: Boolean = true
    var textX: Float = 0f
    var textY: Float = 0f

    init {
        var params: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.textSize = (50f)
    }

    public fun inputText(string: String) {
        text = string
        postInvalidate();
    }

    fun update() {
        postInvalidate()
    }

    fun textMode(mode: Boolean) {
        textMode = mode
        postInvalidate();
    }

    val inputMethodManager =
        context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager



    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        println("key is: pressed")
//        val inputMethodManager =
//            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)

        brush.isAntiAlias = true
        brush.color = Color.BLACK
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeWidth = 20f


        var pointX = event!!.x
        var pointY = event.y

        println("touched x: " + pointX)
        println("touched y: " + pointY)


        if (textMode) {
            textX = pointX
            textY = pointY

            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
            var editText:EditText = EditText()
            editText.requestFocus()

        } else {

            if (event.action == ACTION_DOWN) {
                path.moveTo(pointX, pointY)
                invalidate()
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                path.lineTo(pointX, pointY)
                invalidate()
            } else if (event.action == MotionEvent.ACTION_UP) {
                path.lineTo(pointX, pointY)
                invalidate()
            }
//        ->
//            MotionEvent.ACTION_MOVE -> path.lineTo(pointX, pointY)
////            else -> {
//                false
            return true
        }
        return true
    }

    override fun setOnKeyListener(l: OnKeyListener?) {
        super.setOnKeyListener(l)

    }

//    override fun setClickable(clickable: Boolean) {
//        super.setClickable(true)
//    }
//
//    override fun setFocusable(focusable: Boolean) {
//        super.setFocusable(true)
//    }

    override fun onDraw(canvas: Canvas) {
        var textUpdate = text
        println("textUpdate is:" + textUpdate)

        if (textX == 0f || textY == 0f) {
            textX = 500f
            textY = 300f
        }

        canvas.drawText(textUpdate, textX, textY, textPaint)
        canvas.drawPath(path, brush)
        super.onDraw(canvas)
    }
}
