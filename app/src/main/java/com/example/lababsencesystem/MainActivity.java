package com.example.lababsencesystem;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    Button login;
    EditText username, password;
    ProgressBar spinner;
    TextView loginError;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    Button showPass;
    CheckBox rememberMe;
    Preference preference=new Preference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            db.collection("users").document("doctors")
                    .collection("data")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("email").equals(firebaseUser.getEmail())) {
                                Doctor doctor = document.toObject(Doctor.class);
                                DoctorMain.doctor = doctor;
                                Intent i = new Intent(getApplicationContext(), DoctorMain.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                break;
                            }
                        }

                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            });
            db.collection("users").document("students")
                    .collection("data")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("email").equals(firebaseUser.getEmail())) {
                                Student student = document.toObject(Student.class);
                                StudentMain.student = student;
                                Intent i = new Intent(getApplicationContext(), StudentMain.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                break;
                            }
                        }

                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            });


        } else {
            setContentView(R.layout.activity_main);
            login = findViewById(R.id.login);
            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            spinner = findViewById(R.id.progressBar1);
            loginError = findViewById(R.id.loginError);
            showPass = findViewById(R.id.showPass);
            rememberMe = findViewById(R.id.rememberMe);

            SharedPreferences sp = getSharedPreferences("loginPreference", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();
            final CheckBox rememberMe = findViewById(R.id.rememberMe);
            username.setText(sp.getString("email", ""));
            password.setText(sp.getString("password", ""));
            rememberMe.setChecked(sp.getBoolean("rememberMe", false));

//        if ((preference.getEmail(MainActivity.this) !=null) && (preference.getPass(MainActivity.this)!=null)){
//            if(preference.getType(MainActivity.this).equalsIgnoreCase("student")){
//                Intent i = new Intent(MainActivity.this, StudentMain.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                finish();
//            }
//            else{
//                Intent i = new Intent(MainActivity.this, DoctorMain.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                finish();
//            }
//        }

            login.setOnClickListener(new View.OnClickListener() {
                private int found = 0;
                private int checked = 0;

                @Override
                public void onClick(View v) {
                    found =0;
//                if(rememberMe.isChecked())
//                    checked=1;
                    loginError.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    final String e = username.getText().toString();
                    final String p = password.getText().toString();
                    if (e.isEmpty()) {
                        username.setError("Please enter your username");
                        username.requestFocus();
                        spinner.setVisibility(View.GONE);
                    } else {
                        if (p.isEmpty()) {
                            password.setError("please add your password");
                            password.requestFocus();
                            spinner.setVisibility(View.GONE);
                        } else {
                            if (p.length() < 6) {
                                password.setError("Minimum password length must be 6");
                                password.requestFocus();
                                spinner.setVisibility(View.GONE);
                            } else {
                                db.collection("users").document("students")
                                        .collection("data")
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("TAG", document.getId() + "  for   => " + document.getData());
                                                Log.d("TAG", "  found   => " + found);

                                                if (document.get("username").equals(username.getText().toString())) {
                                                    found = 1;
                                                    Log.d("TAG", "  found   => " + found);

                                                    Log.d("TAG", document.getId() + " ifpass  => " + document.getData());
                                                    firebaseAuth.signInWithEmailAndPassword(document.get("email").toString(), password.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
//                                                            if (checked ==1){
//                                                                preference.saveAcc(username.getText().toString(),password.getText().toString(),MainActivity.this,"student");
//                                                            }
                                                                if (rememberMe.isChecked()) {
                                                                    editor.putBoolean("rememberMe", true);
                                                                    editor.putString("email", e);
                                                                    editor.putString("password", p);
                                                                    editor.commit();
                                                                } else {
                                                                    editor.putBoolean("rememberMe", false);
                                                                    editor.putString("email", "");
                                                                    editor.putString("password", "");
                                                                    editor.commit();
                                                                }
                                                                Intent i = new Intent(MainActivity.this, StudentMain.class);
                                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                                spinner.setVisibility(View.GONE);
                                                                loginError.setText("Error Password Incorrect");
                                                                loginError.setVisibility(View.VISIBLE);
                                                            }

                                                        }
                                                    });
                                                }
                                            }
                                            Log.d("TAG", "  found   before if => " + found);

                                            if (found == 0) {
                                                db.collection("users").document("doctors")
                                                        .collection("data")
                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                Log.d("TAG", document.getId() + "  for   => " + document.getData());
                                                                if (document.get("username").equals(username.getText().toString())) {
                                                                    found = 1;

                                                                    Log.d("TAG", document.getId() + " ifpass  => " + document.getData());
                                                                    firebaseAuth.signInWithEmailAndPassword(document.get("email").toString(), password.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(Task<AuthResult> task) {
                                                                            if (task.isSuccessful()) {
//                                                            if (checked ==1){
//                                                                preference.saveAcc(username.getText().toString(),password.getText().toString(),MainActivity.this,"doctor");
//                                                            }
                                                                                if (rememberMe.isChecked()) {
                                                                                    editor.putBoolean("rememberMe", true);
                                                                                    editor.putString("email", e);
                                                                                    editor.putString("password", p);
                                                                                    editor.commit();
                                                                                } else {
                                                                                    editor.putBoolean("rememberMe", false);
                                                                                    editor.putString("email", "");
                                                                                    editor.putString("password", "");
                                                                                    editor.commit();
                                                                                }
                                                                                Intent i = new Intent(MainActivity.this, DoctorMain.class);
                                                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(i);
                                                                                finish();
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                                                spinner.setVisibility(View.GONE);
                                                                                loginError.setText("Error Password Incorrect");
                                                                                loginError.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                            if (found == 0) {
                                                                loginError.setText("Error Username not found");
                                                                loginError.setVisibility(View.VISIBLE);
                                                                spinner.setVisibility(View.GONE);
                                                            }
                                                        } else {
                                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                            }

                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });


                            }
                        }
                    }
                }
            });


            showPass.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            break;
                        case MotionEvent.ACTION_UP:
                            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            break;
                    }
                    return true;
                }
            });


        }
    }

}
