package com.example.entofu.learn

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.widget.TextView

// singleton class
object Highlighter {

    fun highlight(tv: TextView, spannable: SpannableString, offset:Int, offset_up:Int){
        var calOffset = offset
        var calOffsetUp = offset_up
        while (tv.text[calOffset] in 'a'..'z' || tv.text[calOffset] in 'A'..'Z') calOffset -= 1
        while (tv.text[calOffsetUp] in 'a'..'z' || tv.text[calOffsetUp] in 'A'..'Z') calOffsetUp += 1
        spannable.setSpan(BackgroundColorSpan(Color.parseColor("#D2FFF758")), calOffset, calOffsetUp, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = spannable
    }
}