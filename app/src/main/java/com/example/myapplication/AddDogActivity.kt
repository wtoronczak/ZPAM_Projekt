package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import com.example.myapplication.firestore.DogBreed
import com.example.myapplication.firestore.DogCondition
import com.example.myapplication.firestore.DogFirestore
import com.example.myapplication.firestore.DogFirestoreHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddDogActivity : BaseActivity() {
    private val dogFirestoreHandler = DogFirestoreHandler() //zapytania do bazy danych
//spiner = dropdown
    private var breedSpinner: Spinner? = null
    private var conditionSpinner : Spinner? = null
    private var sexSpinner : Spinner? = null
    private var isPregnantSpinner : Spinner? = null

    private var nameEditText: EditText? = null
    private var weightEditText: EditText? = null
    private var ageEditText: EditText? = null
//seekbar = pasek do przesuwania dog activity 1-5
    private var sportActivitySeekBar: SeekBar? = null

    private var submitButton: Button? = null
    private var returnButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dog)

        val intent = intent  // intent wykorzystywany do przesyłania danych pomiędzy aktywnościami
        val userEmail = intent.getStringExtra("userEmail")

        setupView()
        setupSpinners()

        submitButton?.setOnClickListener{
            val isAddDogSuccess = addDog(userEmail)

            if(isAddDogSuccess){
                goToMainActivity(userEmail)
                finish()
            }
        }

        returnButton?.setOnClickListener{
            goToMainActivity(userEmail)
            finish()
        }
    }

    fun setupView(){
        breedSpinner = findViewById(R.id.breedSpinner)
        conditionSpinner = findViewById(R.id.conditionSpinner)
        sexSpinner = findViewById(R.id.sexSpinner)
        isPregnantSpinner = findViewById(R.id.isPregnantSpinner)

        nameEditText = findViewById(R.id.nameEditText)
        weightEditText = findViewById(R.id.weightEditText)
        ageEditText = findViewById(R.id.ageEditText)

        sportActivitySeekBar = findViewById(R.id.sportActivitySeekBar)

        submitButton = findViewById(R.id.submitButton)
        returnButton = findViewById(R.id.returnButton)
    }
    fun setupSpinners(){

        //Breed dog spinner
        val breedOptions = arrayOf("Select Breed", "small", "medium", "big", "huge")
        //adapter przechowuje dane o dropdownie
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, breedOptions)
        //przypisanie widoku customowego do adaptera - wycentrowanie
        adapter.setDropDownViewResource(R.layout.spiner_view_design)

        breedSpinner?.adapter = adapter

        //Dog Condition Spinner
        val conditionOptions = arrayOf("Select Condition", "skinny", "normal", "chunky")

        val conditionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, conditionOptions)
        conditionAdapter.setDropDownViewResource(R.layout.spiner_view_design)
        conditionSpinner?.adapter = conditionAdapter


        //Dog Sex Spinner
        val sexSpinnerOptions = arrayOf("Select Sex", "male", "female")

        val sexSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sexSpinnerOptions)
        sexSpinnerAdapter.setDropDownViewResource(R.layout.spiner_view_design)
        sexSpinner?.adapter = sexSpinnerAdapter


        //isPregnant Spinner
        val isPregnantSpinnerOptions = arrayOf("Is Pregnant?", "yes", "no")

        val isPregnantSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, isPregnantSpinnerOptions)
        isPregnantSpinnerAdapter.setDropDownViewResource(R.layout.spiner_view_design)
        isPregnantSpinner?.adapter = isPregnantSpinnerAdapter
    }
    fun addDog(userEmail: String?): Boolean{
        // pobranie wartości w stringach
        val nameStr = nameEditText?.text.toString()
        val ageStr = ageEditText?.text.toString()
        val weightStr = weightEditText?.text.toString()

        val breedStr = breedSpinner?.getSelectedItem().toString()
        val conditionStr = conditionSpinner?.getSelectedItem().toString()
        val sexStr = sexSpinner?.getSelectedItem().toString()
        val isPregnantStr = isPregnantSpinner?.getSelectedItem().toString()

        val sportActivity = sportActivitySeekBar?.progress ?: 0

        //walidacja danych i konwetowanie na odpowiednie typy

        if(nameStr.isBlank()){
            Toast.makeText(this,"Dog name can't be empty",Toast.LENGTH_SHORT).show()
            return false
        }

        if (ageStr.isBlank()) {
            Toast.makeText(this, "Age can't be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        val age = ageStr.toInt()

        if (weightStr.isBlank()) {
            Toast.makeText(this, "Weight can't be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        val weight = weightStr.toDouble()

        if (breedStr.isBlank()) {
            Toast.makeText(this, "Breed can't be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        // sprawdzenie czy wartości są zdefiniowane w enum class
        val breed = try {
            DogBreed.valueOf(breedStr)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Please enter a proper breed value", Toast.LENGTH_SHORT).show()
            return false
        }

        if (conditionStr.isBlank()) {
            Toast.makeText(this, "Condition can't be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val condition = try {
            DogCondition.valueOf(conditionStr)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Please enter a proper condition value", Toast.LENGTH_SHORT).show()
            return false
        }

        if (sexStr.isBlank() || (sexStr != "male" && sexStr != "female")) {
            Toast.makeText(this, "Enter proper sex value", Toast.LENGTH_SHORT).show()
            return false
        }

        val isMale = sexStr == "male"

        if (isPregnantStr.isBlank() || (isPregnantStr != "yes" && isPregnantStr != "no")) {
            Toast.makeText(this, "Enter proper is pregnant value", Toast.LENGTH_SHORT).show()
            return false
        }

        val isPregnant = isPregnantStr == "yes"

        if(userEmail == null){
            Toast.makeText(this, "Can't find userEmail", Toast.LENGTH_SHORT).show()
            return false
        }


        //create dog object -
        val dog = DogFirestore(nameStr, age, weight, breed, condition, sportActivity + 1, isPregnant, isMale, "", userEmail)


        //add dog to firestore
        GlobalScope.launch(Dispatchers.Main) {
            // Dodanie psa do bazy danych Firestore
            dogFirestoreHandler.addDog(dog)
        }

        return true
    }

    fun goToMainActivity(userEmail: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
    }
}