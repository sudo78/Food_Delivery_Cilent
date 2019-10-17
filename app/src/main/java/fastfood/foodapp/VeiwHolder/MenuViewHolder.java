package fastfood.foodapp.VeiwHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.R;

/**
 * Created by Sheraz on 12/29/2017.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView menu_name;
    public ImageView menu_img;

    private ItemClickListenser itemClickListenser;

    public MenuViewHolder(View itemView) {
        super(itemView);
        menu_name=(TextView)  itemView.findViewById(R.id.menu_name);
        menu_img=(ImageView)  itemView.findViewById(R.id.menu_img);
        itemView.setOnClickListener(this);

    }
    public void setItemClickListenser(ItemClickListenser itemClickListenser) {
        this.itemClickListenser = itemClickListenser;
    }


    @Override
    public void onClick(View v) {
        itemClickListenser.onClick(v,getAdapterPosition(),false);



    }
}
