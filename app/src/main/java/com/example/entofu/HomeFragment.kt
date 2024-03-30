package com.example.entofu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.adapter.RecyclerViewAdapter
import com.example.entofu.apiInterface.CreateScriptService
import com.example.entofu.dataItem.ScriptItem
import com.example.entofu.databinding.FragmentHomeBinding
import com.example.entofu.learn.LearnFragment
import com.example.entofu.loginService.MyPageFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import kotlin.concurrent.thread


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var scriptRecyclerView : RecyclerView
    private lateinit var scriptList : ArrayList<ScriptItem>
    private lateinit var mainAct : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentHomeBinding.inflate(inflater)

        firstInit()
        if(mainAct.user.isLogin()) loginScriptAdd()
        else commonScriptAdd()

        val toolbar = mainAct.activityMainBinding!!.toolBar
        mainAct.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        mainAct.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        return binding.root
    }

    private fun firstInit() {
        scriptRecyclerView=binding.ScriptRecyclerView
        scriptList = ArrayList()
        mainAct = activity as MainActivity
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.my_info ->{
                if(mainAct.user.isLogin()) mainAct.loadFragment(MyPageFragment())
                else mainAct.makeDialogNonMember(requireContext())
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun scriptOne(status : String?, imgUrl: String?, mainText: String?, content:String?,scriptIdx:Long?) {
        val item = ScriptItem()
        item.setStatus(status)
        if (imgUrl==null) item.setImgUrl(resources.getString(R.string.app_image_url)) else  item.setImgUrl(imgUrl)
        item.setTitle(mainText)
        item.setContent(content)
        item.setScriptIdx(scriptIdx)
        scriptList.add(item)
    }


    private fun commonScriptAdd(){
        scriptOne("COMMON",null,"title","No-one knew ",2323)
        setAdapter()

//        thread {
//            val response = CreateScriptService().scriptService.getCommonScript().execute()
//            CoroutineScope(Dispatchers.Main).launch {
//                if (response.isSuccessful) {
//                    for (scriptOne in response.body()!!.data!!){
//                        scriptOne("Common",scriptOne.image,
//                            scriptOne.title,scriptOne.content,scriptOne.idx)
//                    }
//                    setAdapter()
//                } else {
//                    Log.e("commonScriptCheck", "response-fail!")
//                    Log.e("commonScriptCheck", "error code : " + response.code())
//                    Log.e("commonScriptCheck", "error message : " + response.message())
//                }
//            }
//        }
    }
    private fun memberCommonScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getMemberCommonScript(memberIdx = mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!.data!!){
                        scriptOne("COMMON",scriptOne.image,
                            scriptOne.title,scriptOne.content,scriptOne.idx)
                    }
                    setAdapter()
                } else {
                    Log.e("MemberCommonScriptCheck", "response-fail!")
                    Log.e("MemberCommonScriptCheck", "error code : " + response.code())
                    Log.e("MemberCommonScriptCheck", "error message : " + response.message())
                }
            }
        }
    }
    private fun loginScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getScrapScript(memberIdx = mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!){
                        scriptOne("SCRAP", scriptOne.img, scriptOne.title,scriptOne.content,scriptOne.scriptIdx)
                    }
                    memberScriptAdd()
                } else {
                    Log.e("ScrapScriptCheck", "response-fail!")
                    Log.e("ScrapScriptCheck", "error code : " + response.code())
                    Log.e("ScrapScriptCheck", "error message : " + response.message())
                }
            }
        }

    }
    private fun memberScriptAdd(){
        thread {
            val response = CreateScriptService().scriptService.getMemberScript(memberIdx = mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    for (scriptOne in response.body()!!.data!!){
                        scriptOne("MEMBER", resources.getString(R.string.app_image_url),
                            scriptOne.title,scriptOne.content,scriptOne.idx)
                    }
                    memberCommonScriptAdd()
                } else {
                    Log.e("MemberScriptCheck", "response-fail!")
                    Log.e("MemberScriptCheck", "error code : " + response.code())
                    Log.e("MemberScriptCheck", "error message : " + response.message())
                }
            }
        }
    }
    private fun setAdapter(){

        mainAct.scriptListAdapter = RecyclerViewAdapter(scriptList)

        scriptRecyclerView.adapter = mainAct.scriptListAdapter
        scriptRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mainAct.scriptListAdapter.setItemClickListener(object: RecyclerViewAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {

                mainAct.script.setScriptIdx(scriptList[position].getScriptIdx())
                mainAct.script.setStatus(scriptList[position].getStatus())
                mainAct.script.setTitle("${scriptList[position].getTitle()}")
                mainAct.script.setContent("${scriptList[position].getContent()}")
                mainAct.script.setImgUrl(scriptList[position].getImgUrl()!!)
                mainAct.loadFragment(LearnFragment())
            }
        })
        mainAct.scriptListAdapter.setItemLongClickListener(object : RecyclerViewAdapter.OnItemLongClickListener{
            override fun onLongClick(v: View, position: Int) : Boolean {
                if (!mainAct.user.isLogin()) return false
                val deleteService = if (mainAct.user.getUserId()!! == "${R.string.admin_name}") CreateScriptService().scriptService.adminDeleteCommonScript(idx =  scriptList[position].getScriptIdx()!!)
                else CreateScriptService().scriptService.deleteMemberScript(memberIdx = mainAct.user.getIdx()!!,scriptIdx =  scriptList[position].getScriptIdx()!!)


                if (scriptList[position].getStatus() == "MEMBER"||scriptList[position].getStatus() == "SCRAP"){

                    val sb = Snackbar.make(v,"Are you sure you want to delete that script?", Snackbar.LENGTH_LONG).setAction("OK") {

                        Log.e("deleteScriptCheck","memberIdx :  ${mainAct.user.getIdx()} scriptIdx : ${scriptList[position].getScriptIdx()!!}")
                        deleteService.enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if(response.isSuccessful){
                                    if(scriptList[position].getStatus() == "MEMBER") mainAct.scriptListAdapter.deleteOne(position)
                                    else (context as FragmentActivity).supportFragmentManager.beginTransaction().attach(HomeFragment()).commit()
                                    Log.e("deleteScriptCheck", "success" + response.body()?.string())
                                }
                                else{
                                    Log.e("deleteScriptCheck", "response-fail! :"+response.errorBody()?.string())
                                    Log.e("deleteScriptCheck", "error code : " + response.code())
                                    Log.e("deleteScriptCheck", "error message : " + response.message())
                                }
                            }
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                t.printStackTrace()
                                Log.e("deleteScriptCheck", " fail "+ t.message)
                            }
                        })
                    }
                    sb.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                    sb.show()
                }else return false
                return true
            }
        })

    }

}