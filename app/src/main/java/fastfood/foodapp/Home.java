package fastfood.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Database.Database;
import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.Model.Banner;
import fastfood.foodapp.Model.Category;
import fastfood.foodapp.Model.Token;
import fastfood.foodapp.Service.ListenOrder;
import fastfood.foodapp.VeiwHolder.MenuViewHolder;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase database;
    DatabaseReference catagory;
    TextView full_name;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    CounterFab fab;

    // Slider
    HashMap<String,String> image_list;
    SliderLayout sliderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
     //   setSupportActionBar(toolbar);
        // Init Firebase
        database=FirebaseDatabase.getInstance();
        catagory=database.getReference("Category");
           // Make sure the animation  is atart when database is instabcne
        Paper.init(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(Home.this,Cart.class));
            }
        });

        // set dynamic vlaue of fav counter
        fab.setCount(new Database(this).getcountcart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Set Name for user
        View heder_view=navigationView.getHeaderView(0);
        full_name=(TextView) heder_view.findViewById(R.id.full_name);
        full_name.setText(Common.current_user.getName());

        // Load Menu
        recycler_menu=(RecyclerView) findViewById(R.id.recycler_menu);

        // FOr Linera LAyoout
       // recycler_menu.setHasFixedSize(true);
        //layoutManager=new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);

        // For Grid Layout
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));
        if(Common.isConnectedToInternet(getBaseContext()))
            Load_Menu();
        else{
            Toast.makeText(Home.this, "Please check your connection  !!!", Toast.LENGTH_SHORT).show();
            return;
        }


    }


        private void Load_Menu() {


        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>()
                    .setQuery(catagory,Category.class)
                    .build();

        adapter= new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.menu_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.menu_img);
                viewHolder.setItemClickListenser(new ItemClickListenser() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryID and send to new Activity
                        Intent food_list=new Intent(Home.this,Food_List.class);
                        // Because CategoryID is a key,so we just get key  of this item
                        food_list.putExtra("CategoryID",adapter.getRef(position).getKey());
                        startActivity(food_list);
                    }
                });


            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View item_view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(item_view);
            }



        };
        adapter.startListening();
       recycler_menu.setAdapter(adapter);
       recycler_menu.getAdapter().notifyDataSetChanged();
       recycler_menu.scheduleLayoutAnimation();
       updateTokenToFIrebase(FirebaseInstanceId.getInstance().getToken());

            // Slider
           //setupslider();

    }

    /*private void setupslider() {
        sliderLayout=(SliderLayout)findViewById(R.id.slider);
        image_list=new HashMap<>();
        final DatabaseReference bannners=database.getReference("Banner");
        bannners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Banner banner=dataSnapshot.getValue(Banner.class);

                    image_list.put(banner.getName()+"_"+banner.getId(),banner.getImage());
                }
                for(String key:image_list.keySet()){
                    String[] keysplit=key.split("_");
                    String nameoffood=keysplit[0];
                    String idfood=keysplit[1];
                    // Creat silder
                    final TextSliderView textSliderView=new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameoffood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent=new Intent(Home.this,Food_Detail.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);


                                }
                            });
                    // Add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("foodid",idfood);
                    sliderLayout.addSlider(textSliderView);

                    // Remove Events after finsih
                    bannners.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);

    }*/



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh){
            Load_Menu();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getcountcart());
    }
    private void updateTokenToFIrebase(String tokenRefeshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(tokenRefeshed,false);  // false because this toke send from cilent app
        tokens.child(Common.current_user.getPhone()).setValue(data);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            startActivity(new Intent(Home.this,Home.class));

        }
        else if (id == R.id.nav_chart) {
            startActivity(new Intent(Home.this,Cart.class));

        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(Home.this,StatusOrder.class));

        } else if (id == R.id.nav_log_out) {
            // Delete Rememebr USser and passwords
            Paper.book().destroy();

            //Logout
            Intent signin=new Intent(Home.this,SignIn.class);
            signin.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK));
            startActivity(signin);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


