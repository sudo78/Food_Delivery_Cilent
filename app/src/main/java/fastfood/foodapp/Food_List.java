package fastfood.foodapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import fastfood.foodapp.Database.Database;
import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.Model.Category;
import fastfood.foodapp.Model.Food;
import fastfood.foodapp.VeiwHolder.FoodViewHolder;

public class Food_List extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference food_list;
    String catoegoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    // Search Functionality
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdaptor;
    List<String> suggest_list=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDb;

    // faceBook SHare
    CallbackManager callbackManager;
     ShareDialog shareDialog;

    // Careat Target from Picaso
    com.squareup.picasso.Target target=new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // Creat photo from  bitmap
            SharePhoto sharePhoto=new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content=new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);

            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__list);


        // init fccebook
       callbackManager=CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);



        //firebase
        database=FirebaseDatabase.getInstance();
        food_list=database.getReference("Food");

        recyclerView=(RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Local DB
        localDb=new Database(this);


        //Get Intent Here
        if(getIntent()!=null)
            catoegoryId=getIntent().getStringExtra("CategoryID");
        if(!catoegoryId.isEmpty()&& catoegoryId!=null){
            LoadListFood(catoegoryId);
        }
        // Search
        materialSearchBar=(MaterialSearchBar) findViewById(R.id.serach_bar);
        materialSearchBar.setHint("Enter Your Food");
        // materialSearchBar.setSpeechMode(false);
        //No need,because we alreday define it on xml
        LoadSuggest();
        materialSearchBar.setLastSuggestions(suggest_list);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // When  suer type their text, we will  change suggest list
                List<String> suggest=new ArrayList<>();
                for(String search:suggest_list) // Loop  in suggest list
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // When Serach Bar is close
                //Restore original  adaptor
                if(!enabled)
                    recyclerView.setAdapter(adapter);


            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // When Search Complete
                // Show Result  of seaech adaptor
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });



    }

    private void startSearch(CharSequence text) {
        Query serch_query=food_list.orderByChild("name").equalTo(text.toString());
        // Creat option with query

        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(serch_query,Food.class)
                .build();
        searchAdaptor=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_img);

                final Food local=model;
                viewHolder.setItemClickListenser(new ItemClickListenser() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(Food_List.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent food_detail=new Intent(Food_List.this,Food_Detail.class);
                        food_detail.putExtra("FoodId",searchAdaptor.getRef(position).getKey());
                        startActivity(food_detail);


                    }
                });

            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
        };
        searchAdaptor.startListening();
        recyclerView.setAdapter(searchAdaptor);
    }
    private void LoadSuggest() {
        food_list.orderByChild("menuId").equalTo(catoegoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postsnapshot:dataSnapshot.getChildren()){
                    Food item =postsnapshot.getValue(Food.class);
                    suggest_list.add(item.getName());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void LoadListFood(String catoegoryId) {
        Query serach_by_name=food_list.orderByChild("menuId").equalTo(catoegoryId);

        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(serach_by_name,Food.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                viewHolder.food_name.setText(model.getName());
                //viewHolder.price.setText(String.format("$ %s",model.getPrice().toString()));
                viewHolder.price.setText(" $"+model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_img);

                // Add to fav
                if(localDb.isFavourite(adapter.getRef(position).getKey()))
                    viewHolder.fav_img.setImageResource(R.drawable.fav);

                viewHolder.share_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Food_List.this, "dd", Toast.LENGTH_SHORT).show();
                        Picasso.with(getBaseContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });
                viewHolder.fav_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDb.isFavourite(adapter.getRef(position).getKey())){
                            localDb.addFavourite(adapter.getRef(position).getKey());
                            viewHolder.fav_img.setImageResource(R.drawable.fav);
                            Toast.makeText(Food_List.this, ""+model.getName()+" was added to Favourites", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            localDb.removeFavourite(adapter.getRef(position).getKey());
                            viewHolder.fav_img.setImageResource(R.drawable.unfav);
                            Toast.makeText(Food_List.this, ""+model.getName()+" was remove from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local=model;
                viewHolder.setItemClickListenser(new ItemClickListenser() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(Food_List.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent food_detail=new Intent(Food_List.this,Food_Detail.class);
                        food_detail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(food_detail);


                    }
                });


            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(view);
            }
        };
        // Set Adaptor
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
       // adapter.stopListening();

    }
}
