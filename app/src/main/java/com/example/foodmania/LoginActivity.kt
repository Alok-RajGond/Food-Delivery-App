package com.example.foodmania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.foodmania.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var password: String
    private lateinit var email: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //creating google sign in option for google sign in
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //Initializing google signIn client for google signIn
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        //initialize firebase auth ( it require in both for manullay entering the details and google log in)
        auth = Firebase.auth
        //initialize firebase database ( it require in both for manullay entering the details and google log in)
        database = FirebaseDatabase.getInstance()

        //login with email and password
        binding.loginBtn.setOnClickListener {
            //binding variable
            email = binding.emailInput.text.toString().trim()
            password = binding.passInput.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Fill All The Given Field!!", Toast.LENGTH_SHORT).show()
            } else {
                createUser()
            }
        }

        binding.dontHaveAcc.setOnClickListener {
            val intent = Intent(this, signInActivity::class.java)
            startActivity(intent)
        }

        //google sign in
        binding.googleLoginBtn.setOnClickListener { 
             val signinIntent = googleSignInClient.signInIntent
            launcher.launch(signinIntent)
        }
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "SIGN-In Failed!!", Toast.LENGTH_SHORT).show()
                        Log.d("Account", "Google sign-in: Failure", task.exception)
                    }

                }
            } else {
                Toast.makeText(this, "SIGN-In Failed!!", Toast.LENGTH_SHORT).show()
                Log.d("Account", "Google sign-in: Failure", task.exception)
            }
        } else {
            Toast.makeText(this, "SIGN-In Failed!!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun updateUi(user: FirebaseUser?) {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun createUser() {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            if (task.isSuccessful){
                val user = auth.currentUser
                updateUi(user)
            }else{
                val intent = Intent(this, signInActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Create Account First", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}