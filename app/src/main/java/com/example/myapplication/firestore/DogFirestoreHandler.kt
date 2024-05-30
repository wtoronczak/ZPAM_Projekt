package com.example.myapplication.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DogFirestoreHandler: DogFireStoreInterface {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionName: String = "dogs"
    override fun addDog(dog: DogFirestore) {
        firestore.collection(collectionName)
            .document(dog.dogId)
            .set(dog)
            .addOnSuccessListener {
                println("Dog Added")
            }
            .addOnFailureListener { e ->
                println("Error adding dog $e")
            }

    }

    override suspend fun deleteDog(dogId: String) {
        firestore.collection(collectionName)
            .document(dogId)
            .delete()
            .await()

    }

    override suspend fun editDog(dog: DogFirestore) {
        firestore.collection(collectionName)
            .document(dog.dogId)
            .set(dog)
            .await()
    }

    override suspend fun getByDogId(dogId: String):DogFirestore? {
        val snapshot = firestore.collection(collectionName)
            .document(dogId)
            .get()
            .await()

        return snapshot.toObject(DogFirestore::class.java)
    }

    override suspend fun getAllUserDogs(userEmail: String): List<DogFirestore> {
        return try {
            val snapshot = firestore.collection(collectionName)
                .whereEqualTo("userEmail", userEmail)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(DogFirestore::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

}