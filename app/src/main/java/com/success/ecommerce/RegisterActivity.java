package com.success.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText inputName, inputPhoneNum, inputPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = findViewById(R.id.registe_btn);

        inputName = findViewById(R.id.register_username_input);
        inputPhoneNum = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String name = inputName.getText().toString();
        String phone = inputPhoneNum.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "please enter name...",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "please enter phone number...",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter password...",Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please Wait, while we are checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name,phone,password);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists())){

                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("name",name);
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);

                    rootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Congratulations your account has been created",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network Error, Please try again...after some time..",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "This number "+phone+ "already exist",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Try again using a different phone number",Toast.LENGTH_LONG).show();

                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
