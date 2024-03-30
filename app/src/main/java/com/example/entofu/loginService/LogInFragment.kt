package com.example.entofu.loginService

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.entofu.HomeFragment
import com.example.entofu.MainActivity
import com.example.entofu.apiInterface.CreateLoginService
import com.example.entofu.apiInterface.LoginService
import com.example.entofu.databinding.FragmentLogInBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.concurrent.thread

class LogInFragment : Fragment() {
    private lateinit var binding : FragmentLogInBinding
    private lateinit var mainAct : MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding= FragmentLogInBinding.inflate(inflater)
        mainAct = activity as MainActivity

        val loginService = CreateLoginService().loginService

        binding.loginBtn.setOnClickListener {

            val json = JSONObject("""{"id":"${binding.idEditText.text}", "pw":"${binding.pwdEditText.text}"}""")
            thread {
                val response = loginService.postMemberReq(body = JsonParser.parseString(json.toString()) as JsonObject)
                        .execute()
                CoroutineScope(Dispatchers.Main).launch {
                    if (response.isSuccessful) {
                        mainAct.user.setIdx(response.body()!!.idx)
                        mainAct.user.setJwt(response.body()!!.jwt)
                        Log.e("loginMemberCheck", "success idx :" + mainAct.user.getIdx() + " jwt :" + mainAct.user.getJwt())
                        get(loginService)

                        mainAct.supportFragmentManager.beginTransaction().detach(HomeFragment()).attach(HomeFragment()).commit()
                        mainAct.loadFragment(HomeFragment())
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content)," Please try again", Snackbar.LENGTH_LONG).show()
                        Log.e("loginMemberCheck", "response-fail!")
                        Log.e("loginMemberCheck", "error code : " + response.code())
                        Log.e("loginMemberCheck", "error message : " + response.message())
                    }

                }
            }
        }

        binding.singUp.setOnClickListener{
            mainAct.loadFragment(SignUpFragment())
        }

        return binding.root
    }
    fun get(loginService: LoginService?){
        thread{
            Log.e("loginMemberCheck", "function called idx :"+mainAct.user.getIdx())
            val responseName = loginService!!.getMember(idx=mainAct.user.getIdx()!!).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (responseName.isSuccessful) {
                    mainAct.user.setUserName(responseName.body()!!.data!!.name)
                    mainAct.user.setUserId(responseName.body()!!.data!!.id)
                    mainAct.user.setUserPwd("${binding.pwdEditText.text}")
                    Log.e("loginMemberCheck", "login after name :" + mainAct.user.getUserName() + " jwt :")
//                    Log.e("loginMemberCheck",responseName.body()!!.string())

                } else {
                    if(responseName.code()==500){
                        Snackbar.make(binding.root, "아이디 또는 비밀번호가 다릅니다.", Snackbar.LENGTH_LONG).setAction("OK") {
                        }.show()
                    }
                    Log.e("loginMemberCheck", "response-fail!")
                    Log.e("loginMemberCheck", "error code : " + responseName.code())
                    Log.e("loginMemberCheck", "error message : " + responseName.message())

                }
            }
        }
    }
}