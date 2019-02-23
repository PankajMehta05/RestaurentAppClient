package resaurentapp.pankaj.com.restaurentapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import resaurentapp.pankaj.com.restaurentapp.Common.Common;
import resaurentapp.pankaj.com.restaurentapp.Interface.ItemClickListener;
import resaurentapp.pankaj.com.restaurentapp.Model.Category;
import resaurentapp.pankaj.com.restaurentapp.Model.Request;
import resaurentapp.pankaj.com.restaurentapp.ViewHolder.FoodViewHolder;
import resaurentapp.pankaj.com.restaurentapp.ViewHolder.OrderViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    /*
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/restaurant_font.otf").
//                        setFontAttrId(R.attr.fontPath)
//                .build());

        setContentView(R.layout.activity_order_status);
        database = FirebaseDatabase.getInstance();

        requests = database.getReference("Requests");
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadOrders(Common.currentUser.getPhone());

//        if(getIntent().getExtras()==null)
//        {
//            loadOrders(Common.currentUser.getPhone());
//
//        }
//        else
//        {
//            loadOrders(getIntent().getStringExtra("userPhone"));
//        }

    }

    private void loadOrders(String phone) {

        Query getOrderByUser=requests.orderByChild("phone")
                .equalTo(phone);
        FirebaseRecyclerOptions<Request> orderOptions=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser,Request.class)
                .build();

        adapter =new FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOptions) {

            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(itemView);
            }
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderId.setText(adapter.getRef((position)).getKey());
                viewHolder.txtordersStatus.setText(Common.convertCodetoStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
            }


        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        adapter.stopListening();
        super.onStop();
    }
    */

    private static final String TAG = "Basel";
    FirebaseDatabase database;
    DatabaseReference table_request;
    RecyclerView recyclerView_listOrder;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    // Press Ctrl + O

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cambria.ttf")
               // .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        table_request = database.getReference("request");

        recyclerView_listOrder = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView_listOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_listOrder.setLayoutManager(layoutManager);

        if(getIntent().getStringExtra("userPhone") == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {

        // Create query by phone
        Query query = table_request.orderByChild("phone").equalTo(phone);

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {

                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtordersStatus.setText(Common.convertCodetoStatus(model.getStatus()));


                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                final Request clickedItem = model;
                viewHolder.setItemClickListner(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(OrderStatus.this, "" + clickedItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView_listOrder.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(adapter != null)
            adapter.stopListening();
    }
}




