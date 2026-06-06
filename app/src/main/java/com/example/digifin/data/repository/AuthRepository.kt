package com.example.digifin.data.repository

import android.util.Log
import com.example.digifin.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun getUserData(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Check if user document exists in Firestore
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                if (!userDoc.exists()) {
                    // If no document exists, the user didn't complete registration properly
                    auth.signOut()
                    return Result.failure(Exception("User profile not found in database."))
                }
            }
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        pass: String,
        firstName: String,
        lastName: String,
        country: String,
        currency: String
    ): Result<FirebaseUser?> {
        Log.d("AuthRepository", "Starting registration for: $email")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val firebaseUser = result.user
            Log.d("AuthRepository", "Firebase Auth success: ${firebaseUser?.uid}")
            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    country = country,
                    currency = currency
                )
                Log.d("AuthRepository", "Saving user to Firestore...")
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
                Log.d("AuthRepository", "Firestore save success")
                
                // Send Firebase native verification email
                firebaseUser.sendEmailVerification().await()
                Log.d("AuthRepository", "Verification email sent")
            }
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed", e)
            Result.failure(e)
        }
    }

    suspend fun reloadUser(): Boolean {
        return try {
            auth.currentUser?.reload()?.await()
            auth.currentUser?.isEmailVerified ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateProfile(firstName: String, lastName: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection("users").document(uid).update(
                mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
