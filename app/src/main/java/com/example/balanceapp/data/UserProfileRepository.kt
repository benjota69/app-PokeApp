package com.example.balanceapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String,
    val email: String?,
    val team: String?
)

class UserProfileRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun usersCollection() = firestore.collection("users")

    // Guarda (o actualiza) el team del usuario actual.
    suspend fun saveTeam(team: String) {
        val user = auth.currentUser ?: return
        val data = mapOf(
            "email" to user.email,
            "team" to team
        )
        usersCollection()
            .document(user.uid)
            .set(data, SetOptions.merge())
            .await()
    }

    // Obtiene el perfil completo del usuario actual desde Firestore.
    suspend fun getProfile(): UserProfile? {
        val user = auth.currentUser ?: return null
        val snapshot = usersCollection().document(user.uid).get().await()
        val team = snapshot.getString("team")
        return UserProfile(
            uid = user.uid,
            email = user.email,
            team = team
        )
    }
}



