package fastfood.foodapp.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Model.Request;
import fastfood.foodapp.R;
import fastfood.foodapp.StatusOrder;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase db;
    DatabaseReference requests;
    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db=FirebaseDatabase.getInstance();
        requests=db.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        //Trigger Here
        Request request=dataSnapshot.getValue(Request.class);
        ShowNotofication(dataSnapshot.getKey(),request);

    }

    private void ShowNotofication(String key, Request request) {
        Intent intent=new Intent(getBaseContext(), StatusOrder.class);
        intent.putExtra("userphone",request.getPhone());// We need put user phone, why i tal later
        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Umer Sheraz")
                .setContentInfo("Your order was updated")
                .setContentText("Order  #"+key+" was update to "+ Common.convertCodeToStatus(request.getStatus()))
                .setContentInfo("Info")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager notificationManager=(NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int randomint=new Random().nextInt(999-1)+1;
        notificationManager.notify(randomint,builder.build());

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
