package fastfood.foodapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.File;
import java.util.Arrays;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Database.Database;
import fastfood.foodapp.Model.Food;
import fastfood.foodapp.Model.Order;
import fastfood.foodapp.Model.Rating;


public class Food_Detail extends AppCompatActivity  implements RatingDialogListener{
    TextView food_name,food_price,food_des;
    ImageView food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btn_Chart;
    ElegantNumberButton numberButton;
    Button show_comment;
    String food_id="";

    FirebaseDatabase database;
    DatabaseReference food;
    DatabaseReference ratingtable;
    Food current_food;
    RatingBar ratingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__detail);

        database=FirebaseDatabase.getInstance();
        food=database.getReference("Food");
        ratingtable=database.getReference("Rating");
        numberButton=(ElegantNumberButton) findViewById(R.id.number_button);
        btn_Chart=(FloatingActionButton) this.findViewById(R.id.btn_chart);
        ratingbar=(RatingBar)findViewById(R.id.ratingbar);
        show_comment=(Button)findViewById(R.id.show_comment);


        food_des=(TextView) this.findViewById(R.id.food_des);
        food_name=(TextView) this.findViewById(R.id.food_name);
        food_price=(TextView) this.findViewById(R.id.food_price);
        food_img=(ImageView) this.findViewById(R.id.img_food);

       collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collaping);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        // Get Food Id  from intent
        if(getIntent()!=null)
            food_id=getIntent().getStringExtra("FoodId");
        if(!food_id.isEmpty()){
            getDetailFood(food_id);
            getRatingFood(food_id);
        }

        btn_Chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToChart(new Order(
                        food_id,
                        current_food.getName(),
                        numberButton.getNumber(),
                        current_food.getPrice(),
                        current_food.getDiscount(),
                        current_food.getImage()



                ));
                Toast.makeText(Food_Detail.this, "Add To Cart", Toast.LENGTH_SHORT).show();

            }
        });

        show_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Food_Detail.this,Comment.class);
                intent.putExtra(Common.intent_food_id,food_id);
                startActivity(intent);
            }
        });








    }

    private void getRatingFood(String food_id) {
        Query foodrating=ratingtable.orderByChild("foodid").equalTo(food_id);
        foodrating.addValueEventListener(new ValueEventListener() {
            int count=0, sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Rating item=postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRatevalue());
                    count++;
                }
                if(count!=0) {
                    float avergae = sum / count;
                    ratingbar.setRating(avergae);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("databaseError",""+databaseError.getMessage());


            }
        });
    }

    private void showratingdialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate This Food")
                .setDescription("Please select some start and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setNoteDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please Write your comments here....")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(Food_Detail.this)
                .show();
    }

    private void getDetailFood(final String food_id) {
        food.child(food_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_food=dataSnapshot.getValue(Food.class);

                // Set Image
                Picasso.with(getBaseContext()).load(current_food.getImage())
                        .into(food_img);
                collapsingToolbarLayout.setTitle(current_food.getName());
                food_price.setText(current_food.getPrice());
                food_name.setText(current_food.getName());
                food_des.setText(current_food.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comment) {
        // Geting arting and upload to firebase
        final Rating rating=new Rating(Common.current_user.getPhone()
                ,food_id
                ,String.valueOf(value),
                comment);


        // Fixes bugs you we one user can comment multiple item
        ratingtable.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Food_Detail.this, "Comment Submit !!!", Toast.LENGTH_SHORT).show();

                    }
                });
        /*ratingtable.child(Common.current_user.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.current_user.getPhone()).exists()){

                    // update new value
                    ratingtable.child(Common.current_user.getPhone()).setValue(rating);


                }
                else
                {
                    // update new value
                    ratingtable.child(Common.current_user.getPhone()).setValue(rating);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    public void ratingdailog(View view) {
        showratingdialog();

    }

    public void whatapp(View view) {
        /*Uri imageUri = Uri.parse(pictureFile.getAbsolutePath());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        //Add text and then Image URI
        //shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
           // ToastHelper.MakeShortText("Whatsapp have not been installed.");
        }*/

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
       // shareIntent.putExtra(Intent.EXTRA_TEXT,title + "\n\nLink : " + link );
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(food_img.toString()));
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share image via:"));
    }
}
