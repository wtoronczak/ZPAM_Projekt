package com.example.myapplication.firestore

import java.util.UUID

data class DogFirestore(
    var name: String = "",
    var age: Int = 0,
    var weight : Double = 0.0,
    var breed : DogBreed,
    var condition: DogCondition,
    var sportActivity : Int = 0,
    var isPregnant : Boolean = false,
    var isMale : Boolean= false,
    var photoUrl : String = "",

    var userEmail : String =""){

    val dogId: String = UUID.randomUUID().toString()
}
