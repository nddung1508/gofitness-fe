package com.example.data

import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class FirebaseQueryLiveData(private val query: Query) : LiveData<DataSnapshot>() {
    private val listener = MyValueEventListener()

    override fun onActive() {
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        query.removeEventListener(listener)
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = dataSnapshot
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }
}