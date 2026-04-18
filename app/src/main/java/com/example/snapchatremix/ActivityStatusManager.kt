package com.yourapp.snapchatremix

import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

enum class UserStatus { ACTIVE, IDLE, OFFLINE }

object ActivityStatusManager {

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    fun setActive()  = setStatus(UserStatus.ACTIVE)
    fun setIdle()    = setStatus(UserStatus.IDLE)
    fun setOffline() = setStatus(UserStatus.OFFLINE)

    private fun setStatus(status: UserStatus) {
        val uid = auth.currentUser?.uid ?: return
        val statusMap = mapOf(
            "status"   to status.name,
            "lastSeen" to ServerValue.TIMESTAMP
        )
        db.child("users").child(uid).child("presence")
            .updateChildren(statusMap)
    }

    fun observeUserStatus(
        targetUid: String,
        onChange: (UserStatus) -> Unit
    ): ValueEventListener {
        val ref = db.child("users").child(targetUid).child("presence/status")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val raw = snapshot.getValue(String::class.java) ?: "OFFLINE"
                onChange(runCatching { UserStatus.valueOf(raw) }.getOrDefault(UserStatus.OFFLINE))
            }
            override fun onCancelled(error: DatabaseError) {
                onChange(UserStatus.OFFLINE)
            }
        }
        ref.addValueEventListener(listener)
        return listener
    }

    fun setupOnDisconnect() {
        val uid = auth.currentUser?.uid ?: return
        db.child("users").child(uid).child("presence")
            .onDisconnect()
            .updateChildren(
                mapOf(
                    "status"   to UserStatus.OFFLINE.name,
                    "lastSeen" to ServerValue.TIMESTAMP
                )
            )
    }
}