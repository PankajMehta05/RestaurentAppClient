package resaurentapp.pankaj.com.restaurentapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import resaurentapp.pankaj.com.restaurentapp.Interface.ItemClickListener;
import resaurentapp.pankaj.com.restaurentapp.R;


public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    public ImageView imageView;
     public TextView txtMenuName;
private ItemClickListener itemClickListener;
    @Override

    public void onClick(View view) {
    itemClickListener.onClick(view,getAdapterPosition(),false);
    }
    public MenuViewHolder(View itemView)
    {
        super(itemView);
        txtMenuName=itemView.findViewById(R.id.menu_name);
        imageView=itemView.findViewById(R.id.menu_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
       this.itemClickListener=itemClickListener;
    }
}
