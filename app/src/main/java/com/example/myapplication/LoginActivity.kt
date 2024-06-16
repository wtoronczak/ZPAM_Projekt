package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

//logowanie
class LoginActivity : BaseActivity(), View.OnClickListener {


    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicjalizacja pól wejściowych i przycisku logowania
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword3)
        loginButton = findViewById(R.id.loginButton)

        // Ustawienie nasłuchiwania kliknięć przycisku logowania
        loginButton?.setOnClickListener{
            logInRegisteredUser()
        }

    }

    override fun onClick(view: View?) {
        if(view !=null){
            when (view.id){

                R.id.registerTextViewClickable ->{
                    // Przejście do ekranu rejestracji po kliknięciu linku
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

   //walidacja
    private fun validateLoginDetails(): Boolean {

        return when{
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }

            else -> {
                showErrorSnackBar("Your details are valid",false)
                true
            }
        }


    }

  //logowanie użytkownika
    private fun logInRegisteredUser(){


        if(validateLoginDetails()){
            val email = inputEmail?.text.toString().trim(){ it<= ' '}
            val password = inputPassword?.text.toString().trim(){ it<= ' '}

            // Logowanie za pomocą FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToMainActivity()
                        finish()

                    } else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }

 //przejście do głównej aktywności po zalogowaniu
    fun goToMainActivity() {

        val user = FirebaseAuth.getInstance().currentUser;
        val userEmail = user?.email.toString()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userEmail",userEmail)
        startActivity(intent)
    }

}
