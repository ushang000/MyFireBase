package cn.ushang.myfire;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.logging.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.editText)
    EditText editText;
    @InjectView(R.id.editText2)
    EditText editText2;
    @InjectView(R.id.register)
    Button register;
    private FirebaseAnalytics firebaseAnalytics;
    private Button button;
    private int num = 0;
    private String userName = null;
    private Long userPassword = null;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference nameRef = rootRef.child("name");
    private DatabaseReference passwordRef = rootRef.child("password");
    private DatabaseReference databaseRef=FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth=FirebaseAuth.getInstance();
        /*button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num++;
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, num + "");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "点击");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });*/
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    //Log.i("shao"," current sign in success!");
                }
            }
        };
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editText.getText().toString();
                final String password=editText2.getText().toString();
                if(!email.isEmpty()&&!password.isEmpty()){
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user=task.getResult().getUser();
                                        String userName=user.getEmail();
                                        User myUser=new User(userName,password);
                                        databaseRef.child("users").child(user.getUid()).setValue(myUser);
                                        Log.i("shao","create account success");
                                        Toast.makeText(MainActivity.this,"sign in success",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Log.i("shao","create account failed");
                                        Toast.makeText(MainActivity.this," Sign in Failed ",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName=editText.getText().toString();
                String textPassword=editText2.getText().toString();
                Log.i("shao"," textName = "+textName+" textPassword = "+textPassword);
                mAuth.signInWithEmailAndPassword(textName,textPassword)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("shao"," task : "+task.isSuccessful());
                                if(task.isSuccessful()){
                                    startActivity(new Intent(MainActivity.this,DownloadActivity.class));
                                }
                            }
                        });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String token=FirebaseInstanceId.getInstance().getToken();
        Log.i("shao"," 令牌 : "+token);
        mAuth.addAuthStateListener(mAuthListener);

        /*nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        passwordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPassword = dataSnapshot.getValue(Long.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}
