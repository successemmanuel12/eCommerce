package com.success.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.success.ecommerce.model.Cart;
import com.success.ecommerce.prevalent.Prevalent;
import com.success.ecommerce.viewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProccesBtn;
    private TextView txtTotalamount, txtMsg1;
    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProccesBtn = findViewById(R.id.next_process_btn);
        txtTotalamount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);



        nextProccesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTotalamount.setText("Total Price = N"+ overTotalPrice);
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"), Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                holder.txtProductPrice.setText("Price = N"+model.getPrice());
                holder.txtProductName.setText(model.getPname());

                int oneTypeProductTprice = ((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTprice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence[] options = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               if (which==0){
                                   Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                   intent.putExtra("pid", model.getPid());
                                   startActivity(intent);
                               }

                               if (which == 1){
                                   cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                           .child("Products").child(model.getPid()).removeValue()
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()){
                                                       Toast.makeText(CartActivity.this, "Item Removed from Cart",Toast.LENGTH_SHORT).show();
                                                       startActivity(new Intent(CartActivity.this,HomeActivity.class));
                                                   }
                                               }
                                           });
                               }
                            }
                        });
                        builder.show();
                    }
                });

                txtTotalamount.setText("Total Price = N"+ overTotalPrice);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void checkOrderState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){
                        txtTotalamount.setText("Dear "+userName +"\n Your Order Has Been Successfully Shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, Your Final Order Has Been Shipped Successfully..Soon You Will Receive Your Order.");
                        nextProccesBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products, once you receive your first order",Toast.LENGTH_SHORT).show();
                    }
                    else if (shippingState.equals("not shipped")){
                        txtTotalamount.setText("Products Yet to be Shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, Your Final Order Has Been Placed Successfully..Soon it will be Verified and Shipped.");
                        nextProccesBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products, once you receive your first order",Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
