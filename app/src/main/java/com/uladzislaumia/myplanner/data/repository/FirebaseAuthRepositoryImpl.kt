package com.uladzislaumia.myplanner.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseAuthRepositoryImpl : AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private fun mapFirebaseUser(firebaseUser: FirebaseUser?): User? {
        if (firebaseUser == null) return null
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "User",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString()
        )
    }

    override fun getCurrentUser(): User? {
        return mapFirebaseUser(firebaseAuth.currentUser)
    }

    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(mapFirebaseUser(auth.currentUser))
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun login(email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = mapFirebaseUser(result.user)
            if (user != null) {
                AuthResult.Success(user)
            } else {
                AuthResult.Error(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): AuthResult<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                // Update profile display name
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                firebaseUser.updateProfile(profileUpdates).await()
                
                val user = mapFirebaseUser(firebaseUser)
                if (user != null) {
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error(Exception("Firebase user is null after profile update"))
                }
            } else {
                AuthResult.Error(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
