package fastfood.foodapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Database.Database;
import fastfood.foodapp.Model.CartAdaptor;
import fastfood.foodapp.Model.MyRespone;
import fastfood.foodapp.Model.Notification;
import fastfood.foodapp.Model.Order;
import fastfood.foodapp.Model.Request;
import fastfood.foodapp.Model.Sender;
import fastfood.foodapp.Model.Token;
import fastfood.foodapp.Remote.APIServices;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView total_price;
    FButton btn_place;

    List<Order> cart=new ArrayList<>();
    CartAdaptor cartAdaptor;

    APIServices mservices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // init servies
        mservices=Common.getFCMServices();
        //firebase
          database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        // Init
        recyclerView=(RecyclerView) findViewById(R.id.list_chart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        total_price=(TextView) findViewById(R.id.total_price) ;
        btn_place=(FButton) findViewById(R.id.place_order);
        btn_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size()>0)
                     alert_dailog();
                else {
                    Toast.makeText(Cart.this, "Your Cart iS Empty", Toast.LENGTH_SHORT).show();
                    String order_number=String.valueOf(System.currentTimeMillis());
                    Log.d("abc",order_number);
                }

            }
        });

        LoadListFood();


    }

    private void alert_dailog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Cart.this);
        builder.setTitle("One More Step....");
        builder.setMessage("Enter Your Address:..");
        LayoutInflater inflater=this.getLayoutInflater();
        View order_address_comment=inflater.inflate(R.layout.order_address_comments,null);
        final MaterialEditText edt_address=(MaterialEditText)order_address_comment.findViewById(R.id.address);
        final MaterialEditText comment=(MaterialEditText)order_address_comment.findViewById(R.id.comment);

        builder.setView(order_address_comment);
        builder.setIcon(R.drawable.cart);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request=new Request(
                        Common.current_user.getPhone(),
                        Common.current_user.getName(),
                        edt_address.getText().toString(),
                        total_price.getText().toString(),
                        "0",
                        comment.getText().toString(),
                        cart
                );
                // Submit  To firebase
                // We will  susing System.CurrentMilli to key
                String order_number=String.valueOf(System.currentTimeMillis());

               requests.child(order_number)
                        .setValue(request);
               // requests.push().setValue(request);
                //Delete Cart
                new Database(getBaseContext()).cleanchart();
                // Send Notifidcation
                send_notifictaion(order_number);



            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();



    }

    private void send_notifictaion(final String order_number) {

        Toast.makeText(this, "send_notifictaion", Toast.LENGTH_SHORT).show();
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=tokens.orderByChild("isServerToken").equalTo(true); // get all node with isServerToken is true;
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Toast.makeText(Cart.this, "postSnapShot", Toast.LENGTH_SHORT).show();
                    Token server_token=postSnapShot.getValue(Token.class);

                    // Careat raw payload to send
                    Notification notification=new Notification("Umer Sheraz","You Have new Order"+order_number);
                    Sender content=new Sender(server_token.getToken(),notification);
                    mservices.send_notfiaction(content)
                            .enqueue(new Callback<MyRespone>() {
                                @Override
                                public void onResponse(Call<MyRespone> call, Response<MyRespone> response) {
                                    if(response.code()==200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank You , Order Place", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Failed !!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyRespone> call, Throwable t) {

                                    Log.d("Error",t.getMessage());

                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void LoadListFood() {
        cart=new Database(this).get_charts();
        cartAdaptor=new CartAdaptor(cart,this);
        cartAdaptor.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdaptor);

        // Cal Total Price
         int total=0;
         for(Order order:cart)
             total+=(Integer.parseInt(order.getPrice()))*
                     (Integer.parseInt(order.getQuantity()));
         Locale locale=new Locale("en","US");
         NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
         total_price.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))

            deleteCart(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteCart(int order) {

        // We Will remove item ar List<Order> by position
        cart.remove(order);
        // After that we will delete all data from sqlite
        new Database(this).cleanchart();
        // Add final,we  willl update data from lIst<order> to sliqte
        for(Order item:cart)
            new Database(this).addToChart(item);
        //Refresh
        LoadListFood();
    }
}
