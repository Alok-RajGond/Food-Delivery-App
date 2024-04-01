package com.example.foodmania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.foodmania.databinding.ActivitySignInBinding
import com.example.foodmania.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class signInActivity : AppCompatActivity() {
    private lateinit var userName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //creating google sign in option for google sign in
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //Initializing google signIn client for google signIn
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        //initialize firebase auth ( it require in both for manullay entering the details and google sign in)
        auth = Firebase.auth
        //initialize firebase database ( it require in both for manullay entering the details and google sign in)
        database = Firebase.database.reference


        //create Account Button by entering the details
        binding.signUpBtn.setOnClickListener {
            userName = binding.userName.text.toString()
            password = binding.passInput.text.toString().trim()
            email = binding.emailInput.text.toString().trim()

            if (userName.isBlank() || password.isBlank() || email.isBlank()) {
                Toast.makeText(this, "Fill All The Given Field!!", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        binding.alreadyHaveAcc.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //for googel sign in
        binding.googleLoginBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    //launcher for google sign in
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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


    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "SIGN UP IS COMPLETE!!", Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "SIGN UP IS COMPLETE!!", Toast.LENGTH_SHORT).show()
                Log.d("Account", "create Account: Failure", task.exception)
            }
        }
    }

    private fun saveUserData() {
        //retrive data from input filed and store in database
        userName = binding.userName.text.toString()
        password = binding.passInput.text.toString().trim()
        email = binding.emailInput.text.toString().trim()

        val user = UserModel(userName, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //here we save data in firebase database
        database.child("user").child(userId).setValue(user)
    }
}