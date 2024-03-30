package com.example.entofu.quiz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.MainActivity
import com.example.entofu.R
import com.example.entofu.adapter.Quiz1Adapter
import com.example.entofu.apiInterface.CreateQuizService
import com.example.entofu.dataItem.Quiz1Model
import com.example.entofu.databinding.FragmentQuiz1Binding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.concurrent.thread

class Quiz1Fragment : Fragment() {

    private lateinit var binding : FragmentQuiz1Binding
    private lateinit var quiz1RecyclerView : RecyclerView
    private lateinit var quiz1ListAdapter : Quiz1Adapter
    private lateinit var quiz1List : ArrayList<Quiz1Model>
    private lateinit var mainAct : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentQuiz1Binding.inflate(inflater)
        firstInit()

        quiz1List.add(Quiz1Model("enWord","mean1","mean2","mean3","mean4"))
//        for(i in (1..7)) getVocabQuizKoRequest()
        setAdapter()

        return binding.root
    }
    private fun firstInit() {
        quiz1RecyclerView = binding.quiz1Recyclerview
        quiz1List = ArrayList()
        mainAct = activity as MainActivity
    }
    private fun getVocabQuizKoRequest(){
        thread{
            val response = CreateQuizService().quizService.getVocabQuizKoRequest(memberIdx= mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {

                    val enWord = response.body()!!.english
                    val mean1 = (if(response.body()!!.korean[0]==" ") "비어있는" else response.body()!!.korean[0])
                    val mean2 = (if(response.body()!!.korean[1]==" ") "비어있는" else response.body()!!.korean[1])
                    val mean3 = (if(response.body()!!.korean[2]==" ") "비어있는" else response.body()!!.korean[2])
                    val mean4 = (if(response.body()!!.korean[3]==" ") "비어있는" else response.body()!!.korean[3])

                    quiz1List.add(Quiz1Model(enWord,mean1,mean2,mean3,mean4))

                    Log.e("getVocabQuizKoRequest", "success !")
                } else {
                    Log.e("getVocabQuizKoRequest", "response-fail!")
                    Log.e("getVocabQuizKoRequest", "error code : " + response.code())
                    Log.e("getVocabQuizKoRequest", "error message : " + response.message())

                }
            }
        }
    }
    private fun setAdapter(){

        quiz1ListAdapter = Quiz1Adapter(quiz1List)

        quiz1RecyclerView.adapter = quiz1ListAdapter
        quiz1RecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        quiz1ListAdapter.setItemClickListener(object : Quiz1Adapter.OnItemClickListener {
            override fun onClick(v: View, position: Int): Boolean {
                when (v.id){
                    R.id.mean1 -> checkKoMean(quiz1List[position].enWord, quiz1List[position].mean1)
                    R.id.mean2 -> checkKoMean(quiz1List[position].enWord, quiz1List[position].mean2)
                    R.id.mean3 -> checkKoMean(quiz1List[position].enWord, quiz1List[position].mean3)
                    R.id.mean4 -> checkKoMean(quiz1List[position].enWord, quiz1List[position].mean4)
                }
                return true
            }
        })

    }
    private fun checkKoMean(enWord:String,answer : String){
        Log.e("checkKoMean", "mean :$answer")
        val json = JSONObject("""{"question":"$enWord", "answer":"$answer"}""")
        thread{
            val response = CreateQuizService().quizService.getKoResponse(memberIdx = mainAct.user.getIdx()!!,body =  JsonParser.parseString(json.toString()) as JsonObject).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),response.body()!!.string(), Snackbar.LENGTH_LONG).show()
//                    nextQuiz()
                    Log.e("checkKoMean", "success ! :"+ response.body()!!.string())

                } else {
                    Log.e("checkKoMean", "response-fail!")
                    Log.e("checkKoMean", "error code : " + response.code())
                    Log.e("checkKoMean", "error message : " + response.message())
                }
            }
        }
    }

}