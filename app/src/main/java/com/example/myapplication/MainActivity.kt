package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.firestore.DogBreed
import com.example.myapplication.firestore.DogCondition
import com.example.myapplication.firestore.DogFirestore
import com.example.myapplication.firestore.DogFirestoreHandler
import kotlinx.coroutines.*

/**
 * Główna aktywność aplikacji, wyświetlająca powitanie użytkownika.
 */
class MainActivity : AppCompatActivity() {

    private val dogFirestoreHandler = DogFirestoreHandler()
    private var welcomeTextView: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pobranie identyfikatora użytkownika przekazanego z poprzedniej aktywności
        val intent = intent
        val userEmail = intent.getStringExtra("userEmail")
        Log.d("Email", "$userEmail")
        // Inicjalizacja pola tekstowego powitania i ustawienie tekstu powitalnego
        //welcomeTextView = findViewById(R.id.welcomeText)

        val dogId = "158feb82-eb0f-4484-a443-b2894e6ebbc6"
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                Log.d("GetById", "Before")
                val dog = dogFirestoreHandler.getByDogId(dogId)
                if (dog != null) {
                    Log.d("GetById", "Dog name ${dog.name}")
                } else {
                    Log.d("GetById", "Dog not found")
                }
            } catch (e: Exception) {
                Log.e("GetById", "Error fetching dog by ID", e)
            }
        }

        // Testowe działania
        //Add
        /*
        if(userEmail != null){
            val dog1 = DogFirestore("Reksio",1,5.0,DogBreed.huge,DogCondition.skinny,1,false,false,"", userEmail)
            GlobalScope.launch(Dispatchers.Main) {
                // Dodanie psa do bazy danych Firestore
                dogFirestoreHandler.addDog(dog1)
            }
        }
        */
        //Delete
        /*val dogId = "d615e168-d511-44af-8a59-7e3a629df0a3"
        GlobalScope.launch(Dispatchers.Main) {
            //Usuniecie psa z bazy danych Firestore
            dogFirestoreHandler.deleteDog(dogId)
        }*/

        //GetById




        //GetAll
        if(userEmail != null) {
            GlobalScope.launch(Dispatchers.Main) {
                // Wszystkie psy użytkownika
                Log.d("GetAll", "Before")
                var dogs = dogFirestoreHandler.getAllUserDogs(userEmail)
                Log.d("GetAll", "After + ${dogs.size}")
                for(dog in dogs){
                    Log.d("GetAll", "Dog name ${dog.name}")
                }
            }
        }
    }
}
