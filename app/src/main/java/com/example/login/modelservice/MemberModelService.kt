package com.example.login.modelservice

import com.example.login.firebase.cloudfirestore.CloudFileStoreWrapper
import com.example.login.model.MemberModel
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MemberModelService {
    suspend fun register(memberModel: MemberModel):Void = withContext(Dispatchers.IO){
        return@withContext CloudFileStoreWrapper.replace(
            MemberModel.CLOUD_FIRE_STORE_PATH,
            memberModel.uuId,
            memberModel.toDictionary()
        )
    }
    suspend fun signIn(id:String, password:String): QuerySnapshot = withContext(Dispatchers.IO){
        val map = mutableMapOf<String, Any>()
        map[MemberModel.ID_KEY] = id
        map[MemberModel.PASSWORD_KEY] = password

        return@withContext CloudFileStoreWrapper.select(
            MemberModel.CLOUD_FIRE_STORE_PATH,
            map
        )
    }
}