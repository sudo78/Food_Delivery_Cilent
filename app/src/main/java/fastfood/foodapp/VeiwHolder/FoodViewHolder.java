package fastfood.foodapp.VeiwHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.R;

/**
 * Created by Sheraz on 12/30/2017.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public ImageView food_img;
    public ImageView fav_img;
    public ImageView share_btn;
    public TextView price;

    public void setItemClickListenser(ItemClickListenser itemClickListenser) {
        this.itemClickListenser = itemClickListenser;
    }

    private ItemClickListenser itemClickListenser;
    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name=(TextView)  itemView.findViewById(R.id.food_name);
        food_img=(ImageView)  itemView.findViewById(R.id.food_img);
        fav_img=(ImageView)  itemView.findViewById(R.id.fav);
        share_btn=(ImageView)  itemView.findViewById(R.id.share_btn);
        price=(TextView)itemView.findViewById(R.id.price);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListenser.onClick(v,getAdapterPosition(),false);


    }
}
