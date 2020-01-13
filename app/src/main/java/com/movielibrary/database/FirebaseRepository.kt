package com.movielibrary.database

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseRepository {
    private val db = Firebase.firestore
    private final val TAG = "I/FIRESTORE"

    fun insertUser(user: UserEntity) {
        user.id?.let {
            db.collection("users").document(it).set(user)
                .addOnSuccessListener { _ ->
                    Log.d(TAG, "DocumentSnapshot written with ID: $it")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
}