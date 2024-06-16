package com.example.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.firestore.DogFirestore
import com.example.myapplication.firestore.DogFirestoreHandler
import com.example.myapplication.recycleView.DogAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

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


        setDailyReminder()
    }
    fun setDailyReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Set the alarm to start at 7 AM
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 28)
        }

        // Set the alarm to repeat daily
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun goToAddDogActivity(userEmail: String?) {
        val intent = Intent(this, AddDogActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
    }
}
