package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// rejestracja użytkownika

class RegisterActivity : BaseActivity() {

    private var registerButton: Button? = null
    private var inputEmail: EditText? = null
    private var inputName: EditText? = null
    private var inputPassword: EditText? = null
    private var inputRepPass: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicjalizacja pól wejściowych i przycisku rejestracji
        registerButton = findViewById(R.id.registerButton)
        inputEmail = findViewById(R.id.inputLEmail)
        inputName = findViewById(R.id.inputName)
        inputPassword = findViewById(R.id.inputPassword2)
        inputRepPass = findViewById(R.id.inputPassword2repeat)

        // Ustawienie nasłuchiwania kliknięć przycisku rejestracji
        registerButton?.setOnClickListener{
            registerUser()
        }
    }

   //walidacja
    private fun validateRegisterDetails(): Boolean {

        return when{
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(inputName?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name),true)
                false
            }
            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            TextUtils.isEmpty(inputRepPass?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_reppassword),true)
                false
            }
            inputPassword?.text.toString().trim {it <= ' '} != inputRepPass?.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch),true)
                false
            }
            inputPassword?.text.toString().trim {it <= ' '}.count() < 8 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_too_short),true)
                false
            }
            else -> true
        }
    }

    //Metoda przechodzenia do aktywności logowania

    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // finish(), po to aby użytkownik nie mógł już wrócić do aktualnej aktywności bez restartowania aplikacji
    }


     // Metoda rejestracji do Firebase

    private fun registerUser(){
        if (validateRegisterDetails()){
            val login: String = inputEmail?.text.toString().trim() {it <= ' '}
            val password: String = inputPassword?.text.toString().trim() {it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login,password)
                .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    showErrorSnackBar(
                        "You are registered successfully. Your user id is ${firebaseUser.uid}",
                        false
                    )
                    userRegistrationSuccess()
                    FirebaseAuth.getInstance().signOut()
                    finish()

                } else {
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
        }
    }

    //Metoda wywoływana po udanej rejestracji - wyświetlenie Toast (mini komunikat)

    fun  userRegistrationSuccess(){
        Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_success), Toast.LENGTH_LONG).show()
    }
}
