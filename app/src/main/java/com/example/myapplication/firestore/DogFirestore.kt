package com.example.myapplication.firestore

import java.util.UUID

data class DogFirestore(
    var name: String = "",
    var age: Int = 0,
    var weight: Double = 0.0,
    var breed: DogBreed = DogBreed.small, // Przykładowa wartość domyślna
    var condition: DogCondition = DogCondition.normal, // Przykładowa wartość domyślna
    var sportActivity: Int = 0,
    var isPregnant: Boolean = false,
    var isMale: Boolean = false,
    var photoUrl: String = "",
    var userEmail: String = ""
) {
    val dogId: String = UUID.randomUUID().toString()
}
