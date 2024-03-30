package com.example.entofu.apiInterface

class Script {
    var data: ArrayList<ScriptOne>? = null
    inner class ScriptOne(
        var idx:Long,
        var title : String,
        var content:String,
        var image : String
        )
}
class ScrapScript(
    var scriptIdx:Long,
    var title : String,
    var content:String,
    var img:String
)

class PostMemberScriptRes{
    var data: PostResult? = null

    inner class PostResult(
        var idx:Long,
        var title:String,
        var status:String
    )
}