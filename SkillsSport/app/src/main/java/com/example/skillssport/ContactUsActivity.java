package com.example.skillssport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ContactUsActivity extends AppCompatActivity {

    Button chatButton;
    Button dialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatButtonActivity();
            }
        });

        dialButton = (Button) findViewById(R.id.dialButton);
    }

    public void openChatButtonActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    public void onDialButton(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+96171777777"));
            startActivity(intent);
        } catch (Exception e) {
            //TODO smth
            Log.e("YOUR_APP_LOG_TAG", "I got an error", e);
        }

    }
}
