package com.success.ecommerce.admin;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.success.ecommerce.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private  String categoryName, description,price, pName, saveCurrentDate, saveCurrentTime;
    private Button addNewProductBtn;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private  StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().getString("category");
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);

        addNewProductBtn = findViewById(R.id.add_new_product);
        inputProductImage = findViewById(R.id.select_product_image);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);


        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });
    }

    private void validateProductData() {
        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();
        pName = inputProductName.getText().toString();


        if (imageUri == null){
            Toast.makeText(this, "Product Image is Mandatory..", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please Enter Description..", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Please Enter price..", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(pName)){
            Toast.makeText(this, "Please Enter Product Name..", Toast.LENGTH_LONG).show();
        }
        else{
            storeProductInfo();
        }

    }

    private void storeProductInfo() {

        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please Wait, while we are Adding The New Product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error"+message,Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image Uploaded Successfully",Toast.LENGTH_LONG).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "getting product image url successful",Toast.LENGTH_LONG).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);
        productMap.put("cateory",categoryName);
        productMap.put("price",price);
        productMap.put("pname",pName);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){

                          startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));

                          loadingBar.dismiss();
                          Toast.makeText(AdminAddNewProductActivity.this, "Product Successfully Added",Toast.LENGTH_LONG).show();
                      }
                      else {
                          loadingBar.dismiss();
                          String message = task.getException().toString();
                          Toast.makeText(AdminAddNewProductActivity.this, "Error"+message,Toast.LENGTH_LONG).show();
                      }
                    }
                });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== galleryPick && resultCode==RESULT_OK  && data!=null){
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }
}
