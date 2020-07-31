package com.example.sih;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "test";

    EditText name,email,phone,pwd,c_pwd,v_type, v_num;
    ImageView signup;
    TextView login;
    String userID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        pwd = findViewById(R.id.pwd);
        c_pwd = findViewById(R.id.confirm_pwd);
        signup = findViewById(R.id.register);
        login = findViewById(R.id.login_link);
        v_type=findViewById(R.id.veh_type);
        v_num = findViewById(R.id.veh_num);


//        toolbar.setTitle(R.string.app_name);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

//        If the user is already logged in, take him to main activity
//        if(firebaseAuth.getCurrentUser() != null) {
//            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
//            finish();
//        }



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = email.getText().toString().trim();
                final String password = pwd.getText().toString().trim();
                String c_password = c_pwd.getText().toString().trim();
                final String full_name = name.getText().toString();
                final String phone_num = phone.getText().toString();
                final String veh_type = v_type.getText().toString();
                final String veh_num = v_num.getText().toString();



                if(TextUtils.isEmpty(mail)) {
                    email.setError("Email is required");
                    return;
                }

                if (!mail.contains("@")) {
                    email.setError("Enter valid e-mail address");
                }

                if(phone.length()!=10) {
                    phone.setError("Enter valid phone number");
                }

                if(TextUtils.isEmpty(password)) {
                    pwd.setError("Password is required");
                    return;
                }

                if (password.length()<6){
                    pwd.setError("Password must be greater than 6 characters");
                }

                if (!password.equals(c_password)) {
                    pwd.setError("Password doesn't match. Re-enter password.");
                    pwd.setText("");
                    c_pwd.setText("");
                    return;
                }


                firebaseAuth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registered Successfully",
                                            Toast.LENGTH_LONG).show();

                                    userID = firebaseAuth.getCurrentUser().getUid();

                                    DocumentReference documentReference = fstore.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("name", full_name);
                                    user.put("phone", phone_num);
                                    user.put("email", mail);
                                    user.put("pwd",password);
                                    user.put("veh_type",veh_type);
                                    user.put("veh_num",veh_num);

                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "SUCCESS! USER PROFILE IS CREATED FOR"+userID);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "FAILUrEEEEEEEEE"+e.toString());
                                        }
                                    });

                                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
//                                    finish();

                                }else{
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to register" + e.toString());
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
//                finish();
            }
        });




    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}
