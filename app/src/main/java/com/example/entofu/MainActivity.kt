package com.example.entofu

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.entofu.adapter.RecyclerViewAdapter
import com.example.entofu.dataItem.LearnScriptItem
import com.example.entofu.dataItem.ScriptItem
import com.example.entofu.dataItem.User
import com.example.entofu.databinding.ActivityMainBinding
import com.example.entofu.loginService.LogInFragment
import com.example.entofu.newScript.BottomSheetScriptAdd
import com.example.entofu.quiz.Quiz1Fragment
import com.example.entofu.quiz.Quiz2Fragment
import com.example.entofu.quiz.Quiz3Fragment

var updateHome = false
class MainActivity : AppCompatActivity() {

    val user = User()
    val script = LearnScriptItem()

    var activityMainBinding : ActivityMainBinding? = null
    lateinit var scriptListAdapter :RecyclerViewAdapter
    private var quizCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding!!.root)

        loadFragment(HomeFragment())

        activityMainBinding!!.bottomNavigation.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.menu_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.menu_voca -> {
//                    loadFragment(VocabularyFragment())
                    if (user.isLogin()) loadFragment(VocabularyFragment())
                    else makeDialogNonMember(this)
                    true

                }
                R.id.menu_quiz -> {
                    when(quizCount++ % 3){
                        0->loadFragment(Quiz1Fragment())
                        1->loadFragment(Quiz2Fragment())
                        2->loadFragment(Quiz3Fragment())
                    }
                    true
                }
                R.id.menu_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.menu_add -> {
                    if (user.isLogin()) scriptAdd()
                    else makeDialogNonMember(this)
                    true
                }
                else -> false
            }
        }

    }

    fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frame_layout,fragment)
        transaction.commit()
    }

    private fun scriptAdd() {
        val sheet = BottomSheetScriptAdd()
        sheet.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
        sheet.show(supportFragmentManager, BottomSheetScriptAdd.TAG)
    }
    fun makeDialogNonMember(context : Context){
        AlertDialog.Builder(context)
            .setTitle("NOTION")
            .setMessage("Non-members can not use this screen.\n Please login in")
            .setPositiveButton("Ok",null)
            .setNegativeButton("Log In") { _, _ ->
                loadFragment(LogInFragment()) }
            .show()


    }
}