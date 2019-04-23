package com.example.skillssport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private AutoCompleteTextView mPhoneNumber;
    private AutoCompleteTextView mFirstName;
    private AutoCompleteTextView mLastName;
    private AutoCompleteTextView mEmail;
    //private AutoCompleteTextView mHobby;
    private Button mEditandSubmit;
    String buttonState;
    String my_uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        my_uid=getIntent().getStringExtra("my_uid");

        mPhoneNumber = findViewById(R.id.number);
        mPhoneNumber.setEnabled(false);
        mFirstName=findViewById(R.id.first_name);
        mLastName=findViewById(R.id.last_name);
        mEmail=findViewById(R.id.email);
        mEditandSubmit=findViewById(R.id.edit_and_submit);
        //mHobby=findViewById(R.id.hobby);

        buttonState="Submit";

        boolean isSignedIn = getIntent().getBooleanExtra("isSignedIn",false);
        if(isSignedIn){
            setEnableEditTexts(false);
            buttonState="Edit";
            populateData();
        }

        mEditandSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonListener();
            }
        });
    }

    public void setEnableEditTexts(boolean b){
        mFirstName.setEnabled(b);
        mLastName.setEnabled(b);
        mEmail.setEnabled(b);
        //mHobby.setEnabled(b);
        mEditandSubmit.setText(b ? "Submit" : "Edit");
    }

    public void addButtonListener(){
        if(buttonState.equals("Edit")){
            setEnableEditTexts(true);
            buttonState="Submit";
        }
        else if(buttonState.equals("Submit")){
            updateData();
            setEnableEditTexts(false);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void populateData(){

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(my_uid);
        final String firstName="";
        String lastName="";
        String Email="";
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo info = dataSnapshot.getValue(UserInfo.class);
                mFirstName.setText(info.first_name);
                mLastName.setText(info.last_name);
                mEmail.setText(info.email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mFirstName.setText("");
    }

    public void updateData(){

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(my_uid);
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("first_name", mFirstName.getText().toString());
        userMap.put("last_name", mLastName.getText().toString());
        userMap.put("email",mEmail.getText().toString());
        //userMap.put("Hobby",mHobby.getText().toString());

        mDatabase.updateChildren(userMap);
    }


}
