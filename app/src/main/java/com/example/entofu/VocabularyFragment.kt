package com.example.entofu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.adapter.VocabularyAdapter
import com.example.entofu.apiInterface.CreateVocabularyService
import com.example.entofu.dataItem.Word
import com.example.entofu.databinding.FragmentVocabularyBinding
import com.example.entofu.loginService.MyPageFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import kotlin.concurrent.thread

class VocabularyFragment : Fragment() {

    private lateinit var binding : FragmentVocabularyBinding
    private lateinit var vocabularyRecyclerView : RecyclerView
    private lateinit var vocabularyListAdapter : VocabularyAdapter
    private lateinit var vocabularyList : ArrayList<Word>
    private lateinit var mainAct : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentVocabularyBinding.inflate(inflater)

        firstInit()
        getMemberVocabulary()
        setAdapter()

        val toolbar = mainAct.activityMainBinding!!.toolBar
        mainAct.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        mainAct.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        return binding.root
    }

    private fun firstInit() {
        vocabularyRecyclerView = binding.vocabularyRecyclerView
        vocabularyList = ArrayList()
        mainAct = activity as MainActivity
    }
    private fun addWord(word : String?, meaning: String?,idx: Long?) {
        val item = Word()
        item.setWord(word)
        item.setMeaning(meaning)
        item.setWordIdx(idx)
        vocabularyList.add(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.my_info ->{
                if(mainAct.user.isLogin()) mainAct.loadFragment(MyPageFragment())
                else mainAct.makeDialogNonMember(requireContext())            }
        }
        return super.onOptionsItemSelected(item)
    }



//    override fun onResume() {
//        super.onResume()
//        if(!user.isLogin()){
//            recreate()
//        }
//    }

    private fun getMemberVocabulary(){
        thread {
            val response = CreateVocabularyService().vocabularyService.getMemberVocabulary(memberIdx = mainAct.user.getIdx()!!).execute()

            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for(word in response.body()?.data!!){
                        addWord(word.english,word.korean,word.vocabularyIdx)
                    }
                    setAdapter()

                    Log.e("getMemberVocabulary", "success"+response.body().toString())

                } else {
                    Log.e("getMemberVocabulary", "response-fail!")
                    Log.e("getMemberVocabulary", "error code : " + response.code())
                    Log.e("getMemberVocabulary", "error message : " + response.message())
                }
            }
        }
    }
    private fun setAdapter(){

        vocabularyListAdapter = VocabularyAdapter(vocabularyList)

        vocabularyRecyclerView.adapter = vocabularyListAdapter
        vocabularyRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        vocabularyListAdapter.setItemLongClickListener(object : VocabularyAdapter.OnItemLongClickListener {
            override fun onLongClick(v: View, position: Int): Boolean {

                Snackbar.make(v,"Are you sure you want to delete that word?", Snackbar.LENGTH_LONG).setAction("OK") {

                    CreateVocabularyService().vocabularyService.deleteVocabularyOne(idx = vocabularyList[position].getWordIdx()!!).enqueue(object : retrofit2.Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if(response.isSuccessful){

                                vocabularyListAdapter.deleteOne(position)
                                Log.e("deleteVocabularyOne", "success" + response.body()?.string())
                            }
                            else{
                                Log.e("deleteVocabularyOne", "response-fail! :"+response.errorBody()?.string())
                                Log.e("deleteVocabularyOne", "error code : " + response.code())
                                Log.e("deleteVocabularyOne", "error message : " + response.message())
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.printStackTrace()
                            Log.e("deleteVocabularyOne", " fail "+ t.message)
                        }
                    })
                }.show()
                return true
            }
        })

    }

}