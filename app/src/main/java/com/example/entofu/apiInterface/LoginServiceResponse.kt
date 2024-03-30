package com.example.entofu.apiInterface

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject

class LoginRes(
    var idx: Long,
    var jwt: String   // token
)
data class UpdateMemberRes(
    var idSuccess : Boolean,
    var code : Int,
    var message:String?,
    var result: ResultUpdate) {
    inner class ResultUpdate(
        var idx: Long,
        var id: String,   // token
        var pw: String,
        var name: String
    )
}

class Member{
    var data: ResultData? = null
    inner class ResultData(
        var id: String,
        var name: String,   // token
        var status:String
    )
}
class SignUpReq(private var id : String, private var pw :String, private var name : String
){
    fun toJson(): JsonObject {
        val json = JSONObject("""{"id":"$id", "pw":"$pw" ,"name":"$name"}""")
        return JsonParser.parseString(json.toString()) as JsonObject
    }
}

data class MyPageDTO(
    var scrapNum : Int,
    var scriptNum : Int,
    var vocabulary : Int
)
