package com.enesuzumcu.shoppingapp.data.remote.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {

    fun getUid() : String? = firebaseAuth.uid

    fun getUserInformation(userId: String): DocumentReference {
        return firebaseFirestore.collection("users").document(userId)
    }

    fun getProductsInBasket(userId: String): CollectionReference{
        return firebaseFirestore.collection("baskets").document(userId).collection("basket")
    }

    fun addProductInBasket(userId: String,productId: String) : DocumentReference{
        return firebaseFirestore.collection("baskets").document(userId).collection("basket").document(productId)
    }

    fun deleteAllProductsInBasket(userId: String) :CollectionReference{
        return firebaseFirestore.collection("baskets").document(userId).collection("basket")
    }

    fun signOut() { firebaseAuth.signOut()}
}