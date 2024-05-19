package com.example.myapplication.firestore

import com.google.firebase.firestore.FieldPath
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
        firestore.collection(collectionName)
        val snapshot = FirebaseFirestore.getInstance().collection("dogs")
            .whereEqualTo(FieldPath.documentId(), dogId)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(DogFirestore::class.java)
    }

    override fun getAllUserDogs(userEmail: String): MutableList<DogFirestore> {
        val list = mutableListOf<DogFirestore>()

        firestore.collection(collectionName)
            .whereEqualTo(FieldPath.of(userEmail),userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    list.add(document.toObject(DogFirestore::class.java))
                }
            }
        return list
    }

}