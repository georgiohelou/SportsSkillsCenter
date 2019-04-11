package com.example.skillssport;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends Activity {

    private WebView mWebView;

    Context thisContext;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private Button myProfileButton;
    private Button mChatButton;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisContext =this;

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        mWebView = findViewById(R.id.activity_main_webview);
        myProfileButton = findViewById(R.id.my_profile);
        mChatButton = findViewById(R.id.chat);

        setButtonSettings();
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // REMOTE RESOURCE
         mWebView.loadUrl("https://sites.google.com/view/skillssportscenter/home");
         //mWebView.setWebViewClient(new MyWebViewClient());

        // LOCAL RESOURCE
        // mWebView.loadUrl("file:///android_asset/index.html");
    }

    // Prevent the back-button from closing the app
    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void setButtonSettings(){

        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, ChatActivity.class);
                startActivity(intent);
            }
        });

        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorFrom = 0xffaaaaaa;
                int colorTo = 0xffFFFFFF;
                int duration = 500;
                ObjectAnimator anim = ObjectAnimator.ofObject(myProfileButton, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo);
                anim.setDuration(duration).start();

                Intent intent = new Intent(thisContext,ProfileActivity.class);
                intent.putExtra("isSignedIn",true);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseAuth.getInstance().signOut();
//        currentUser=null;
        if(currentUser == null){
            Intent phoneVerf = new Intent(MainActivity.this, PhoneVerificationActivity.class);
            startActivity(phoneVerf);
            finish();
        }
    }

}