package com.success.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.success.ecommerce.prevalent.Prevalent;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check = "";
    private TextView pageTitle, titleQuestions;
    private EditText phoneNumber, question1, question2;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        pageTitle = findViewById(R.id.page_title);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_one);
        question2 = findViewById(R.id.question_two);
        verifyButton = findViewById(R.id.verify);
    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);
        if (check.equals("settings")){
            pageTitle.setText("Set Question");
            titleQuestions.setText("please set answers for the following security questions");
            verifyButton.setText("Update");

            displayPreviousAnswers();

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setAnswers();
                }
            });
        }
        else if (check.equals("login")){
            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    verifyUser();
                }
            });
        }

     }


    private void setAnswers(){
         String answer1 = question1.getText().toString().toLowerCase();
         String answer2 = question2.getText().toString().toLowerCase();

         if (question1.equals("") && question2.equals("")){
             Toast.makeText(ResetPasswordActivity.this, "Please answer both questions", Toast.LENGTH_SHORT).show();
         }
         else {
             DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                     .child(Prevalent.currentOnlineUser.getPhone());

             HashMap<String, Object> userDataMap = new HashMap<>();
             userDataMap.put("answer1",answer1);
             userDataMap.put("answer2",answer2);

             reference.child("Security Questions").updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){
                         Toast.makeText(ResetPasswordActivity.this, "Security questions successfully saved", Toast.LENGTH_SHORT).show();

                         startActivity(new Intent(ResetPasswordActivity.this, HomeActivity.class));
                     }
                 }
             });
         }
     }

     private void displayPreviousAnswers(){
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                 .child(Prevalent.currentOnlineUser.getPhone());

         reference.child("Security Questions").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){

                     String ans1 = dataSnapshot.child("answer1").getValue().toString();
                     String ans2 = dataSnapshot.child("answer2").getValue().toString();

                     question1.setText(ans1);
                     question2.setText(ans2);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

    private void verifyUser() {
        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")){

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(phone);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String mPhone = dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Security Questions"))
                        {
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this, "The First Answer is Incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else if (!ans2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this, "The Second Answer is Incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Enter New Password");
                                newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newPassword.getText().toString().equals("")){
                                            reference.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(ResetPasswordActivity.this,
                                                                    "password changed..", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                        else{
                            Toast.makeText(ResetPasswordActivity.this, "You are yet to set security questions", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ResetPasswordActivity.this, "Phone number does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(ResetPasswordActivity.this, "please complete the form", Toast.LENGTH_SHORT).show();
        }
    }
}
