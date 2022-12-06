package com.example.newsapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var googleSignInClient: GoogleSignInClient

//    var callbackManager:CallbackManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        textView = findViewById(R.id.gotoRegister)
        email = findViewById(R.id.inputEmail)
        password = findViewById(R.id.inputPassword)
//        callbackManager = CallbackManager.Factory.create()


        textView.setOnClickListener{

            intent = Intent(this,Register::class.java)


            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1029624286917-4e39tt29obcpgcp8mrlsar4dr29o05l6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
        findViewById<ImageView>(R.id.googleLogin).setOnClickListener{
            signInGoogle()
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener{
            if(checking()){
                val Email = email.text.toString()
                val Password = password.text.toString()
                auth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this){
                            task->
                        if (task.isSuccessful){
                            var intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this,"Login Successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this,"Wrong Details", Toast.LENGTH_LONG).show()
                        }
                    }

            }else{
                Toast.makeText(this,"Enter all the details", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun checking():Boolean{
        email = findViewById(R.id.inputEmail)
        password = findViewById(R.id.inputPassword)
        if(email.text.toString().trim{it<=' '}.isNotEmpty() && password.text.toString().trim{it<=' '}.isNotEmpty()){
            return true
        }
        return false
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if (result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }

    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {

        if(task.isSuccessful){
            val account : GoogleSignInAccount?=task.result
            if (account!=null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this,"Succesfully Login", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }



}
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager?.onActivityResult(requestCode,resultCode,data)
//    }
