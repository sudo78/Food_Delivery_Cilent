package fastfood.foodapp.VeiwHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.R;

/**
 * Created by Umer Sheraz on 2/17/2018.
 */

public class OrderViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener
        {

    public TextView order_id,order_status,order_phoneNumber,order_address;
    private ItemClickListenser itemClickListenser;

    public OrderViewHolder(View itemView) {
        super(itemView);
        order_id=(TextView)itemView.findViewById(R.id.order_id);
        order_status=(TextView)itemView.findViewById(R.id.order_status);
        order_phoneNumber=(TextView)itemView.findViewById(R.id.order_phoneNumber);
        order_address=(TextView)itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListenser(ItemClickListenser itemClickListenser) {
        this.itemClickListenser = itemClickListenser;
    }


    @Override
    public void onClick(View v) {
        itemClickListenser.onClick(v,getAdapterPosition(),false);

    }

};
