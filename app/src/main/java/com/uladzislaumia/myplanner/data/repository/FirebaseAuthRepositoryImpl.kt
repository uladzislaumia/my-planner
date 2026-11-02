package com.uladzislaumia.myplanner.data.repository

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.uladzislaumia.myplanner.domain.model.AuthResult
import com.uladzislaumia.myplanner.domain.model.User
import com.uladzislaumia.myplanner.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val firebaseAnalytics: FirebaseAnalytics,
    ) : AuthRepository {
        private fun mapFirebaseUser(firebaseUser: FirebaseUser?): User? {
            if (firebaseUser == null) return null
            return User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "User",
                email = firebaseUser.email ?: "",
                avatarUrl = firebaseUser.photoUrl?.toString(),
            )
        }

        override fun getCurrentUser(): User? = mapFirebaseUser(firebaseAuth.currentUser)

        override fun observeAuthState(): Flow<User?> =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        trySend(mapFirebaseUser(auth.currentUser))
                    }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

        // Any sign in failure is reported to the user instead of crashing the app
        override suspend fun login(
            email: String,
            password: String,
        ): AuthResult<User> =
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = mapFirebaseUser(result.user)
                if (user != null) {
                    firebaseAnalytics.setUserId(user.id)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                        param(FirebaseAnalytics.Param.METHOD, "email")
                    }
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error(Exception("Firebase user is null"))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error signing in with email and password")
                AuthResult.Error(e)
            }

        // Any sign up failure is reported to the user instead of crashing the app
        override suspend fun signUp(
            name: String,
            email: String,
            password: String,
        ): AuthResult<User> =
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // Update profile display name
                    val profileUpdates =
                        userProfileChangeRequest {
                            displayName = name
                        }
                    firebaseUser.updateProfile(profileUpdates).await()

                    val user = mapFirebaseUser(firebaseUser)
                    if (user != null) {
                        firebaseAnalytics.setUserId(user.id)
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                            param(FirebaseAnalytics.Param.METHOD, "email")
                        }
                        AuthResult.Success(user)
                    } else {
                        AuthResult.Error(Exception("Firebase user is null after profile update"))
                    }
                } else {
                    AuthResult.Error(Exception("Firebase user is null"))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error creating user with email and password")
                AuthResult.Error(e)
            }

        override suspend fun logout() {
            firebaseAnalytics.logEvent("logout", null)
            firebaseAnalytics.setUserId(null)
            firebaseAuth.signOut()
        }
    }
