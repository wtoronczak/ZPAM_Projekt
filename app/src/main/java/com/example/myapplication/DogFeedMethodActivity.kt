package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.myapplication.firestore.DogBreed
import com.example.myapplication.firestore.DogCondition
import com.example.myapplication.firestore.DogFirestore
import com.example.myapplication.firestore.DogFirestoreHandler
import com.google.type.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter




class DogFeedMethodActivity : BaseActivity() {
    private val dogFirestoreHandler = DogFirestoreHandler()
    private var portionValueTextView: TextView? = null
    private var frequencyValueTextView: TextView? = null
    private var hoursValueTextView: TextView? = null

    private var returnButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dog_feed_method)

        val intent = intent
        val userEmail = intent.getStringExtra("userEmail")
        val dogId = intent.getStringExtra("dogId")

        setupView()

        returnButton?.setOnClickListener{
            goToMainActivity(userEmail)
            finish()
        }
        if(dogId != null){
            GlobalScope.launch(Dispatchers.Main) {
                val dog = dogFirestoreHandler.getByDogId(dogId)
                if(dog != null){
                    val dogFeedContext = calculateDogFeedContext(dog)
                    updateView(dogFeedContext)
                }
            }
        }
    }

    fun setupView(){
        returnButton = findViewById(R.id.returnButton)
        portionValueTextView = findViewById(R.id.portionValueTextView)
        frequencyValueTextView = findViewById(R.id.frequencyValueTextView)
        hoursValueTextView = findViewById(R.id.hoursValueTextView)
    }

    fun goToMainActivity(userEmail: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
    }

    fun calculateDogFeedContext(dog : DogFirestore): DogFeedContext{
        var percent = 0.3
        var frequency = 3

        //age
        if(dog.age <= 1) percent += 0.21
        if(dog.age == 2) percent += 0.13

        //activity
        if(dog.sportActivity == 2) percent += 0.04
        if(dog.sportActivity == 3) percent += 0.07
        if(dog.sportActivity == 4) percent += 0.09
        if(dog.sportActivity == 5) percent += 0.187

        //pregnancy
        if(dog.isPregnant) percent += 0.02

        //condition
        if(dog.condition == DogCondition.skinny) {
            frequency += 1
            percent += 0.02
        }

        if(dog.condition == DogCondition.chunky){
            if(dog.breed == DogBreed.huge || dog.breed == DogBreed.big) percent -= 0.05
            else percent -= 0.02
        }

        val gramPerDay = Math.round(percent * dog.weight * 100).toInt()

        var hours = listOf<Int>()
        if(frequency == 3) hours = listOf(7,13,19)
        if(frequency == 4) hours = listOf(7,11,15,19)

        // Z czata wzięte
        val today = LocalDateTime.now().toLocalDate()
        val dates = hours.map { LocalDateTime.of(today, LocalTime.of(it, 0)) }

        return DogFeedContext(gramPerDay, frequency, dates)
    }


    fun updateView(dogFeedContext: DogFeedContext) {
        portionValueTextView?.text = dogFeedContext.gramsPerDay.toString()
        frequencyValueTextView?.text = dogFeedContext.feedingFrequency.toString()

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        var hoursAsString = ""
        for(datetime in dogFeedContext.feedingHours){
            hoursAsString += formatter.format(datetime)
            hoursAsString += "\n"
        }
        hoursAsString.removeSuffix("\n")
        hoursValueTextView?.text = hoursAsString

    }
}