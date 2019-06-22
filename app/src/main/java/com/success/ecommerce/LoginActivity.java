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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.success.ecommerce.admin.AdminCategoryActivity;
import com.success.ecommerce.model.Users;
import com.success.ecommerce.prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText inputPhoneNumber, inputPassword;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;
    private TextView adminLink, notAdminLink, forgetPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_btn);
        inputPhoneNumber = findViewById(R.id.login_phone_number_input);
        inputPassword = findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = findViewById(R.id.remember_me_chk);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
        Paper.init(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });

    }

    private void LoginUser() {
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please Enter a Valid Phone Number",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Password",Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please Wait, while we are checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(phone,password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)){
                        if (usersData.getPassword().equals(password)){
                           if (parentDbName.equals("Admins")){
                               Toast.makeText(LoginActivity.this, "Welcome Admin you are Logged in Successfully",Toast.LENGTH_LONG).show();
                               loadingBar.dismiss();
                               startActivity(new Intent(LoginActivity.this, AdminCategoryActivity.class));
                           }else if (parentDbName.equals("Users")){
                               Toast.makeText(LoginActivity.this, "Login Successful",Toast.LENGTH_LONG).show();
                               loadingBar.dismiss();

                               Prevalent.currentOnlineUser = usersData;
                               startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                           }
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Invalid Phone Number Or Password. Please try again",Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Account With this Credentials does not exist. Please try again",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
