package resaurentapp.pankaj.com.restaurentapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import resaurentapp.pankaj.com.restaurentapp.Interface.ItemClickListener;
import resaurentapp.pankaj.com.restaurentapp.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView food_image ;
    public TextView food_name;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener=itemClickListener;
    }

    public FoodViewHolder(View itemView){
        super(itemView);
        food_name=itemView.findViewById(R.id.food_name);
        food_image=itemView.findViewById(R.id.food_image);
        itemView.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
