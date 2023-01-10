package com.capstone.mcov_naskes_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.capstone.mcov_naskes_v1.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var firestoreDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        registerButton = findViewById(R.id.avRegisButton)
        firestoreDatabase = FirebaseFirestore.getInstance()

        registerButton.setOnClickListener{
            registerEvent()
        }
    }

    private fun registerEvent() {
        val email = findViewById<EditText>(R.id.avEditEmail).text.toString().trim()
        val pass = findViewById<EditText>(R.id.avEditPassword).text.toString().trim()
        val verifyPass = findViewById<EditText>(R.id.avEditRePassword).text.toString().trim()
        val firstName = findViewById<EditText>(R.id.avEditFirstName).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.avEditLastName).text.toString().trim()

        if(email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()){
            if(pass == verifyPass) {
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        createUser(firstName,lastName,auth.currentUser?.uid)
//                        createCondition(auth.currentUser?.uid)
                        Toast.makeText(this,"Success Register", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            val mainIntent: Intent = Intent(this,MainActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        },500)
                    }else{
                        Toast.makeText(this,it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Konfirmasi Password tidak sesuai", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"Isi semua field", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUser(firstName:String, lastName:String, UID:String?) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                val user = User(UID, firstName, lastName,it.result, null)
                firestoreDatabase.collection("naskes")
                    .add(user)
                    .addOnCompleteListener {res->
                        if (!res.isSuccessful)
                            Toast.makeText(this, res.exception?.message, Toast.LENGTH_SHORT).show()
                    }
            }
    }
}