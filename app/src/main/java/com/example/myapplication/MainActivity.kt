package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.firestore.DogBreed
import com.example.myapplication.firestore.DogCondition
import com.example.myapplication.firestore.DogFirestore
import com.example.myapplication.firestore.DogFirestoreHandler
import com.example.myapplication.recycleView.DogAdapter
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

        val userEmailTextView = findViewById<TextView>(R.id.userEmail)


        // Pobranie identyfikatora użytkownika przekazanego z poprzedniej aktywności
        val intent = intent
        val userEmail = intent.getStringExtra("userEmail")
        userEmailTextView.text = userEmail

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        /*
        var dogs = mutableListOf(
            DogFirestore(name = "Burek"),
            DogFirestore(name = "Sami"),
            DogFirestore(name = "Reksio"),
            DogFirestore(name = "Arsen"),
            DogFirestore(name = "Max"),
            DogFirestore(name = "Terminator"),
            DogFirestore(name = "Megatron"),
            DogFirestore(name = "Bumblebee"),
            DogFirestore(name = "Terminator"),
            DogFirestore(name = "Megatron"),
            DogFirestore(name = "Bumblebee")
        )*/

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                // Pobieranie danych w wątku IO
                var dogs = withContext(Dispatchers.IO) {
                    dogFirestoreHandler.getAllUserDogs(userEmail)
                }

                // Logowanie rozmiaru listy po pobraniu danych
                Log.d("GetAll", "${dogs.size}")

                // Ustawienie adaptera dla RecyclerView po pobraniu danych
                val adapter = DogAdapter(dogs)
                recyclerView.adapter = adapter
            }
        } else {
            var dogs = emptyList<DogFirestore>()
            val adapter = DogAdapter(dogs)
            recyclerView.adapter = adapter
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
        /*
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
         */



        //GetAll
        /*


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
         */
    }
}
