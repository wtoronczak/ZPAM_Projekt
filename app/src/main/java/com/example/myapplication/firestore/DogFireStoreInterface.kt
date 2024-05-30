package com.example.myapplication.firestore

interface DogFireStoreInterface {
    // dodawanie do Firestore
    fun addDog (dog : DogFirestore)
    suspend fun deleteDog (dogId : String)
    suspend fun editDog (dog: DogFirestore)
    suspend fun getByDogId (dogId: String): DogFirestore?
    suspend fun getAllUserDogs (userEmail: String): List<DogFirestore>

}