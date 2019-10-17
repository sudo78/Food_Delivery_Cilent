package fastfood.foodapp;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import fastfood.foodapp.Model.User;
import info.hoang8f.widget.FButton;

public class SignUp extends AppCompatActivity {
    MaterialEditText phone_number,pwd,name,securecode;
    FButton BtnSignUp;
    RelativeLayout root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        phone_number=(MaterialEditText) this.findViewById(R.id.phone_number);
        pwd=(MaterialEditText) this.findViewById(R.id.pwd);
        name=(MaterialEditText) this.findViewById(R.id.name);
        securecode=(MaterialEditText) this.findViewById(R.id.securecode);
        root=(RelativeLayout) findViewById(R.id.root);

        BtnSignUp=(FButton) this.findViewById(R.id.BtnSignUp);

        // Firebase Code
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty( phone_number.getText().toString()))
                {
                    Snackbar.make(root,"Enter Phone Number",
                            Snackbar.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty(name.getText().toString()))
                {
                    Snackbar.make(root,"Enter Name",
                            Snackbar.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty( pwd.getText().toString()))
                {
                    Snackbar.make(root,"Enter password",
                            Snackbar.LENGTH_SHORT).show();

                }
                else {

                    final ProgressDialog dialog = new ProgressDialog(SignUp.this);
                    dialog.setMessage("Please Waiting.....");
                    dialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Check if ALready Phone Number is Database
                            if (dataSnapshot.child(phone_number.getText().toString()).exists()) {
                                dialog.dismiss();
                                Toast.makeText(SignUp.this, "PHONE NUMBER ALREDAY REGISTED", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                User user = new User(name.getText().toString(), pwd.getText().toString(),securecode.getText().toString());
                                table_user.child(phone_number.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "SIGN IN SUCCESSFULLY!!!", Toast.LENGTH_SHORT).show();
                               finish();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(SignUp.this, "Some Went Wrong !!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }


            }
        });

    }
}
