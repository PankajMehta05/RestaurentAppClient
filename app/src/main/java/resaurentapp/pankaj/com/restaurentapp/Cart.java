package resaurentapp.pankaj.com.restaurentapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import resaurentapp.pankaj.com.restaurentapp.Common.Common;
import resaurentapp.pankaj.com.restaurentapp.Common.Config;
import resaurentapp.pankaj.com.restaurentapp.Database.Database;
import resaurentapp.pankaj.com.restaurentapp.Model.MyResponse;
import resaurentapp.pankaj.com.restaurentapp.Model.Notification;
import resaurentapp.pankaj.com.restaurentapp.Model.Order;
import resaurentapp.pankaj.com.restaurentapp.Model.Request;
import resaurentapp.pankaj.com.restaurentapp.Model.Sender;
import resaurentapp.pankaj.com.restaurentapp.Model.Token;
import resaurentapp.pankaj.com.restaurentapp.Model.User;
import resaurentapp.pankaj.com.restaurentapp.Remote.APIService;
import resaurentapp.pankaj.com.restaurentapp.Remote.IGoogleService;
import resaurentapp.pankaj.com.restaurentapp.ViewHolder.CartAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;

    // Declare Google Map Api Retrofit
    IGoogleService mGoogleMapService;
    APIService mService;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;

    Place shippingAddress;

    Button btnPlace;


    boolean ableToGetDeviceLocation;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleAPiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private static final int LOCATION_REQUEST_CODE = 9999;


    //Paypal Payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //use sandbox because we test change it later
            .clientId(Config.Paypal_client_ID);

    String address, comment;


    LatLng latlng;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//        .setDefaultFontPath("fonts/restaurant_font.otf").
//        setFontAttrId(R.attr.fontPath)
//        .build());
        setContentView(R.layout.activity_cart);
//
        ableToGetDeviceLocation = false;
        //Init
        mGoogleMapService = Common.getGoogleMapAPI();


        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {    // Check if your device has pay services ?
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        //Init service

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        mService = Common.getFCMService();

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    showAlertDialog();
                else {
                    Toast.makeText(Cart.this, "Your cart is empty", Toast.LENGTH_LONG).show();
                }
            }

        });

        loadListFood();


    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices())   //if you have play services on device
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
        }
    }





    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((UPDATE_INTERVAL));
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }








    private synchronized void buildGoogleApiClient() {
        mGoogleAPiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleAPiClient.connect();

    }






    public void setmLocationRequest(LocationRequest mLocationRequest) {
        this.mLocationRequest = mLocationRequest;
    }






//    private boolean checkPlayServices() {
//        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if(resultCode!= ConnectionResult.SUCCESS)
//        {
//            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
//                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_REQUEST).show();
//            else
//            {
//                Toast.makeText(this,"This device is not supported",Toast.LENGTH_SHORT).show();
//                finish();
//            }
//
//            return false;
//        }
//        return true;
//    }











    private boolean checkPlayServices() {

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {

                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_REQUEST).show();

            } else {

                Toast.makeText(this, "Device isn't supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }






    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step");
        alertDialog.setMessage("Enter your Address");
        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_comment_address, null);
//    final MaterialEditText edtAddress=order_address_comment.findViewById(R.id.edtAddress);
        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //Hide search icon bar before fragment

        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //set hint for Auto complete edit text

        ((android.widget.EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter your Address");

        //set text size here

        ((android.widget.EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);


        //Get address from place autocomplete

        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR", status.getStatusMessage());
            }
        });


        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);

        final RadioButton rdiShipToAddress = order_address_comment.findViewById(R.id.rdiShipToLAddress);
        final RadioButton rdiHomeToAddress = order_address_comment.findViewById(R.id.rdiHomeAddress);

        final RadioButton rdiCOD = order_address_comment.findViewById(R.id.rdiCOD);
        final RadioButton rdiPaypal = order_address_comment.findViewById(R.id.rdiPaypal);

        final RadioButton rdiBalance = order_address_comment.findViewById(R.id.rdiEatItBalance);


        //Event Radio

        rdiHomeToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Common.currentUser.getHomeAddress() != null || !TextUtils.isEmpty(Common.currentUser.getHomeAddress())) {
                        address = Common.currentUser.getHomeAddress();
                        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
                    } else {
                        Toast.makeText(Cart.this, "Please update your home address", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        rdiShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //ship to this address feature

                if (isChecked && ableToGetDeviceLocation)   //isCHecked is true
                {
                    mGoogleMapService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    // If fetch API successfully
                                    try {

                                        JSONObject object = new JSONObject(response.body().toString());
                                        if (object.getJSONArray("results").length() > 0) {

                                            JSONArray result = object.getJSONArray("results");
                                            JSONObject firstObject = result.getJSONObject(0);
                                            address = firstObject.getString("formatted_address");
                                            ((android.widget.EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                                    .setText(address);

                                        }
                                        //set this address to edtAddress

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(Cart.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }
            }
        });


        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //add check condition here
                //if user select address from place fragment,just use it
                //if use select Ship to this address get address from location and use it
                //if use select home address,get Homeaddress from profile and use it
                address = shippingAddress.getAddress().toString();
//                if (!rdiShipToAddress.isChecked() && !rdiHomeToAddress.isChecked()) {
//                    if (shippingAddress != null)
//                        //if both radio are not selected
//
//                } else {
//                    Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();
//                    //Fix crash fragment
//                    getFragmentManager().beginTransaction()
//                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                    return;
//                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();
                    //Fix crash fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                    return;
                }
                //show paypal to payment

                comment = edtComment.getText().toString();


                if (!rdiCOD.isChecked() && !rdiCOD.isChecked() && !rdiBalance.isChecked()) {
                    Toast.makeText(Cart.this, "Please select payment option", Toast.LENGTH_SHORT).show();
                    //Fix crash fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                    return;
                } else if (rdiPaypal.isChecked()) {


                    String formatAmount = txtTotalPrice.getText().toString()
                            .replace("$", "")
                            .replace(",", "");

                    float amount = Float.parseFloat(formatAmount);


                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                            "USD",
                            "Eat it App Order "
                            , PayPalPayment.PAYMENT_INTENT_SALE);


                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);

                } else if (rdiCOD.isChecked()) {
                    //Copy code from onActivityResult
                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString()
                            , "0",  //status
                            comment,
                            "COD",
                            "Unpaid",//state from json
                            String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude())
                            , cart
                    );

                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child((order_number)).setValue(request);


                    requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                    new Database(getBaseContext()).cleanCart();

                    sendNotificationOrder(order_number);


                    Toast.makeText(Cart.this, "Thank you,Order place", Toast.LENGTH_SHORT).show();
                    finish();

                } else if (rdiBalance.isChecked()) {
                    double amount = 0;
                    try {
                        amount = Common.formatCurrency(txtTotalPrice.getText().toString(), Locale.US).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (Common.currentUser.getBalance() >= amount) {
                        Request
                                request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString()
                                , "0",  //status
                                comment,
                                "EatIt Balance",
                                "Paid",//state from json
                                String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude())
                                , cart
                        );

                        final String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child((order_number)).setValue(request);


                        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                        new Database(getBaseContext()).cleanCart();

                        //Update balance
                        double balance = Common.currentUser.getBalance() - amount;
                        Map<String, Object> update_balance = new HashMap<>();
                        update_balance.put("balance", balance);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(Common.currentUser.getPhone())
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Refresh user
                                            FirebaseDatabase.getInstance()
                                                    .getReference("User")
                                                    .child(Common.currentUser.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                                            sendNotificationOrder(order_number);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });


                        Toast.makeText(Cart.this, "Thank you,Order place", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(Cart.this, "Your balance is not enough ,please choose other payment option", Toast.LENGTH_SHORT).show();
                    }
                }

                //Remove fragment
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
            }

        });

        //Remove fragment


        alertDialog.show();
    }








    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);

                        JSONObject jsonObject = new JSONObject(paymentDetail);
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString()
                                , "0",  //status
                                comment,
                                "Paypal",
                                jsonObject.getJSONObject("response").getString("state"),//state from json
                                String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude)
                                , cart
                        );

                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child((order_number)).setValue(request);


                        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                        new Database(getBaseContext()).cleanCart();

                        sendNotificationOrder(order_number);


                        Toast.makeText(Cart.this, "Thank you,Order place", Toast.LENGTH_SHORT).show();
                        finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment cancel", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);

        final ValueEventListener valueEventListener = data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    //create new raw payload to sen


                    resaurentapp.pankaj.com.restaurentapp.Model.Notification notification = new resaurentapp.pankaj.com.restaurentapp.Model.Notification(("" + order_number));


                    //Notification notification = new Notification("Ai Robotics Resturant", "You have new Order: " + order_number);


                    Sender content = new Sender(serverToken.getToken(), notification);


                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                                            finish();

                                        } else {
                                            Toast.makeText(Cart.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());

                                }
                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        int total = 0;
        for (Order order : cart) {

            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {
        //we will remove item at List<order> by position
        cart.remove(position);
        //After that we will delete all data from sqlite
        new Database(this).cleanCart();
        // and final,we will update new data from List<order> to sqlite
        for (Order item : cart)
            new Database(this).addToCart(item);
        // Refresh
        loadListFood();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleAPiClient, mLocationRequest, this);
    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPiClient);
        if (mLastLocation != null) {
            Log.d("LOCATION", "YOUR LOACTION : " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        } else {
            Log.d("LOCATION", "COULD NOT GET YOUR LOCATION");


        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleAPiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }
}

//
//public class Cart extends AppCompatActivity {
//
//private static final String TAG = "Basel";
//public TextView textView_totalPrice;
//        FirebaseDatabase database;
//        DatabaseReference table_request, table_user;
//        RecyclerView recyclerView_listCart;
//        RecyclerView.LayoutManager layoutManager;
//        RadioButton radioButton_shipToThisAddress , radioButton_homeAddress, radioButton_cashOnDelivery,radioButton_paypal, radioButton_balance ;
//        EditText editText_notes;
////    EditText editText_address;
//        FButton button_placeOrder;
//        List<Order> carts = new ArrayList<>();
//        CartAdapter cartAdapter;
//        AlertDialog addressDialog;
//        APIService mService;
//    IGoogleService mGoogleMapAPIService;
//
//        String address, notes;
//        Place shippingAddress;
//
//private final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
//private final int PLAY_SERVICES_RESOLUTION_REQUEST = 2;
//private FusedLocationProviderClient mFusedLocationClient;
//        Location currentLocation;
//        boolean ableToGetDeviceLocation;
//
//// Press Ctrl + O
//@Override
//protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//        }
//
//@SuppressLint("MissingPermission")
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        == PackageManager.PERMISSION_GRANTED) {
//
//        if (checkPlayServices()) {
//        getDeviceLocation();
//        }
//        } else
//        checkLocationPermission();
//
//        // Add this code before setContentView method
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//        .setDefaultFontPath("fonts/cambria.ttf")
//        .setFontAttrId(R.attr.fontPath)
//        .build());
//
//        setContentView(R.layout.activity_cart);
//
//        ableToGetDeviceLocation = false;
//        // Init services
//        mService = Common.getFCMService();
//        mGoogleMapAPIService = Common.getGoogleMapAPI();
//
//        button_placeOrder = (FButton) findViewById(R.id.btnPlaceOrder);
//        textView_totalPrice = (TextView) findViewById(R.id.total);
//
//        database = FirebaseDatabase.getInstance();
//        table_request = database.getReference("Requests");
//        table_user = database.getReference("User");
//
//        recyclerView_listCart = (RecyclerView) findViewById(R.id.listCart);
//        recyclerView_listCart.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView_listCart.setLayoutManager(layoutManager);
//
//        loadCartList();
//
//        button_placeOrder.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        if (carts.size() > 0)
//        openAddressDialog();
//        else
//        Toast.makeText(Cart.this, "Your cart is empty ! ", Toast.LENGTH_SHORT).show();
//        }
//        });
//        }
//
//private void openAddressDialog() {
//
//        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.order_comment_address, null);
//        myAlertDialog.setView(dialogView);
//        myAlertDialog.setCancelable(true);
//        myAlertDialog.setTitle("One more step !");
////        editText_address = (EditText) dialogView.findViewById(R.id.editText_address);
//final PlaceAutocompleteFragment placeAutocompleteFragment_address = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        // Hide search icon before fragment
//        placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
//        // Set hint for the fragment
//        ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("CLICK HERE !");
//
//        ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15);
//
//        placeAutocompleteFragment_address.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//@Override
//public void onPlaceSelected(Place place) {
//        shippingAddress = place;
//        }
//
//@Override
//public void onError(Status status) {
//        Log.d(TAG, "onError: " + status.getStatusMessage());
//        }
//        });
//
//        editText_notes = (EditText) dialogView.findViewById(R.id.edtComment);
//        radioButton_shipToThisAddress = (RadioButton) dialogView.findViewById(R.id.rdiShipToLAddress);
//        radioButton_homeAddress = (RadioButton) dialogView.findViewById(R.id.rdiHomeAddress);
//        radioButton_paypal = (RadioButton) dialogView.findViewById(R.id.rdiPaypal);
//        radioButton_cashOnDelivery = (RadioButton) dialogView.findViewById(R.id.rdiCOD);
//        radioButton_balance = (RadioButton) dialogView.findViewById(R.id.rdiEatItBalance);
//
//        radioButton_shipToThisAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//@Override
//public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//        if(isChecked && ableToGetDeviceLocation){
//
//        Log.d(TAG, "onCheckedChanged: " + String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f &sensor=false",
//        currentLocation.getLatitude(),currentLocation.getLongitude()));
//
//        mGoogleMapAPIService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
//        currentLocation.getLatitude(),currentLocation.getLongitude()))
//        .enqueue(new Callback<String>() {
//@Override
//public void onResponse(Call<String> call, Response<String> response) {
//
//        Log.d(TAG, "onResponse: " + response.body().toString());
//
//        try {
//
//        JSONObject jsonObject = new JSONObject(response.body().toString());
//
//        if(jsonObject.getJSONArray("results").length() > 0){
//
//        JSONArray resultsArray = jsonObject.getJSONArray("results");
//        JSONObject firstObject = resultsArray.getJSONObject(0);
//        address = firstObject.getString("formatted_address");
//        ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
//        }
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//        }
//
//@Override
//public void onFailure(Call<String> call, Throwable t) {
//        Toast.makeText(Cart.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "onFailure: " + t.getMessage());
//        }
//        });
//        }
//        }
//        });
//
//        radioButton_homeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//@Override
//public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//        if(isChecked) {
//        if (!TextUtils.isEmpty(Common.currentUser.getHomeAddress()) || Common.currentUser.getHomeAddress() != null) {
//        address = Common.currentUser.getHomeAddress();
//        ((EditText) placeAutocompleteFragment_address.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
//        }
//        else{
//        Toast.makeText(Cart.this, "Please update your home address", Toast.LENGTH_LONG).show();
//        }
//        }
//        }
//        });
//
//        myAlertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
//
//        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//        dialog.dismiss();
//        Toast.makeText(Cart.this, "Order is canceled", Toast.LENGTH_LONG).show();
//        // Remove fragment
//        getFragmentManager().beginTransaction()
//        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//        }
//        });
//
//        myAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//
//        if(!radioButton_shipToThisAddress.isChecked() && !radioButton_homeAddress.isChecked()){
//
//        if(shippingAddress != null){
//
//        address = shippingAddress.getAddress().toString();
//        }else{
//
//        Toast.makeText(Cart.this, "No address is entered", Toast.LENGTH_SHORT).show();
//        // Remove fragment
//        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//        return;
//        }
//        }
//
//        if(TextUtils.isEmpty(address)){
//
//        Toast.makeText(Cart.this, "No address is entered", Toast.LENGTH_SHORT).show();
//        // Remove fragment
//        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//        return;
//        }
//
//        notes = editText_notes.getText().toString();
//final String orderNumber;
//        Request request;
//
//        if(!radioButton_cashOnDelivery.isChecked() && !radioButton_paypal.isChecked() && !radioButton_balance.isChecked()){
//
//        Toast.makeText(Cart.this, "No payment method is selected", Toast.LENGTH_SHORT).show();
//        // Remove fragment
//        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//        return;
//        }
//        else if(radioButton_paypal.isChecked()) {
//
//        if(shippingAddress != null){
//
//        request = new Request(Common.currentUser.getPhone(),
//        Common.currentUser.getName(),
//        address,
//        textView_totalPrice.getText().toString(),
//        carts,
//        notes,
//        String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
//        "PayPal",
//        "Unpaid");
//        }
//        else{
//
//        request = new Request(
//        Common.currentUser.getPhone(),
//        Common.currentUser.getName(),
//        address,
//        textView_totalPrice.getText().toString(),
//        carts,
//        notes,
//        "No Latlng",
//        "PayPal",
//        "Unpaid");
//        }
//
//        // Remove fragment
//        getFragmentManager().beginTransaction()
//        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//
//        // Submit to firebase
//        // currentTimeMillis is considered as a key
//        orderNumber = String.valueOf(System.currentTimeMillis());
//        table_request.child(orderNumber).setValue(request);
//        dialog.dismiss();
//
//        // Delete carts
//        new Database(getBaseContext()).cleanCart();
//        sendOrderNotification(orderNumber);
//        }
//        else if(radioButton_cashOnDelivery.isChecked()){
//
//        if(shippingAddress != null){
//
//        request = new Request(
//        Common.currentUser.getPhone(),
//        Common.currentUser.getName(),
//        address,
//        textView_totalPrice.getText().toString(),
//        carts,
//        notes,
//        String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
//        "Cash On Delivery",
//        "Unpaid");
//        }
//        else{
//
//        request = new Request(
//        Common.currentUser.getPhone(),
//        Common.currentUser.getName(),
//        address,
//        textView_totalPrice.getText().toString(),
//        carts,
//        notes,
//        "No Latlng",
//        "Cash On Delivery",
//        "Unpaid");
//        }
//
//        // Remove fragment
//        getFragmentManager().beginTransaction()
//        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//
//        // Submit to firebase
//        // currentTimeMillis is considered as a key
//        orderNumber = String.valueOf(System.currentTimeMillis());
//        table_request.child(orderNumber).setValue(request);
//        dialog.dismiss();
//
//        // Delete carts
//        new Database(getBaseContext()).cleanCart();
//        sendOrderNotification(orderNumber);
//        }
//        else if(radioButton_balance.isChecked()){
//
//        double amount = 0;
//        try {
//        amount = Common.formatCurrency(textView_totalPrice.getText().toString(),Locale.US).doubleValue();
//        } catch (ParseException e) {
//        e.printStackTrace();
//        }
//
//        if(Common.currentUser.getBalance() >= amount){
//
//        if(shippingAddress != null){
//
//        request = new Request(
//        Common.currentUser.getPhone(),
//        Common.currentUser.getName(),
//        address,
//        textView_totalPrice.getText().toString(),
//        carts,
//        notes,
//        String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
//        "EatIt Balance",
//        "Paid");
//        }
//        else{
//
//        request = new Request(
//        Common.currentUser.getPhone(),
//        Common.currentUser.getName(),
//        address,
//        textView_totalPrice.getText().toString(),
//        carts,
//        notes,
//        "No Latlng",
//        "EatIt Balance",
//        "Paid");
//        }
//
//        // Remove fragment
//        getFragmentManager().beginTransaction()
//        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//
//        // Submit to firebase
//        // currentTimeMillis is considered as a key
//        orderNumber = String.valueOf(System.currentTimeMillis());
//        table_request.child(orderNumber).setValue(request);
//        dialog.dismiss();
//
//        // Delete carts
//        new Database(getBaseContext()).cleanCart();
//
//        // Update balance
//        double balance = Common.currentUser.getBalance() - amount;
//        Map<String,Object> updated_balance = new HashMap<>();
//        updated_balance.put("balance",balance);
//        table_user.child(Common.currentUser.getPhone())
//        .updateChildren(updated_balance)
//        .addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//
//        if(task.isSuccessful()){
//
//        // Refresh user
//        table_user.child(Common.currentUser.getPhone())
//        .addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(DataSnapshot dataSnapshot) {
//
//        Common.currentUser = dataSnapshot.getValue(User.class);
//        sendOrderNotification(orderNumber);
//        }
//
//@Override
//public void onCancelled(DatabaseError databaseError) {
//
//        }
//        });
//        }
//        }
//        });
//        }else{
//        Toast.makeText(Cart.this, "Your balance isn't enough, please choose another payment method", Toast.LENGTH_SHORT).show();
//        }
//        }
//        }
//        });
//
//        addressDialog = myAlertDialog.create();
//        addressDialog.show();
//        }
//
//private void sendOrderNotification(final String orderNumber) {
//
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference table_token = db.getReference("token");
//        Query data = table_token.orderByChild("serverToken").equalTo(true);  // Get all nodes which its isServerToken is true
//        data.addValueEventListener(new ValueEventListener() {
//@Override
//public void onDataChange(DataSnapshot dataSnapshot) {
//
//        for (DataSnapshot item : dataSnapshot.getChildren()) {
//
//        Token serverToken = item.getValue(Token.class);
//
//        // Create Raw payload to send
//        Notification notification = new Notification("Basel", "New order: " + orderNumber);
//        Sender content = new Sender(serverToken.getToken(), notification);
//        mService.sendNotification(content)
//        .enqueue(new Callback<MyResponse>() {
//@Override
//public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//        if (response.code() == 200) {
//
//        if (response.body().success == 1) {
//        Toast.makeText(Cart.this, "Thank you , order is placed ", Toast.LENGTH_LONG).show();
//        finish();
//        } else
//        Toast.makeText(Cart.this, "Failed ", Toast.LENGTH_LONG).show();
//        }
//        }
//
//@Override
//public void onFailure(Call<MyResponse> call, Throwable t) {
//
//        }
//        });
//        }
//        }
//
//@Override
//public void onCancelled(DatabaseError databaseError) {
//
//        }
//        });
//        }
//
//private void loadCartList() {
//
//        carts = new Database(this).getCarts();
//        cartAdapter = new CartAdapter(carts, this);
//        cartAdapter.notifyDataSetChanged();
//        recyclerView_listCart.setAdapter(cartAdapter);
//
//        int totalPrice = 0;
//        for (Order i : carts)
//        totalPrice += Integer.parseInt(i.getPrice()) * Integer.parseInt(i.getQuantity());
//        Locale locale = new Locale("en", "US");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
//        textView_totalPrice.setText(fmt.format(totalPrice));
//        }
//
//public boolean onContextItemSelected(MenuItem item) {
//
//        if (item.getTitle().equals(Common.DELETE))
//        deleteCart(item.getOrder());
//
//        return super.onContextItemSelected(item);
//        }
//
//private void deleteCart(int position) {
//
//        // we'll remove item from list<order> by position
//        carts.remove(position);
//        // then , we delete all old data from SQLite
//        new Database(this).cleanCart();
//        // finally , we'll update the SQLite using the new updated carts
//        for (Order item : carts)
//        new Database(this).addToCart(item);
//        loadCartList();
//        }
//
//private boolean checkPlayServices() {
//
//        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
//
//        if (resultCode != ConnectionResult.SUCCESS) {
//
//        if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
//
//        GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//
//        } else {
//
//        Toast.makeText(this, "Device isn't supported", Toast.LENGTH_SHORT).show();
//        finish();
//        }
//        return false;
//        }
//        return true;
//        }
//
//@SuppressLint("MissingPermission")
//private void getDeviceLocation() {
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try {
//
//        Task location = mFusedLocationClient.getLastLocation();
//        location.addOnCompleteListener(new OnCompleteListener() {
//@Override
//public void onComplete(@NonNull Task task) {
//        if (task.isSuccessful() && task.getResult() != null) {
//
//        currentLocation = (Location) task.getResult();
//        Log.d(TAG, "onComplete: Location: " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
//        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        Toast.makeText(Cart.this, currentLocation.getLatitude() + " , " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//        ableToGetDeviceLocation = true;
//
//        } else {
//        Toast.makeText(Cart.this, "Unable to get current location !", Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//        } catch (SecurityException ex) {
//        Log.d(TAG, "GetDeviceLocation : SecurityException: " + ex.getMessage());
//        }
//        }
//
//private void checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        != PackageManager.PERMISSION_GRANTED &&
//        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        != PackageManager.PERMISSION_GRANTED)
//        // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
//        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//        // Show an explanation to the user *asynchronously* -- don't block
//        // this thread waiting for the user's response! After the user
//        // sees the explanation, try again to request the permission.
//        new AlertDialog.Builder(this)
//        .setTitle("Location Permission Needed")
//        .setMessage("This app needs the Location permission, please accept to use location functionality")
//        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialogInterface, int i) {
//        //Prompt the user once explanation has been shown
//        ActivityCompat.requestPermissions(Cart.this,
//        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//        MY_PERMISSIONS_REQUEST_LOCATION);
//        }
//        })
//        .create()
//        .show();
//        } else {
//        // No explanation needed, we can request the permission.
//        ActivityCompat.requestPermissions(this,
//        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//        MY_PERMISSIONS_REQUEST_LOCATION);
//        }
//        }
//
//@Override
//public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//        case MY_PERMISSIONS_REQUEST_LOCATION: {
//        // If request is cancelled, the result arrays are empty.
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        if (checkPlayServices()) {
//        getDeviceLocation();
//        }
//        } else {
//        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//        }
//        return;
//        }
//        }
//        }
//        }
