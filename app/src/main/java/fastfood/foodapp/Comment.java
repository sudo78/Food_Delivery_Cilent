package fastfood.foodapp;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Model.Rating;
import fastfood.foodapp.VeiwHolder.ShowCommentViewHoldeer;

public class Comment extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference rating_table;
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHoldeer> adapter;

    String foodid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        recyclerView=(RecyclerView)findViewById(R.id.recyler_comment);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        database=FirebaseDatabase.getInstance();
        rating_table=database.getReference("Rating");

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refersh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent()!=null)
                    foodid=getIntent().getStringExtra(Common.intent_food_id);
                if(!foodid.isEmpty() && foodid!=null)
                {

                    Query query=rating_table.orderByChild("foodid").equalTo(foodid);

                    FirebaseRecyclerOptions<Rating> options=new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();
                    adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHoldeer>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHoldeer holder, int position, @NonNull Rating model) {
                            holder.user_comment.setText(model.getComment());
                            holder.user_rating.setRating(Float.parseFloat(model.getRatevalue()));
                            holder.user_phone.setText(model.getUserphone());

                        }

                        @Override
                        public ShowCommentViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                            return new ShowCommentViewHoldeer(view);
                        }
                    };

                    LoadComment(foodid);


                }



            }
        });

        // Thread to load commment to firsst lanuch
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if(getIntent()!=null)
                    foodid=getIntent().getStringExtra(Common.intent_food_id);
                if(!foodid.isEmpty() && foodid!=null)
                {

                    Query query=rating_table.orderByChild("foodid").equalTo(foodid);

                    FirebaseRecyclerOptions<Rating> options=new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();
                    adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHoldeer>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHoldeer holder, int position, @NonNull Rating model) {
                            holder.user_comment.setText(model.getComment());
                            holder.user_rating.setRating(Float.parseFloat(model.getRatevalue()));
                            holder.user_phone.setText(model.getUserphone());

                        }

                        @Override
                        public ShowCommentViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                            return new ShowCommentViewHoldeer(view);
                        }
                    };

                    LoadComment(foodid);


                }
            }
        });
    }

    private void LoadComment(String foodid) {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }


}
