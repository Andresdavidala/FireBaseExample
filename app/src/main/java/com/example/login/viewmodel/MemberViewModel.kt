package com.example.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.model.MemberModel
import com.example.login.modelservice.MemberModelService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MemberViewModelSate{
    data class  RegisterSuccessfully(val memberModel: MemberModel):MemberViewModelSate()
    data class  SignInSuccessfully(val memberModel: MemberModel):MemberViewModelSate()

    data class Error(val message: String?):MemberViewModelSate()

    object  Empty : MemberViewModelSate()
    object  Loading : MemberViewModelSate()
    object  None: MemberViewModelSate()

}

class MemberViewModel : ViewModel() {

    private val _memberViewModelSate = MutableStateFlow<MemberViewModelSate>(MemberViewModelSate.None)
    val memberViewModelSate: StateFlow<MemberViewModelSate> = _memberViewModelSate

    fun register(memberModel: MemberModel) = viewModelScope.launch {
        _memberViewModelSate.value = MemberViewModelSate.Loading

        try {

            coroutineScope {
                val register = async {
                    MemberModelService.register(memberModel)
                }
                register.await()
                _memberViewModelSate.value = MemberViewModelSate.RegisterSuccessfully(memberModel)
            }

        }catch (e: Exception)
        {
            _memberViewModelSate.value = MemberViewModelSate.Error(e.message)
        }
    }
    fun signIn(id:String, password: String) = viewModelScope.launch {
        _memberViewModelSate.value = MemberViewModelSate.Loading

        try{
            coroutineScope {
                val signIn = async {
                    MemberModelService.signIn(id, password)
                }
                val querySnapshot = signIn.await()
                val list = mutableListOf<MutableMap<String, Any>>()
                for(document in querySnapshot.documents){
                    list.add(document.data as MutableMap<String, Any>)
                }
                if(list.isEmpty()){
                    _memberViewModelSate.value = MemberViewModelSate.Empty
                    return@coroutineScope
                }
                val memberModel = MemberModel()

                memberModel.parsing(list[0])
                _memberViewModelSate.value = MemberViewModelSate.SignInSuccessfully(memberModel)
            }
        }catch (e: Exception)
        {
            _memberViewModelSate.value = MemberViewModelSate.Error(e.message)
        }
    }
}