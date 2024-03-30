package com.example.entofu.dataItem

import com.example.entofu.updateHome


class User {
    private var idx : Long? = null
    private var jwt : String? = null
    private var name : String? = null
    private var id : String? = null
    private var pw : String? = null
    private var createAt : String? = null
    private var scrapNum : Int = 0
    private var scriptNum : Int = 0
    private var vocabularyNum:Int = 0

    fun isLogin() : Boolean {
        return idx != null
    }

    fun logout() {
        name = null
        id = null
        pw = null
        updateHome = true
    }
    fun getJwt(): String? = jwt

    fun setJwt(jwt : String){
        this.jwt = jwt
    }
    fun getIdx(): Long? = idx

    fun setIdx(idx : Long){
        this.idx = idx
    }
    fun getUserName(): String? =name

    fun setUserName(name : String){
        this.name = name
    }

    fun getUserId(): String? = id

    fun setUserId(Id : String){
        this.id = Id
    }
    fun getUserPwd(): String? =pw

    fun setUserPwd(pwd : String){
        this.pw = pwd
    }
    fun getCreateAt(): String? =createAt

    fun setCreateAt(createAt : String){
        this.createAt = createAt
    }
    fun getScrapNum() : Int = scrapNum
    fun setScrapNum(num:Int){
        this.scrapNum = num
    }
    fun getScriptNum() : Int = scriptNum
    fun setScriptNum(num:Int){
        this.scriptNum = num
    }
    fun getVocabularyNum() : Int = vocabularyNum
    fun setVocabularyNum(num:Int){
        this.vocabularyNum = num
    }



}