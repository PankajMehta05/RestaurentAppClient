package resaurentapp.pankaj.com.restaurentapp;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import resaurentapp.pankaj.com.restaurentapp.Common.Common;
import resaurentapp.pankaj.com.restaurentapp.Model.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
//Button btnSignIn,btnSignUp;
    Button btncontinue;
TextView txtSlogan;
    private int REQUEST_CODE=7171;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/restaurant_font.otf").
//                        setFontAttrId(R.attr.fontPath)
//                .build());
        setContentView(R.layout.activity_main);

    //    printKeyHash();

        //Init database
        database=FirebaseDatabase.getInstance();
        users=database.getReference("User");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);

        btncontinue=findViewById(R.id.btnContinue);
     //   btnSignUp=findViewById(R.id.btnSignUp);
        txtSlogan=findViewById(R.id.txtSlogan);

//
//      Typeface face=Typeface.createFromAsset(getAssets(), "fonts/restaurant_font.otf");
//       txtSlogan.setTypeface(face);

     //   Paper.init(this);


        btncontinue.setOnClickListener(new View.OnClickListener() {@Override
       public void onClick(View v) {
//           Intent signIn=new Intent(MainActivity.this,SignIn.class);
//           startActivity(signIn);
            startloginSystem();
       }
   });
        if(AccountKit.getCurrentAccessToken()!=null)
        {
final AlertDialog waitingDialog=new SpotsDialog.Builder().setContext(this).build();
waitingDialog.show();
waitingDialog.setMessage("Please wait...");
waitingDialog.setCancelable(false);
AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
    @Override
    public void onSuccess(Account account) {
        //copy code from exits
        users.child(account.getPhoneNumber().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User localUser=dataSnapshot.getValue(User.class);
                        Intent homeIntent = new Intent(MainActivity.this, Home.class);
                        resaurentapp.pankaj.com.restaurentapp.Common.Common.currentUser = localUser;
                        startActivity(homeIntent);
                        waitingDialog.dismiss();
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onError(AccountKitError accountKitError) {

    }
});
        }


//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//       @Override
//       public void onClick(View v) {
//Intent signUp=new Intent(MainActivity.this,SignUp.class);
//startActivity(signUp);
//       }
//   });
//        //Check remember
//        String user=Paper.book().read(Common.USER_KEY);
//        String pwd=Paper.book().read(Common.PWD_KEY);
//        if(user!=null && pwd!=null){
//
//            if(!user.isEmpty() && !pwd.isEmpty())
//            {
//                  login(user,pwd);
//            }
//        }

    }

    private void startloginSystem() {
        Intent intent=new Intent(MainActivity.this,AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE
                , AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void printKeyHash() {
        try{
            PackageInfo info=getPackageManager().getPackageInfo("resaurentapp.pankaj.com.restaurentapp", PackageManager.GET_SIGNATURES);
for(Signature signature:info.signatures){
    MessageDigest md=MessageDigest.getInstance("SHA");
md.update(signature.toByteArray());
Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
}
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        {
            AccountKitLoginResult  result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError()!=null)
            {
                Toast.makeText(this,""+result.getError().getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                return;

            }
            else if(result.wasCancelled())
            {
                Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                if(result.getAccessToken()!=null)
                {
                    final AlertDialog waitingDialog=new SpotsDialog.Builder().setContext(this).build();
                    waitingDialog.show();
                    waitingDialog.setMessage("Please wait...");
                    waitingDialog.setCancelable(false);

                    //Get current Phone
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String userPhone=account.getPhoneNumber().toString();
                            //Check if exits on Firebase
                            users.orderByKey().equalTo(userPhone)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.child(userPhone).exists())
                                            {
                                                //we will create new user and login
                                                User newUser=new User();
                                                newUser.setPhone(userPhone);
                                                newUser.setName("");
                                                newUser.setBalance(0.0);

                                                //Add to firebase
                                                users.child(userPhone).setValue(newUser)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(MainActivity.this,"User register successfully",Toast.LENGTH_SHORT).show();

                                                                    //Login
                                                                    users.child(userPhone)
                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    User localUser=dataSnapshot.getValue(User.class);
                                                                                    Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                                    resaurentapp.pankaj.com.restaurentapp.Common.Common.currentUser = localUser;
                                                                                    startActivity(homeIntent);
                                                                                    waitingDialog.dismiss();
                                                                                    finish();

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                users.child(userPhone)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                User localUser=dataSnapshot.getValue(User.class);
                                                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                                                resaurentapp.pankaj.com.restaurentapp.Common.Common.currentUser = localUser;
                                                                startActivity(homeIntent);
                                                                waitingDialog.dismiss();
                                                                finish();

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                          Toast.makeText(MainActivity.this,""+accountKitError.getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

  /*  private void login(final String phone, final String pwd) {

        //InitFirebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("user");
        if (resaurentapp.pankaj.com.restaurentapp.Common.Common.isConnectedToInternet(getBaseContext()))
        {

            //save user and password



            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting !!!");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();

                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);


                            // Toast.makeText(SignIn.this, "Sign in sucessfully", Toast.LENGTH_SHORT).show();
                            //   resaurentapp.pankaj.com.restaurentapp.Common.Common.currentUser=user;


                            resaurentapp.pankaj.com.restaurentapp.Common.Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else

                    {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User Does Not Exist in Database", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
    */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
