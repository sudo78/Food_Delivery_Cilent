package fastfood.foodapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.FacebookSdk;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Model.User;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button BtnSignUp,BtnSignIn;
    TextView txt_Slogon;
    CheckBox check_remeber;
    MaterialEditText phone_number,pwd;
    DatabaseReference table_user;
    FirebaseDatabase  database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        phone_number=(MaterialEditText) this.findViewById(R.id.phone_number);
        pwd=(MaterialEditText) this.findViewById(R.id.pwd);
       BtnSignUp=(Button) this.findViewById(R.id.BtnSignUp);
       BtnSignIn=(Button) this.findViewById(R.id.BtnSignIn);
        txt_Slogon=(TextView) findViewById(R.id.txt_Slogon);

       Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Arkhip_font.ttf");
         txt_Slogon.setTypeface(font);


         ///apper ini
        Paper.init(this);

        // Firebase Code
        database=FirebaseDatabase.getInstance();
        //Select Table
        table_user=database.getReference("User");


        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUp.class));



            }
        });

        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignIn.class));

            }
        });

        // User Check
        String phone=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);
        if(phone!=null && pwd!=null){
            if(!phone.isEmpty()&&!pwd.isEmpty()){
                login(phone,pwd);

            }

        }

        getHashKey();


    }

    private void getHashKey() {
        try{
            PackageInfo info=getPackageManager().getPackageInfo("fastfood.foodapp",
                    PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md=MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));



            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String pwd) {
        // Just Copy Login Code FRom SIGnIN.class
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Please Waiting.....");

        dialog.show();
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check IF user is not  exixts in database
                if (dataSnapshot.child(phone).exists()) {

                    // GEt USER INFMORATION
                    dialog.dismiss();
                    User user = dataSnapshot.child(phone).getValue(User.class);
                    user.setPhone(phone);  // Set phone
                    if (user.getPassword().equals(pwd)) {

                        Intent intent = new Intent(MainActivity.this, Home.class);
                        Common.current_user = user;
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(MainActivity.this, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "USER NOT EXIST IN DATABASE", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }

    @Override
    public void onBackPressed() {
       confrim_back();

    }

    private void confrim_back() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you want to close this  app");
        builder.setCancelable(false);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
