package fastfood.foodapp.Remote;



import fastfood.foodapp.Model.MyRespone;
import fastfood.foodapp.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Umer Sheraz on 3/14/2018.
 */

public interface APIServices {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAsbYX1BY:APA91bGePAl6D7bCSKkIdRzuPdJN0HBrX30EWxXLOrdVLg1dsdQkhuBbpk5qEBFhvSVt-YjfOnYTF7qnUpGzKoYR82e93tUMTQ6JMhUmosN41AjIY7ni_iuYu1-0abJz6vD2PS36gGDk"
    })
    @POST("fcm/send")
    Call<MyRespone> send_notfiaction(@Body Sender body);

}
