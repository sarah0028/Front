package com.example.entofu.papago

class PapagoEntity {
    var message: ResultMessage? = null

    inner class ResultMessage {
        var result: Result? = null

        inner class Result {
            var translatedText: String? = null
        }
    }
}