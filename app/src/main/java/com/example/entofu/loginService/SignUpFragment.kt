package com.example.entofu.loginService

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.entofu.MainActivity
import com.example.entofu.R
import com.example.entofu.apiInterface.CreateSignUpService
import com.example.entofu.apiInterface.LoginRes
import com.example.entofu.apiInterface.SignUpReq
import com.example.entofu.databinding.FragmentLogInBinding
import com.example.entofu.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpFragment : Fragment() {

    private lateinit var binding : FragmentSignUpBinding
    private lateinit var mainAct : MainActivity
    private val shakeAnimator: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentSignUpBinding.inflate(inflater)
        mainAct = activity as MainActivity

        val inputId =binding.inputId
        val inputPwd =binding.inputPwd
        val inputName =binding.inputName

        binding.signUpBtn.setOnClickListener {
            if(inputName.text.toString().length < 2) {
                inputName.startAnimation(shakeAnimator)
                Snackbar.make(binding.root, "이름이 너무 짧습니다.", Snackbar.LENGTH_INDEFINITE).show()
            }
            else if(inputId.text==null) {
                inputId.startAnimation(shakeAnimator)
                Snackbar.make(binding.root, "아이디를 입력해 주세요.", Snackbar.LENGTH_INDEFINITE).show()
            }
            else if(inputPwd.text.toString().length<8) {
                inputPwd.startAnimation(shakeAnimator)
                Snackbar.make(binding.root, "1개 이상의 알파벳, 숫자, 특수문자를 포함하는 8자리 이상의 문자열을 입력해주세요.", Snackbar.LENGTH_INDEFINITE).show()
            }
            else {
                // 서버로 아이디,비번, 이름 전송
                val signUpReq = SignUpReq(inputId.text.toString(),inputPwd.text.toString(),inputName.text.toString())
                CreateSignUpService().signUpService.postMemberRequest(body = signUpReq.toJson()).enqueue(object :
                    Callback<LoginRes> {
                    override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                        if(response.isSuccessful){
                            Log.e("signUpCheck", "success! response idx : "+ response.body()?.idx!!+ "response jwt :" +response.body()?.jwt)
                            mainAct.loadFragment(LogInFragment())
                        }
                        else {  // 서버 연결은 성공, but 성공하지 못한 경우 => 비밀번호 특수문자 미포함

                            Snackbar.make(binding.root, "1개 이상의 알파벳, 숫자, 특수문자를 포함하는 8자리 이상의 문자열을 입력해주세요.", Snackbar.LENGTH_INDEFINITE).show()

                            Log.e("signUpCheck", "response-fail!")
                            Log.e("signUpCheck", "error code : " + response.code())
                            Log.e("signUpCheck", "error message : " + response.errorBody()?.string()!!)
                            Log.e("signUpCheck", "parameter : " + signUpReq.toJson().toString())

                        }
                    }
                    override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                        Log.e("signUpCheck", "fail! : ${t.message}")
                    }
                })
                mainAct.loadFragment(LogInFragment())
            }
        }


        return binding.root
    }

}