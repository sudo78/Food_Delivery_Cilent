package fastfood.foodapp.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Umer Sheraz on 3/14/2018.
 */

public class RetrofitCilent {
    private static Retrofit retrofit=null;

    public static Retrofit getClient(String BaseURL){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }
}
