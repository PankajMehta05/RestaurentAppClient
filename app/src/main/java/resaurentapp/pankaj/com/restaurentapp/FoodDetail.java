package resaurentapp.pankaj.com.restaurentapp;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import resaurentapp.pankaj.com.restaurentapp.Database.Database;
import resaurentapp.pankaj.com.restaurentapp.Model.Food;
import resaurentapp.pankaj.com.restaurentapp.Model.Order;

public class FoodDetail extends AppCompatActivity {
 TextView   food_name,food_price,food_description;
 ImageView food_image;
 CollapsingToolbarLayout collapsingToolbarLayout;
 FloatingActionButton btnCart;
 ElegantNumberButton numberButton;
 String foodId="";
 FirebaseDatabase database;
 DatabaseReference foods;
 Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
//Firebase

           database=FirebaseDatabase.getInstance();
          foods=database.getReference("Foods");

//Init View
btnCart=findViewById(R.id.btnCart);


btnCart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
         new Database(getBaseContext()).addToCart(new Order(foodId,currentFood.getName(),numberButton.getNumber(),currentFood.getPrice(),currentFood.getDiscount()));
        Toast.makeText(FoodDetail.this,"Added to cart",Toast.LENGTH_SHORT).show();
    }
});
        numberButton=findViewById(R.id.number_button);
        btnCart=findViewById(R.id.btnCart);

        food_description=findViewById(R.id.food_description);
        food_name=findViewById(R.id.food_name);
        food_price=findViewById(R.id.food_price);
        food_image=findViewById(R.id.img_food);
        collapsingToolbarLayout=findViewById(R.id.collapsing);
collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppbar);
collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //get food id from Intent
        if(getIntent()!=null)
        {
            foodId=getIntent().getStringExtra("FoodId");
            if(!foodId.isEmpty())
            {
                getDetailFood(foodId);
            }
        }

    }
    private void getDetailFood(String foodId)
    {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood =dataSnapshot.getValue(Food.class);


                //Set Image
                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText((currentFood.getDescription()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
