package com.success.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.success.ecommerce.prevalent.Prevalent;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNametxt, userPhoneTxt, addressTxt;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn;
    private Button securityQuestionBtn;
    private Uri imageUri;
    private String myUri = "";
    private StorageTask uplaodTask;
    private StorageReference storageProfilePicRefe;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePicRefe = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNametxt = findViewById(R.id.settings_full_name);
        userPhoneTxt = findViewById(R.id.settings_phone_number);
        addressTxt = findViewById(R.id.settings_address);
        securityQuestionBtn = findViewById(R.id.security_questions);

        profileChangeTextBtn = findViewById(R.id.profile_image_change);
        closeTextBtn = findViewById(R.id.close_settings_btn);
        saveTextBtn = findViewById(R.id.update_account_settings);

        userInfoDisplay(profileImageView,fullNametxt,userPhoneTxt,addressTxt);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }
                else {
                    updateOnlyUserInfo();
                }
            }
        });
        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this,"Error: Try Again",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void updateOnlyUserInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNametxt.getText().toString());
        userMap.put("address", addressTxt.getText().toString());
        userMap.put("phoneOrder", userPhoneTxt.getText().toString());
        databaseReference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

        Toast.makeText(SettingsActivity.this,"Profile Info Update Successful",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullNametxt.getText().toString())){
            Toast.makeText(this,"Name is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneTxt.getText().toString())){
            Toast.makeText(this,"phone number is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressTxt.getText().toString())){
            Toast.makeText(this,"Address is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Wait, We are Updating Your Account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri !=null){
            final  StorageReference fileRef = storageProfilePicRefe.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uplaodTask = fileRef.putFile(imageUri);

            uplaodTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNametxt.getText().toString());
                        userMap.put("address", addressTxt.getText().toString());
                        userMap.put("phoneOrder", userPhoneTxt.getText().toString());
                        userMap.put("image", myUri);
                        databaseReference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

                        Toast.makeText(SettingsActivity.this,"Profile Info Update Successful",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this,"Error Occurred",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this,"Image Not Selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNametxt, final EditText userPhoneTxt, final EditText addressTxt) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNametxt.setText(name);
                        userPhoneTxt.setText(phone);
                        addressTxt.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
