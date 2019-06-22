package com.success.ecommerce.admin;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.success.ecommerce.R;
import com.success.ecommerce.model.AdminOrders;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminNewOrdersActivity.AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminNewOrdersActivity.AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {
                holder.userName.setText("Name: " +model.getName());
                holder.userPhoneNumber.setText("Phone: " +model.getPhone());
                holder.userTotalPrice.setText("Total Amount: " +model.getTotalAmount());
                holder.userDateTime.setText("Order at: " +model.getDate()+" "+model.getTime());
                holder.userShipping.setText("Shipping Address: " +model.getAddress()+" " +model.getCity());

                holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uID =getRef(position).getKey();

                        Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence[] options = new CharSequence[]{
                                "Yes",
                                "No"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Have You Shipped This Order Products ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which ==0){
                                    String uID =getRef(position).getKey();

                                    removeOrder(uID);
                                }
                                else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminNewOrdersActivity.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout,viewGroup,false);
                return  new AdminNewOrdersActivity.AdminOrdersViewHolder(view);
            }
        };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends  RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShipping;
        private Button showOrdersBtn;


        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShipping = itemView.findViewById(R.id.order_address_city);
            showOrdersBtn = itemView.findViewById(R.id.show_all_product_btn);

        }
    }

    private void removeOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }

}
