package com.success.ecommerce.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.success.ecommerce.HomeActivity;
import com.success.ecommerce.MainActivity;
import com.success.ecommerce.R;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView tShirts, sportTshirts, femaleDresses, sweaters;
    private ImageView glasses, hatsCpas, walletsBags, shoes;
    private ImageView  headphones, laptops, watches, phones;

    private Button logoutBtn, checkOrderBtn, maintainProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        logoutBtn = findViewById(R.id.admin_logout_btn);
        checkOrderBtn = findViewById(R.id.check_orders_btn);
        maintainProductsBtn = findViewById(R.id.maintain_btn);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        tShirts = findViewById(R.id.t_shirt);
        sportTshirts = findViewById(R.id.sports_t_shirt);
        femaleDresses = findViewById(R.id.female_dresses);
        sweaters = findViewById(R.id.sweaters);

        glasses = findViewById(R.id.glasses);
        hatsCpas = findViewById(R.id.hats_caps);
        walletsBags = findViewById(R.id.purses_bags_wallets);
        shoes = findViewById(R.id.shoes);

        headphones = findViewById(R.id.headphones_handfree);
        laptops = findViewById(R.id.laptop_pc);
        watches = findViewById(R.id.watches);
        phones = findViewById(R.id.mobiles);



        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","tShirts");
                startActivity(intent);
            }
        });

        sportTshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","sportsTshirts");
                startActivity(intent);
            }
        });

        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","femaleDresses");
                startActivity(intent);
            }
        });
        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","sweaters");
                startActivity(intent);
            }
        });

        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","glasses");
                startActivity(intent);
            }
        });

        hatsCpas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","hatsCaps");
                startActivity(intent);
            }
        });
        walletsBags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","walletsBags");
                startActivity(intent);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","shoes");
                startActivity(intent);
            }
        });
        headphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","headphones");
                startActivity(intent);
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","laptops");
                startActivity(intent);
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","watches");
                startActivity(intent);
            }
        });
        phones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","phones");
                startActivity(intent);
            }
        });
    }
}
