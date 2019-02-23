package resaurentapp.pankaj.com.restaurentapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import resaurentapp.pankaj.com.restaurentapp.Interface.ItemClickListener;
import resaurentapp.pankaj.com.restaurentapp.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView food_image,fav_image,shareImage,quick_cart ;
    public TextView food_name,food_price;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener=itemClickListener;
    }

    public FoodViewHolder(View itemView){
        super(itemView);
        food_name=itemView.findViewById(R.id.food_name);
      food_image=itemView.findViewById(R.id.food_image);
 fav_image=itemView.findViewById(R.id.fav);
        shareImage=itemView.findViewById(R.id.btnShare);
        food_price=itemView.findViewById(R.id.food_price);
     quick_cart=itemView.findViewById(R.id.btnquickCart);

        itemView.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
