package fastfood.foodapp.Model;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fastfood.foodapp.R;

/**
 * Created by Sheraz on 1/1/2018.
 */

public class CartAdaptor  extends RecyclerView.Adapter<CartViewHolder>{
    private List<Order> listData=new ArrayList<>();
    private Context context;


    public CartAdaptor(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View item_view=inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(item_view);

    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Picasso.with(context)
                .load(listData.get(position).getImage())
                .resize(70,70)
        .centerCrop()
        .into(holder.cart_image);


        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .fontSize(30) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(""+listData.get(position).getQuantity(), Color.BLACK);
        holder.cart_count.setImageDrawable(drawable);

        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        int price = -(Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.cart_price.setText(fmt.format(price));
        holder.cart_name.setText(listData.get(position).getProductName());


    }
    @Override
    public int getItemCount() {
        return listData.size();
    }
}
