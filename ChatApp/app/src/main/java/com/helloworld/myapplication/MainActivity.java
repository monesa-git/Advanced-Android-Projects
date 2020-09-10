package com.helloworld.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    //If an user is already logged in then it validates the user info and goes to the main page or it asks the user to login again.
    @Override
    protected void onStart() {
        super.onStart();
        showProgressBarDialog();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Log.d("demo","CurrentUser" + currentUser.getDisplayName());
            hideProgressBarDialog();
            Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
            //sending userid to the next activity and based on user id we can fetch the data from the firebase.
            intent.putExtra("user",currentUser.getUid());
            startActivity(intent);
        } else{
            Log.d("demo","Please login to go see your contacts");
            hideProgressBarDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Chat Room Login");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //buttonLogin
        //Action : If the username and password is correct then it goes to the chat room activity or it says authentication failed.
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailTextView = findViewById(R.id.loginText);
                EditText passwordTextView = findViewById(R.id.passwordText);
                if(checkValidations(emailTextView) && checkValidations(passwordTextView) && checkEmailValidations(emailTextView)){
                    showProgressBarDialog();
                    mAuth.signInWithEmailAndPassword(emailTextView.getText().toString().trim(), passwordTextView.getText().toString().trim())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("demo", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        hideProgressBarDialog();
                                        Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
                                        //sending userid to the next activity and based on user id we can fetch the data from the firebase.
                                        intent.putExtra("user",user.getUid());
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("demo", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        hideProgressBarDialog();
                                    }
                                }
                            });
                }
            }
        });

        //buttonSignup
        //Actions: for calling sign up screen
        findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    //for showing the progress dialog
    public void showProgressBarDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //for hiding the progress dialog
    public void hideProgressBarDialog()
    {
        progressDialog.dismiss();
    }

    //Regex for checking email validations
    public boolean checkEmailValidations(EditText editText)
    {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!editText.getText().toString().trim().matches(emailPattern))
        {
            editText.setError("Invalid Email");
            return false;
        }
        else
        {
            return true;
        }
    }

    //For checking the empty strings
    public boolean checkValidations(EditText editText){
        if(editText.getText().toString().trim().equals("")){
            editText.setError("Cannot be empty");
            return false;
        }else{
            return true;
        }
    }


}