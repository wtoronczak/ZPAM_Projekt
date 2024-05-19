package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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

        // Inicjalizacja pola tekstowego powitania i ustawienie tekstu powitalnego
        welcomeTextView = findViewById(R.id.welcomeText)


        // Testowe działania
        //Add
        if(userEmail != null){
            val dog1 = DogFirestore("Reksio",1,5.0,DogBreed.huge,DogCondition.skinny,1,false,false,"", userEmail)
            GlobalScope.launch(Dispatchers.Main) {
                // Dodanie psa do bazy danych Firestore
                dogFirestoreHandler.addDog(dog1)
            }
        }

        //Delete
        /*val dogId = "d615e168-d511-44af-8a59-7e3a629df0a3"
        GlobalScope.launch(Dispatchers.Main) {
            //Usuniecie psa z bazy danych Firestore
            dogFirestoreHandler.deleteDog(dogId)
        }*/

        //GetById
        /*
        val dogId = "e9b4c811-1dcb-475a-838e-cf9ab88ce161"
        GlobalScope.launch(Dispatchers.Main) {
            //Usuniecie psa z bazy danych Firestore
            Log.d("GetById", "Before")
            var dog = dogFirestoreHandler.getByDogId(dogId)
            if(dog != null) {
               Log.d("GetById", "Dog name ${dog.name}")
            }
        }*/


        //GetAll
        if(userEmail != null) {
            GlobalScope.launch(Dispatchers.Main) {
                //Usuniecie psa z bazy danych Firestore
                Log.d("GetAll", "Before")
                var dogs = dogFirestoreHandler.getAllUserDogs(userEmail)
                Log.d("GetAll", "After")
                for(dog in dogs){
                    Log.d("GetAll", "Dog name ${dog.name}")
                }
            }
        }
    }
}
