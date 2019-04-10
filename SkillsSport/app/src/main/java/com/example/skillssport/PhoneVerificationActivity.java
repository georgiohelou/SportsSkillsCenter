package com.example.skillssport;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_CONTACTS;

public class PhoneVerificationActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;

    private AutoCompleteTextView mPhoneNumber;
    private AutoCompleteTextView mVerification;
    private ProgressBar mPhoneBar;
    private ProgressBar mVerifBar;
    private LinearLayout mActivationLayout;
    private Button mSendVerification;

    int buttonType=0;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        mPhoneNumber=(AutoCompleteTextView)findViewById(R.id.number);
        mVerification=(AutoCompleteTextView)findViewById(R.id.verification);
        mPhoneBar = (ProgressBar)findViewById(R.id.phone_bar);
        mVerifBar = (ProgressBar) findViewById(R.id.activation_bar);
        mActivationLayout=(LinearLayout) findViewById(R.id.activation_layout);
        mSendVerification=(Button)findViewById(R.id.send_verification);

        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference();

        mSendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerification();
            }
        });

        mayRequestContacts();
    }

    private void SendVerification(){
        if (buttonType == 0) {
            mPhoneBar.setVisibility(View.VISIBLE);
            mPhoneNumber.setEnabled(false);
            String phoneNumber = mPhoneNumber.getText().toString();
            mSendVerification.setEnabled(false);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    PhoneVerificationActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            mPhoneNumber.setEnabled(true);
                            mSendVerification.setEnabled(true);
                            mPhoneBar.setVisibility(View.INVISIBLE);
                            mPhoneNumber.setError("Invalid Phone Number");

                        }

                        @Override
                        public void onCodeSent(String verificationId,
                                               PhoneAuthProvider.ForceResendingToken token) {

                            //mPhoneNumber.setError("Code Sent");

                            // Save verification ID and resending token so we can use them later
                            mVerificationId = verificationId;
                            mResendToken = token;

                            buttonType=1;
                            mPhoneBar.setVisibility(View.INVISIBLE);
                            mActivationLayout.setVisibility(View.VISIBLE);

                            mSendVerification.setText("Verify Code");
                            mSendVerification.setEnabled(true);

                            // ...
                        }
                    }
            );
        }
        else{

            mSendVerification.setEnabled(false);
            mVerifBar.setVisibility(View.VISIBLE);

            String verificationCode = mVerification.getText().toString();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);

            signInWithPhoneAuthCredential(credential);

        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            String uid = user.getUid();
                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("number",mPhoneNumber.getText().toString());
                            userMap.put("device_token",device_token);
                            userMap.put("status","Hi there! I'm using Whosapp.");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");
                            mDatabase.setValue(userMap);

                            Intent intent = new Intent(PhoneVerificationActivity.this,ProfileActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mVerifBar.setVisibility(View.INVISIBLE);
                                mSendVerification.setEnabled(true);
                                mVerification.setError("Invalid Code");

                            }
                            else{
                                mVerification.setError("Sign In Failed");
                            }
                        }
                    }
                });
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {

        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


}
