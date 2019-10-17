package fastfood.foodapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Model.User;
import io.paperdb.Book;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    MaterialEditText phone_number,pwd;
    Button BtnSignIn;
    RelativeLayout root;
    CheckBox check_remeber;
    TextView foregt_passowrd;
;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        root=(RelativeLayout)this.findViewById(R.id.root);
        phone_number=(MaterialEditText) this.findViewById(R.id.phone_number);
        pwd=(MaterialEditText) this.findViewById(R.id.pwd);
        BtnSignIn=(Button) this.findViewById(R.id.BtnSignIn);
        check_remeber=(CheckBox)findViewById(R.id.check_remeber);
        foregt_passowrd=(TextView)findViewById(R.id.foregt_passowrd);

        // INi Paper
        Paper.init(this);
          // Firebase Code
        final FirebaseDatabase  database=FirebaseDatabase.getInstance();
        //Select Table
        final DatabaseReference table_user=database.getReference("User");
        foregt_passowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_forgoet_dialog();
            }
        });

        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty( phone_number.getText().toString()))
                {
                    Snackbar.make(root,"Enter Phone Number",
                            Snackbar.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty(pwd.getText().toString()))
                {
                    Snackbar.make(root,"Enter Passwords",
                            Snackbar.LENGTH_SHORT).show();

                }
                else
                {
                    // Saving  User and Passwords
                    if(check_remeber.isChecked()){
                        Paper.book().write(Common.USER_KEY, phone_number.getText().toString());
                        Paper.book().write(Common.PWD_KEY, pwd.getText().toString());

                    }
                    final ProgressDialog dialog = new ProgressDialog(SignIn.this);
                    dialog.setMessage("Please Waiting.....");

                    dialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Check IF user is not  exixts in database
                            if (dataSnapshot.child(phone_number.getText().toString()).exists()) {

                                // GEt USER INFMORATION
                                dialog.dismiss();

                                User user = dataSnapshot.child(phone_number.getText().toString()).getValue(User.class);

                                 user.setPhone(phone_number.getText().toString());
                                if (user.getPassword().equals(pwd.getText().toString())) {
                                    Intent intent = new Intent(SignIn.this, Home.class);
                                    Common.current_user = user;
                                    startActivity(intent);
                                    finish();
                                } else
                                    Toast.makeText(SignIn.this, "WRONG PASSWORD", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(SignIn.this, "USER NOT EXIST IN DATABASE", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });
                }

            }
        });


    }

    private void show_forgoet_dialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forget Password");
        builder.setIcon(R.drawable.ic_security_black_24dp);
        builder.setMessage("Enter Your Secure Code");
        LayoutInflater inflater=this.getLayoutInflater();
        View forget_view=inflater.inflate(R.layout.forget_password_layout,null);
        builder.setView(forget_view);
        final MaterialEditText phone=(MaterialEditText)forget_view.findViewById(R.id.phone_number);
        final MaterialEditText secure_code=(MaterialEditText)forget_view.findViewById(R.id.securecode);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase database=FirebaseDatabase.getInstance();
                DatabaseReference table_user=database.getReference("User");
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!phone.getText().toString().isEmpty() &&!secure_code.getText().toString().isEmpty()) {
                            if (dataSnapshot.child(phone.getText().toString()).exists()) {
                                User user = dataSnapshot.child(phone.getText().toString())
                                        .getValue(User.class);
                                if (user.getSecurecode().equals(secure_code.getText().toString()))
                                    Toast.makeText(SignIn.this, "Your Password: " + user.getPassword(), Toast.LENGTH_LONG).show();
                                else {
                                    Toast.makeText(SignIn.this, "Wrong Secure COde", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Toast.makeText(SignIn.this, "User Not Exits", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Snackbar.make(root,"All Feild Required",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
}
