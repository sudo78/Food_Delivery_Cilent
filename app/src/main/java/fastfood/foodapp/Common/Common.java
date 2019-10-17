package fastfood.foodapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import fastfood.foodapp.Model.User;
import fastfood.foodapp.Remote.APIServices;
import fastfood.foodapp.Remote.RetrofitCilent;

/**
 * Created by Sheraz on 12/29/2017.
 */

public class Common {
    public static User current_user;

    public static  final  String intent_food_id="foodid";

    public  static final  String DELETE="Delete";
    public  static final  String USER_KEY="USer";
    public  static final  String PWD_KEY="Password";
    public static final String UPDATE = "Update";

    public static String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "on my way";
        else
            return "Shipped";

    }
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    private static  final String googleapiurl="https://maps.googleapis.com";
    private static  final String BASE_URL="https://fcm.googleapis.com";

    public static APIServices getFCMServices(){
        return RetrofitCilent.getClient(BASE_URL).create(APIServices.class);
    }
    /*public static IGoogleApi getGoogleAPI(){
        return GoogleMapAPI.getClient(googleapiurl).create(IGoogleApi.class);
    }*/


}
