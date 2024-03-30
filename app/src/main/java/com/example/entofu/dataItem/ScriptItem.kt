package com.example.entofu.dataItem

class ScriptItem {
    private var mImgUrl: String? = null
    private var title: String? = null
    private var status : String? = null
    private var isCommon: Boolean? = null
    private var content:String? = null
    private var scriptIdx : Long? = null

    fun getImgUrl(): String? = mImgUrl
    fun getTitle(): String? =title
    fun getStatus(): String?=status
    fun getContent():String? = content
    fun getScriptIdx(): Long? = scriptIdx
    fun getIsCommon(): Boolean? = isCommon

    fun setIsCommon(isCommon:Boolean?){
        this.isCommon = isCommon
    }

    fun setImgUrl(imgName: String?) {
        mImgUrl = imgName
    }
    fun setContent(content:String?){
        this.content = content
    }
    fun setScriptIdx(scriptIdx: Long?){
        this.scriptIdx = scriptIdx
    }
    fun setTitle(mainText: String?) {
        title = mainText
    }
    fun setStatus(status: String?) {
        this.status = status
    }
}