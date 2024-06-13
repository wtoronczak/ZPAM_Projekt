package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

/**
 * Główna aktywność aplikacji, wyświetlająca powitanie użytkownika.
 */
class MainActivity : BaseActivity() {
    private val dogFirestoreHandler = DogFirestoreHandler()
    private var welcomeTextView: TextView? = null
    private var addDogButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addDogButton = findViewById(R.id.addDogButton)

        val userEmailTextView = findViewById<TextView>(R.id.userEmail)

        // Pobranie identyfikatora użytkownika przekazanego z poprzedniej aktywności
        val intent = intent
        val userEmail = intent.getStringExtra("userEmail")
        userEmailTextView.text = userEmail

        // Ustawienie nasłuchiwania kliknięć przycisku logowania
        addDogButton?.setOnClickListener{
            goToAddDogActivity(userEmail)
            finish()
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)


        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                // Pobieranie danych w wątku IO
                var dogs = withContext(Dispatchers.IO) {
                    dogFirestoreHandler.getAllUserDogs(userEmail)
                }.toMutableList()

                // Logowanie rozmiaru listy po pobraniu danych
                Log.d("GetAll", "${dogs.size}")

                // Ustawienie adaptera dla RecyclerView po pobraniu danych
                val adapter = DogAdapter(dogs)
                recyclerView.adapter = adapter
            }
        } else {
            var dogs = mutableListOf<DogFirestore>()
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

    }


    fun goToAddDogActivity(userEmail: String?) {
        val intent = Intent(this, AddDogActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
    }
}
