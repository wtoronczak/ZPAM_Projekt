package com.example.myapplication

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
import java.util.Calendar


class DogFeedMethodActivity : BaseActivity() {
    private val dogFirestoreHandler = DogFirestoreHandler()
    private var portionValueTextView: TextView? = null
    private var frequencyValueTextView: TextView? = null
    private var hoursValueTextView: TextView? = null

    private var returnButton: ImageButton? = null
    private var notificationButton: ImageButton? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Uprawnienie przyznane
        } else {
            // Uprawnienie nie przyznane
            // Możesz pokazać użytkownikowi informację, że uprawnienie jest wymagane
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dog_feed_method)

        val intent = intent
        val userEmail = intent.getStringExtra("userEmail")
        val dogId = intent.getStringExtra("dogId")

        setupView()
        //przycisk cofania
        returnButton?.setOnClickListener{
            goToMainActivity(userEmail)
            finish()
        }
        //pobranie psa, następnie wyliczenie karmy i odświeżenie widoku
        if(dogId != null){
            GlobalScope.launch(Dispatchers.Main) {
                val dog = dogFirestoreHandler.getByDogId(dogId)
                if(dog != null){
                    val dogFeedContext = calculateDogFeedContext(dog)
                    updateView(dogFeedContext)
                }
            }
        }
        //dodanie kanału
        createNotificationChannel(this)
        //sprawdzenie czy wersja jest odpowiednia i uprawnienia
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Uprawnienie już przyznane
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    // Pokaż wyjaśnienie, dlaczego uprawnienie jest potrzebne
                    // Następnie poproś o uprawnienie
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Bezpośrednio poproś o uprawnienie
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Dla starszych wersji SDK, uprawnienie nie jest wymagane
        }


        notificationButton?.setOnClickListener{
            if (userEmail != null) {
                sendNotification(this, userEmail)
            }
        }
    }

    fun setupView(){
        returnButton = findViewById(R.id.returnButton)
        notificationButton = findViewById(R.id.notificationButton)
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

        // mapowanie listy intów na odpowiadające godziny (wzięto z Chatgpt)
        val today = LocalDateTime.now().toLocalDate()
        val dates = hours.map { LocalDateTime.of(today, LocalTime.of(it, 0)) }

        return DogFeedContext(gramPerDay, frequency, dates)
    }


    fun updateView(dogFeedContext: DogFeedContext) {
        portionValueTextView?.text = dogFeedContext.gramsPerDay.toString()
        frequencyValueTextView?.text = dogFeedContext.feedingFrequency.toString()


        // formatowanie z daty na tekst godzin karmienia
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        var hoursAsString = ""
        for(datetime in dogFeedContext.feedingHours){
            hoursAsString += formatter.format(datetime)
            //\n nowa linia
            hoursAsString += "\n"
        }
        hoursAsString.removeSuffix("\n") // usunięcie ostatniego
        hoursValueTextView?.text = hoursAsString
    }


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "HauFood"
            val descriptionText = "Channel for HauFood"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, userEmail:String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("userEmail", userEmail)
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "id")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Remember to feed your dogs !")
            .setContentText("At 7 AM give your dog breakfast")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

}