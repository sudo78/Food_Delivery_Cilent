package fastfood.foodapp.VeiwHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import fastfood.foodapp.R;

/**
 * Created by Umer Sheraz on 3/12/2018.
 */

public class ShowCommentViewHoldeer extends RecyclerView.ViewHolder {

    public TextView user_phone,user_comment;
    public RatingBar user_rating;

    public ShowCommentViewHoldeer(View itemView) {
        super(itemView);
        user_phone=(TextView)itemView.findViewById(R.id.user_phone);
        user_comment=(TextView)itemView.findViewById(R.id.user_Commment);
        user_rating=(RatingBar)itemView.findViewById(R.id.rating_user);
    }
}
