package resaurentapp.pankaj.com.restaurentapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import info.hoang8f.widget.FButton;
import resaurentapp.pankaj.com.restaurentapp.Common.Common;
import resaurentapp.pankaj.com.restaurentapp.Database.Database;
import resaurentapp.pankaj.com.restaurentapp.Model.Food;
import resaurentapp.pankaj.com.restaurentapp.Model.Order;
import resaurentapp.pankaj.com.restaurentapp.Model.Rating;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener{
 TextView   food_name,food_price,food_description;
 ImageView food_image;
 CollapsingToolbarLayout collapsingToolbarLayout;
 FloatingActionButton btnRtaing;
 CounterFab btnCart;
 ElegantNumberButton numberButton;
 String foodId="";
 FirebaseDatabase database;
 DatabaseReference foods;
 Food currentFood;
 RatingBar ratingBar;
 DatabaseReference ratingTbl;


 FButton btnShowComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf").
                        setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_food_detail);
        btnShowComment=findViewById(R.id.btnShowComment);
        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FoodDetail.this,ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);

            }
        });

//Firebase

           database=FirebaseDatabase.getInstance();
          foods=database.getReference("Foods");

//Init View
btnCart=findViewById(R.id.btnCart);


btnCart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
         new Database(getBaseContext()).addToCart(new Order(foodId,currentFood.getName(),numberButton.getNumber(),currentFood.getPrice(),currentFood.getDiscount()
         ,currentFood.getImage()));
        Toast.makeText(FoodDetail.this,"Added to cart",Toast.LENGTH_SHORT).show();

    }
});
btnCart.setCount((new Database(this).getCountCart()));
        numberButton=findViewById(R.id.number_button);
        btnCart=findViewById(R.id.btnCart);

        food_description=findViewById(R.id.food_description);
        food_name=findViewById(R.id.food_name);
        food_price=findViewById(R.id.food_price);
        food_image=findViewById(R.id.img_food);
        collapsingToolbarLayout=findViewById(R.id.collapsing);
        ratingTbl=database.getReference("Rating");

collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppbar);
collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
ratingBar=findViewById(R.id.ratingBar);
btnRtaing=findViewById(R.id.btn_rating);


btnRtaing.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showRatingDialog();
    }
});



        //get food id from Intent
        if(getIntent()!=null)
        {
            foodId=getIntent().getStringExtra("FoodId");
            if(!foodId.isEmpty())
            {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    getDetailFood(foodId);
                    getRatingFood(foodId);
                }
                else {
                    Toast.makeText(FoodDetail.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        }

    }

    private void getRatingFood(String foodId) {

        com.google.firebase.database.Query foodRating=ratingTbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate This Food")
                .setDescription("Please select some star and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please  write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnimation)
                .create(FoodDetail.this)
                .show();
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

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        final Rating rating=new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(value),comments);

ratingTbl.push()
        .setValue(rating)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FoodDetail.this,"Thank You For Your Feedback",Toast.LENGTH_LONG).show();
            }
        });
/*
        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    //remove old value
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();
                    //Update new value

                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                else
                {
                    //Update new value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this,"Thank You For Your Feedback",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });  */
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
