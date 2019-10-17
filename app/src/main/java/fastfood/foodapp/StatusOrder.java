package fastfood.foodapp;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.Model.Food;
import fastfood.foodapp.Model.Request;
import fastfood.foodapp.R;
import fastfood.foodapp.VeiwHolder.FoodViewHolder;
import fastfood.foodapp.VeiwHolder.OrderViewHolder;

public class StatusOrder extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adaptoer;

    FirebaseDatabase db;
    DatabaseReference request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_order);
        db=FirebaseDatabase.getInstance();
        request=db.getReference("Requests");

        //INin
        recyclerView=(RecyclerView)findViewById(R.id.list_order);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // if we start order stataus activity from home activity
        // we will not put any extra , so we just loadorder by phone from COmmmon
        if(getIntent()==null) {
            LoadOrders(Common.current_user.getPhone());// Load All Orders
        }
        else{
            LoadOrders(getIntent().getStringExtra("userphone"));
        }


    }

    private void LoadOrders(String phone) {

        Query get_order_user=request.orderByChild("Phone").equalTo(phone);
        FirebaseRecyclerOptions<Request> order_option=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(get_order_user,Request.class)
                .build();


        adaptoer=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(order_option) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.order_id.setText(adaptoer.getRef(position).getKey());
                viewHolder.order_status.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.order_address.setText(model.getAddress());
                viewHolder.order_phoneNumber.setText(model.getPhone());
                viewHolder.setItemClickListenser(new ItemClickListenser() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Jsut Implemnet it to fix Crash when click on item
                    }
                });

            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };
        adaptoer.startListening();
        adaptoer.notifyDataSetChanged();
        recyclerView.setAdapter(adaptoer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptoer.stopListening();
    }
}
