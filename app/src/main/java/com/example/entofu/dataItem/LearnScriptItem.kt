package com.example.entofu.dataItem


class LearnScriptItem {
    private var title:String = "The highest rail route in northern Europe"
    private var content:String? = "On a chilly November morning in Oslo, I boarded a train bound for Bergen.\n I have been exploring Norway for more than a decade, returning at least a dozen times since my wide-eyed first trip. I have been to the northernmost point on the Norwegian mainland (Knivskjelodden) and to its southernmost tip (Lindesnes Fyr, where my sunglasses blew clean off my head and out to sea in a gale). I have seen whales and walrus. I have hiked across glaciers in Svalbard and stood beneath the country\\'s only palm tree in Kristiansand. And I have watched the northern lights in winter and partied beneath the midnight sun in summer.\\n I had done my research. I knew, for example, that on a short November day, only one of the five daily departure times, 08:25, would ensure that I made the entire six-and-a-half-hour, 496km journey during daylight hours. I knew enough also to book a window seat on the left side of the train (on the right if travelling from Bergen) to get the best views."
    private var imgUrl : String = "https://ychef.files.bbci.co.uk/1280x720/p0dzqw1z.webp"
    private var status: String?= null
    private var scriptIdx : Long? = null

    fun getTitle() = title
    fun getContent() = content
    fun getStatus()= status
    fun getScriptIdx() = scriptIdx
    fun getImgUrl() = imgUrl

    fun setImgUrl(imgUrl:String) {
        this.imgUrl = imgUrl
    }

    fun setTitle(title:String){
        this.title=title
    }
    fun setContent(content:String){
        this.content=content
    }
    fun setStatus(status:String?){
        this.status = status
    }
    fun setScriptIdx(idx:Long?){
        this.scriptIdx = idx
    }
}