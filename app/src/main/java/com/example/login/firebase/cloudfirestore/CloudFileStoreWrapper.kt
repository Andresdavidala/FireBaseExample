package com.example.login.firebase.cloudfirestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object CloudFileStoreWrapper {

    suspend fun replace(collectionPath: String, documentPath: String, map: MutableMap<String,Any>):Void{
        return suspendCoroutine { continuation->
            Firebase.firestore.collection(collectionPath).document(documentPath).set(map)
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener{
                    continuation.resumeWithException(it)
                }
        }

    }
    suspend fun select(collectionPath: String, conditionMap: MutableMap<String, Any>? = null, limit: Long = 1):QuerySnapshot{
        return suspendCoroutine {continuation->
            val collectionRefrence = Firebase.firestore.collection(collectionPath)

            //select one default
            var query  = collectionRefrence.limit(limit)

            conditionMap?.let{
                it.forEach{map->
                    //condicion map example id = xxxx.passwrod = xxx
                    query = collectionRefrence.whereEqualTo(map.key, map.value)
                }
            }
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }

                .addOnFailureListener{
                    continuation.resumeWithException(it)
                }
        }
    }
}