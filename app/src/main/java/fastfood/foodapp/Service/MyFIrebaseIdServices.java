package fastfood.foodapp.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Model.Token;

/**
 * Created by Umer Sheraz on 3/14/2018.
 */

public class MyFIrebaseIdServices extends FirebaseInstanceIdService {
    String tokenRefeshed;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        tokenRefeshed=FirebaseInstanceId.getInstance().getToken();
        if(Common.current_user!=null)
            updateTokenToFIrebase(tokenRefeshed);
    }

    private void updateTokenToFIrebase(String tokenRefeshed ) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(tokenRefeshed,false);  // false because this toke send from cilent app
        tokens.child(Common.current_user.getPhone()).setValue(data);



    }
}
