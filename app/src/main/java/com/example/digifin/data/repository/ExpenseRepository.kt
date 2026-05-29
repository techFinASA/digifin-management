package com.example.digifin.data.repository

import com.example.digifin.data.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExpenseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    /**
     * Retrieves expenses from the "expenses" sub-collection inside the user's document.
     */
    fun getExpenses(): Flow<List<Expense>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val subscription = firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val expenses = snapshot.toObjects(Expense::class.java)
                    trySend(expenses)
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Fetches the most recent transactions with a specific limit.
     */
    suspend fun getRecentTransactions(limit: Int): List<Expense> {
        if (userId.isEmpty()) return emptyList()
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("expenses")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
                .toObjects(Expense::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addExpense(expense: Expense) {
        if (userId.isEmpty()) return
        val docRef = firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .document()
        val newExpense = expense.copy(id = docRef.id, userId = userId)
        docRef.set(newExpense).await()
    }

    suspend fun updateExpense(expense: Expense) {
        if (userId.isEmpty()) return
        firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .document(expense.id)
            .set(expense.copy(userId = userId))
            .await()
    }

    suspend fun deleteExpense(expenseId: String) {
        if (userId.isEmpty()) return
        firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .document(expenseId)
            .delete().await()
    }
}
