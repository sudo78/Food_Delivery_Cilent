package fastfood.foodapp.Model;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fastfood.foodapp.Common.Common;
import fastfood.foodapp.Interface.ItemClickListenser;
import fastfood.foodapp.R;

/**
 * Created by Sheraz on 1/1/2018.
 */

 public  class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView cart_name,cart_price;
    public ImageView cart_count;
    public ImageView cart_image;

    private ItemClickListenser itemClickListener;

    public void setTxt_chartName(TextView cart_name){
        this.cart_name=cart_name;
    }


    public CartViewHolder(View itemView) {
        super(itemView);
        cart_name=(TextView)  itemView.findViewById(R.id.chart_item_name);
        cart_price=(TextView)  itemView.findViewById(R.id.chart_item_price);
        cart_count=(ImageView)  itemView.findViewById(R.id.chart_iten_count);
        cart_image=(ImageView)  itemView.findViewById(R.id.cart_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);

    }
}
