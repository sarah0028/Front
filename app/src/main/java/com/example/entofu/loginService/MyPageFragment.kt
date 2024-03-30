package com.example.entofu.loginService

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.entofu.MainActivity
import com.example.entofu.R
import com.example.entofu.adapter.MyPageListAdapter
import com.example.entofu.apiInterface.CreateMyPageService
import com.example.entofu.databinding.FragmentMyPageBinding
import com.example.entofu.databinding.UpdatepwBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    private lateinit var mainAct : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentMyPageBinding.inflate(inflater)
        mainAct = activity as MainActivity


        setMyPageInfo()
        val adapter = MyPageListAdapter()

        val listview = binding.myPageList
        listview.adapter = adapter

        adapter.addItem(ContextCompat.getDrawable(requireContext(), R.drawable.user_image_my_list)!!, mainAct.user.getUserName()!!)
        adapter.addItem(ContextCompat.getDrawable(requireContext(), R.drawable.id_image)!!, mainAct.user.getUserId()!!)
        adapter.addItem(ContextCompat.getDrawable(requireContext(), R.drawable.pwd_image)!!, mainAct.user.getUserPwd()!!)

        listview.setOnItemClickListener { _, _, position, _ ->
            if (position == 2) {
                val builder = AlertDialog.Builder(requireContext())
                val builderItem = UpdatepwBinding.inflate(layoutInflater)
                with(builder){
                    setTitle("Update Password")
                    setView(builderItem.root)
                    setPositiveButton("OK") { _, _ ->
                        updatePw(builderItem.editText.text.toString())
                    }
                    show()
                }
            }
        }



        binding.deleteMember.setOnClickListener{
            deleteMember()
        }


        return binding.root
    }
    @SuppressLint("SetTextI18n")
    private fun setMyPageInfo(){
       thread {

            val response = CreateMyPageService().myPageService.getMyPage(memberIdx= mainAct.user.getIdx()!!).execute()

            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    binding.scrapNum.text = "${response.body()!!.scrapNum}개"
                    mainAct.user.setScrapNum(response.body()!!.scrapNum)
                    binding.scriptNum.text = "${response.body()!!.scriptNum}개"
                    mainAct.user.setScriptNum(response.body()!!.scriptNum)
                    binding.wordNum.text = "${response.body()!!.vocabulary}개"

                    Log.e("setMyPageInfo", "success !")

                } else {
                    Log.e("setMyPageInfo", "response-fail!")
                    Log.e("setMyPageInfo", "error code : " + response.code())
                    Log.e("setMyPageInfo", "error message : " + response.message())
                }
            }
        }
    }
    private fun updatePw(updatePw:String){
//        thread {
//
//            val json = JSONObject("""{"pw":"$updatePw"}""")
//            val response = CreateLoginService().loginService.updateMemberRequest(idx= user.getIdx()!!,
//                body=(JsonParser.parseString(json.toString()) as JsonObject)).execute()
//
//            CoroutineScope(Dispatchers.Main).launch {
//                if (response.isSuccessful) {
//                    user.setUserName(response.body()!!.result.name)
//                    user.setUserId(response.body()!!.result.id)
//                    user.setUserPwd(response.body()!!.result.pw)
//                    user.setIdx(response.body()!!.result.idx)
//                    Log.e("updatePwCheck","result pw :${response.body()!!.result.pw}")
//                    Log.e("updatePwCheck", "success id"+ user.getUserId()+" pw : "+ user.getUserPwd())
//
//
//                } else {
//                    Log.e("updatePwCheck", "response-fail!")
//                    Log.e("updatePwCheck", "error code : " + response.code())
//                    Log.e("updatePwCheck", "error message : " + response.message())
//                }
//            }
//        }
    }
    private fun deleteMember(){
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("NOTION")
//            .setMessage("Are you sure you want to delete member? \nAfter deletion, it cannot be undone")
//            .setPositiveButton("Ok") { _, _ ->
//                Log.d("Dialog", "ok Btn")
//                user.logout()
//
//                CreateLoginService().loginService.deleteMemberRequest(idx= user.getIdx()!!).enqueue(object : retrofit2.Callback<ResponseBody> {
//                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                        if(response.isSuccessful){
//                            user.logout()
//                            Snackbar.make(activityMyPageBinding!!.root, "${response.body()?.string()}", Snackbar.LENGTH_INDEFINITE).setAction("OK") {
//                                finish()
//                                val intent = Intent(this@MyPageActivity, MainActivity::class.java)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                startActivity(intent)
//                                overridePendingTransition(R.anim.horizon_in, R.anim.horizon_exit)
//                            }.show()
//                        }
//                        else{
//                            Log.e("deleteMemberCheck", "response-fail!")
//                            Log.e("deleteMemberCheck", "error code : " + response.code())
//                         Log.e("deleteMemberCheck", "error message : " + response.message())
//                        }
//                    }
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        t.printStackTrace()
//                        Log.e("deleteMemberCheck", " fail "+ t.message)
//                    }
//                })
//
//            }
//            .setNegativeButton("Undo",null)
//            .show()
    }
}