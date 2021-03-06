package resaurentapp.pankaj.com.restaurentapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import resaurentapp.pankaj.com.restaurentapp.Common.Common;
import resaurentapp.pankaj.com.restaurentapp.Database.Database;
import resaurentapp.pankaj.com.restaurentapp.Interface.ItemClickListener;
import resaurentapp.pankaj.com.restaurentapp.Model.Food;
import resaurentapp.pankaj.com.restaurentapp.Model.Order;
import resaurentapp.pankaj.com.restaurentapp.ViewHolder.FoodViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {
/*
RecyclerView recyclerView;
RecyclerView.LayoutManager layoutManager;
FirebaseDatabase database;
FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
DatabaseReference foodList;
String categoryId="";

// Search Functionality
FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
List<String> suggestList=new ArrayList<>();
MaterialSearchBar materialSearchBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");
        recyclerView=findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // get intent here
        if(getIntent()!=null)
        {
            categoryId=getIntent().getStringExtra(("CategoryId"));

        }
        if(!categoryId.isEmpty() && categoryId != null)
        {
            loadListFood(categoryId);
        }



        // Search

        materialSearchBar=findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Your Food");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList );
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        materialSearchBar.addTextChangeListener(new TextWatcher() {




            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override


            public void onTextChanged(CharSequence s, int start, int before, int count) {
             List<String> suggest=new ArrayList<String>();
              for(String search:suggestList)
          {
               if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()));
               suggest.add(search);

             }
                    materialSearchBar.setLastSuggestions(suggest);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search bar is closed
                //restore original adapter
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
// when search finish
                //show result of search adapter
                startSearch(text);
                
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }




    private void loadSuggest() {
        {

            {
                foodList.orderByChild("MenuId").equalTo(categoryId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Food item = data.getValue(Food.class);
                                    suggestList.add(item.getName());

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

        }
    }



    private void loadListFood(final String categoryId) {

    adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,
                   foodList.orderByChild("MenuId").equalTo(categoryId)) {// like select * from foods where menuid=
        @Override
        protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
            viewHolder.food_name.setText(model.getName());

            Picasso.with(FoodList.this)
                    .load(model.getImage())
                    .into(viewHolder.food_image);

            final Food local=model;

            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(FoodList.this, FoodDetail.class);
                    intent.putExtra("FoodId", adapter.getRef(position).getKey());
                    startActivity(intent);
                    Toast.makeText(FoodList.this, local.getName() + " ", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mFoodViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
            return new FoodViewHolder(mFoodViewHolder);
        }
    };
    recyclerView.setAdapter(adapter);

}

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }





    private void startSearch(CharSequence text) {
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("Name").equalTo(text.toString())//compare name
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                 Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                 foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                 startActivity(foodDetail);

            }
        }); recyclerView.setAdapter(searchAdapter);

    }
        };

    }
    }
*/

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String CategoryId = "";

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    //Serach Functionality
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
       Database localDb;


       CallbackManager callbackManager;
       ShareDialog shareDialog;


       SwipeRefreshLayout swipeRefreshLayout;


Target target=new Target() {

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        SharePhoto photo=new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        if(ShareDialog.canShow(SharePhotoContent.class))
        {
            SharePhotoContent content=new SharePhotoContent.Builder().addPhoto(photo).build();
            shareDialog.show(content);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }



    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf").
                        setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_food_list);

        callbackManager= new CallbackManager.Factory().create();
        shareDialog=new ShareDialog(this);

        //Firebase Init
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

swipeRefreshLayout=findViewById(R.id.swipe_layout);

      swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
        android.R.color.holo_green_dark,
        android.R.color.holo_orange_dark,
        android.R.color.holo_blue_dark);

swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        if (getIntent() != null)
            CategoryId = getIntent().getStringExtra("CategoryId");

        if (!CategoryId.isEmpty() && CategoryId != null){

            if(Common.isConnectedToInternet(getBaseContext()))

                loadListFood(CategoryId);
            else {
                Toast.makeText(FoodList.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                return;
            }


        }

    }
});

swipeRefreshLayout.post(new Runnable() {
    @Override
    public void run() {
        if (getIntent() != null)
            CategoryId = getIntent().getStringExtra("CategoryId");

        if (!CategoryId.isEmpty() && CategoryId != null){

            if(Common.isConnectedToInternet(getBaseContext()))

                loadListFood(CategoryId);
            else {
                Toast.makeText(FoodList.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                return;
            }


        }
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Your Food...");
        loadSuggest();

        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ketika user mengetik teks, kita akan mengubah daftar rekomendasi

                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList){  // looping di suggestLIst
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }
});


        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //favorites
        localDb=new Database(this);

        //Get Intent Here

        //Pencarian

    }

    private void startSearch(CharSequence text) {

        Query searchByName=foodList.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Food> foodOptions=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();

        searchAdapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Log.d("TAG", ""+adapter.getItemCount());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClik) {
                        //Start New Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey()); //Send food Id to new activity
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);

    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(CategoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

   private void loadListFood(String categoryId) {

       Query searchByName=foodList.orderByChild("menuId").equalTo(categoryId);
       FirebaseRecyclerOptions<Food> foodOptions=new FirebaseRecyclerOptions.Builder<Food>()
               .setQuery(searchByName,Food.class)
               .build();





        adapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override


            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {

                        viewHolder.food_name.setText(model.getName());
                        viewHolder.food_price.setText(String.format("$ %s",model.getPrice().toString()));
                        Log.d("TAG", ""+adapter.getItemCount());
                        Picasso.with(getBaseContext()).load(model.getImage())
                                .into(viewHolder.food_image);

viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        new Database(getBaseContext()).addToCart(new Order(adapter.getRef(position).getKey()
                ,model.getName()
                ,"1"
                ,model.getPrice()
                ,model.getDiscount()
                ,model.getImage()));
        Toast.makeText(FoodList.this,"Added to cart",Toast.LENGTH_SHORT).show();
    }
});
                        // final Food local=model;
                        // add favorites

                        if(localDb.isFavorites(adapter.getRef(position).getKey()))

                            viewHolder.fav_image.setImageResource(R.drawable.ic_security_black_24dp);

                        //click to change status of favorates

                        viewHolder.shareImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Picasso.with(getBaseContext())
                                        .load(model.getImage())
                                        .into(target);

                            }
                        });
                        viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(localDb.isFavorites(adapter.getRef(position).getKey()))
                                {
                                    localDb.addToFavorites((adapter.getRef(position).getKey()));

                                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);


                                    Toast.makeText(FoodList.this,""+model.getName()+" was added to favorates ",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                    localDb.deleteFromFavorites((adapter.getRef(position).getKey()));
                                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                                    Toast.makeText(FoodList.this,""+model.getName()+" was removed from favorates",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClik) {
                                //Start New Activity
                                Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                                foodDetail.putExtra("FoodId", adapter.getRef(position).getKey()); //Send food Id to new activity
                                startActivity(foodDetail);
                            }
                        });
                    }



            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);

            }
        };

        // Set Adapter
       adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
    protected void onResume() {
        super.onResume();

        if(adapter!=null)
        {
            adapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

//        searchAdapter.stopListening();
   //     adapter.stopListening();
    }

    //    private void loadListFood(String categoryId) {
//        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
//                R.layout.food_item,
//                FoodViewHolder.class,
//                foodList.orderByChild("menuId").equalTo(categoryId))
//        {
//            @Override
//            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, final int position) {
//                viewHolder.food_name.setText(model.getName());
//                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
//                final Food local=model;
//                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
//                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());//send Food ID to new Acitivity
//                        startActivity(foodDetail);
//                    }
//
//
//                });
//            }
//        };
//        //Set adapter
//        recyclerView.setAdapter(adapter);
//    }
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
}


}
