package com.example.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.example.login.model.MemberModel
import com.example.login.viewmodel.MemberViewModel
import com.example.login.viewmodel.MemberViewModelSate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val memberViewModel : MemberViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFlow()
//        registerTest()
        sigInTest()
    }

    private fun sigInTest(){
        memberViewModel.signIn("test","qwer1234")
    }
    private fun registerTest(){
        val memberModel = MemberModel()
        //as user seq
        val uuid = getRandomUUIDString()
        memberModel.uuId = uuid
        memberModel.id = "test"
        memberModel.password  = "qwer1234"
        memberModel.email = "test@test.com"
        memberModel.nickname = "tester"
        memberModel.profileImageUrl = ""
        memberModel.profileImageFileCloudPath = ""
        memberModel.createDate  = getCurrentDateTimeString()
        memberModel.modifyDate = getCurrentDateTimeString()
        memberModel.createBy = uuid
        memberModel.modifyBy = uuid

        memberViewModel.register(memberModel)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDateTimeString() : String{
        val date = Calendar.getInstance().time
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
    }
    private fun getRandomUUIDString():String{
        return UUID.randomUUID().toString().replace("-","")
    }
    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main){

            whenCreated {
                memberViewModel.memberViewModelSate.collect{

//                    data class Error(val message: String?):MemberViewModelSate()
//
//                    object  Empty : MemberViewModelSate()
//                    object  Loading : MemberViewModelSate()
//                    object  None: MemberViewModelSate()
                    when(it){
                        is MemberViewModelSate.RegisterSuccessfully->{
                            //register success
                            Log.d("???", "${it.memberModel}")
                            Toast.makeText(this@MainActivity, "register success", Toast.LENGTH_SHORT).show()
                        }
                        is MemberViewModelSate.SignInSuccessfully ->{
                            Log.d("???", "${it.memberModel.toDictionary()}")
                            Toast.makeText(this@MainActivity, "sign in success", Toast.LENGTH_SHORT).show()


                            //sign in success
                        }
                        is MemberViewModelSate.Loading ->{
                            //show progress here
                        }
                        is MemberViewModelSate.Empty ->{
                            //selected is  empty
                        }
                        is MemberViewModelSate.Error ->{
                            Log.d("???", "${it.message}")

                        }
                        is MemberViewModelSate.None ->Unit
                    }
                }
            }
        }
    }
}