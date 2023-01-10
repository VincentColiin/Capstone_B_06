package com.capstone.mcov_naskes_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var regisButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        regisButton = findViewById(R.id.regisButton)
        loginButton = findViewById(R.id.loginButton)

        if(auth.currentUser != null){
            Handler(Looper.getMainLooper()).postDelayed({
                val loginIntent: Intent = Intent(this,MainActivity::class.java)
                startActivity(loginIntent)
                finish()
            },1000)
        }

        loginButton.setOnClickListener{
            login()
        }

        regisButton.setOnClickListener{
            register()
        }
    }

    private fun login() {
        val eEmail = findViewById<EditText>(R.id.editEmail).text.toString().trim()
        val ePassword = findViewById<EditText>(R.id.editPassword).text.toString().trim()

        if(eEmail.isNotEmpty() && ePassword.isNotEmpty()){
            auth.signInWithEmailAndPassword(eEmail,ePassword)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val loginIntent: Intent = Intent(this,MainActivity::class.java)
                        startActivity(loginIntent)
                        finish()
                    }else{
                        Toast.makeText(this,it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this,"Isi Semua field", Toast.LENGTH_SHORT).show()
        }
    }

    private fun register() {
        val registerIntent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }
}