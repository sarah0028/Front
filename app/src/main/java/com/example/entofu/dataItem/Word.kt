package com.example.entofu.dataItem

class Word {
    private var word: String? = null
    private var meaning : String? = null
    private var wordIdx:Long? = null

    fun getWord(): String? = word
    fun getWordIdx(): Long? = wordIdx
    fun getMeaning(): String? =meaning


    fun setWord(word: String?) {
        this.word = word
    }

    fun setMeaning(meaning: String?) {
        this.meaning = meaning
    }
    fun setWordIdx(idx: Long?){
        this.wordIdx = idx
    }
}