package com.example.entofu.dataItem

import android.graphics.drawable.Drawable

class MyPageListItem {
    private var mImg: Drawable? = null
    private var mText: String? = null
    private var mBtn : String? = null

    fun getImg(): Drawable? {
        return mImg
    }

    fun setImg(imgName: Drawable?) {
        mImg = imgName
    }

    fun getText(): String? {
        return mText
    }

    fun setText(Text: String?) {
        mText = Text
    }
    fun getBtn(): String? {
        return mBtn
    }

    fun setBtn(text: String?) {
        mBtn = text
    }


}