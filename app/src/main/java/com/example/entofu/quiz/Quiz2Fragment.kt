package com.example.entofu.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.MainActivity
import com.example.entofu.adapter.Quiz2Adapter
import com.example.entofu.dataItem.Quiz2Model
import com.example.entofu.databinding.FragmentQuiz2Binding

class Quiz2Fragment : Fragment() {

    private lateinit var binding : FragmentQuiz2Binding
    private lateinit var quiz2RecyclerView : RecyclerView
    private lateinit var quiz2ListAdapter : Quiz2Adapter
    private lateinit var quiz2List : ArrayList<Quiz2Model>
    private lateinit var mainAct : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentQuiz2Binding.inflate(inflater)
        firstInit()


        quiz2List.add(Quiz2Model("사과"))
        quiz2List.add(Quiz2Model("바나나"))
        quiz2List.add(Quiz2Model("키위"))
        quiz2List.add(Quiz2Model("딸기"))
        setAdapter()

        binding.submitBtn.setOnClickListener {

//            for(i in quiz2List){
//                Toast.makeText(context,"${i.e}")
//            }
//            getVocabQuizEnResponse(binding.essayEditText.text.toString())
        }
        return binding.root
    }
    private fun firstInit() {
        quiz2RecyclerView = binding.quiz2Recyclerview
        quiz2List = ArrayList()
        mainAct = activity as MainActivity
    }
//    private fun getVocabQuizEnResponse(answer: String){
//        Log.e("getVocabQuizEnResponse", "answer :$answer")
//        val json = JSONObject("""{"question":"${binding!!.enWord.text}", "answer":"$answer"}""")
////        thread{
////            val response = CreateQuizService().quizService.getEnResponse(memberIdx = user.getIdx()!!,body =  JsonParser.parseString(json.toString()) as JsonObject).execute()
////            CoroutineScope(Dispatchers.Main).launch {
////                if (response.isSuccessful) {
////                    makeSnackBar(activityQuizActivityBinding!!.root,response.body()!!.string())
////                    nextQuiz()
////                    Log.e("getVocabQuizEnResponse", "success ! :"+ response.body()!!.string())
////
////                } else {
////                    Log.e("getVocabQuizEnResponse", "response-fail!")
////                    Log.e("getVocabQuizEnResponse", "error code : " + response.code())
////                    Log.e("getVocabQuizEnResponse", "error message : " + response.message())
////                }
////            }
////        }
//    }
    private fun setAdapter(){

        quiz2ListAdapter = Quiz2Adapter(quiz2List)

        quiz2RecyclerView.adapter = quiz2ListAdapter
        quiz2RecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }
}