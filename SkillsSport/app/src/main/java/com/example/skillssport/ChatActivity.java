package com.example.skillssport;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    //AESEncryptDecrypt encryptDecrypt;

    private FirebaseAuth mAuth;
    private DatabaseReference updateStatusRef;
    private DatabaseReference mNotificationRef;
    private String user_uid;
    private String user_name;
    private String my_uid;

    private TextView display_name;
    private TextView last_seen;
    private ImageView display_image;
    private ImageButton video_call;

    private RecyclerView messages_list;
    private final List<Messages> messagesList= new ArrayList<>();
    private LinearLayoutManager linear_layout;
    private MessageAdapter adapter;

    private ImageButton more_button;
    private ImageButton send_button;
    private EditText message_text;

    DatabaseReference rootRef;

    private static final int GALLERY_PICK=1;

    @Override
    protected void onStart() {
        super.onStart();
        updateStatusRef.child("online").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatusRef.child("online").setValue(false);
        updateStatusRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user_uid = "OWNER";
        user_name = "Skills Sports Center Chat";
        mAuth = FirebaseAuth.getInstance();
        my_uid=mAuth.getCurrentUser().getUid();
        updateStatusRef=FirebaseDatabase.getInstance().getReference().child("Users").child(my_uid);
        rootRef=FirebaseDatabase.getInstance().getReference();
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("notifications");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setTitle(user_name);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_menu,null);

        actionBar.setCustomView(action_bar_view);

        display_name=(TextView)findViewById(R.id.contact_display_name);
        last_seen = (TextView)findViewById(R.id.contact_last_seen);

        more_button=(ImageButton)findViewById(R.id.more_button);
        send_button=(ImageButton)findViewById(R.id.send_button);
        message_text=(EditText)findViewById(R.id.message_text);

        adapter=new MessageAdapter(messagesList);
        messages_list=(RecyclerView)findViewById(R.id.messages_list);
        linear_layout= new LinearLayoutManager(this);
        messages_list.setHasFixedSize(true);
        messages_list.setLayoutManager(linear_layout);
        messages_list.setAdapter(adapter);

        loadMessages();



        display_name.setText(user_name);
        last_seen.setText("Chat With Us!");

//        rootRef.child("Users").child(user_uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String online = dataSnapshot.child("online").getValue().toString();
//                String image = dataSnapshot.child("image").getValue().toString();
//
//                if(online.equals("true")){
//                    last_seen.setText("Online");
//                }else{
//                    String lastSeen=dataSnapshot.child("lastSeen").getValue().toString();
//                    long lastSeenLong=Long.parseLong(lastSeen);
//                    Date d = new Date(lastSeenLong);
//                    String minutes =""+d.getMinutes();
//                    if(d.getMinutes()<10){
//                        minutes="0"+d.getMinutes();
//                    }
//
//                    last_seen.setText("last seen at "+d.getHours()+":"+minutes);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        rootRef.child("Chat").child(my_uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.hasChild(user_uid)){
//                    Map chatMap = new HashMap();
//                    chatMap.put("seen",false);
//                    chatMap.put("timeStamp",ServerValue.TIMESTAMP);
//
//                    Map chatUserMap = new HashMap();
//                    chatUserMap.put("Chat/"+user_uid+"/"+my_uid, chatMap);
//                    chatUserMap.put("Chat/"+my_uid+"/"+user_uid, chatMap);
//
//                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if(databaseError!=null){
//                                Log.d("CHAT_LOG",databaseError.getMessage().toString());
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
//        more_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendPhoto();
//            }
//        });
    }

//    private void sendPhoto() {
//        Intent gallery_intent = new Intent();
//        gallery_intent.setType("image/*");
//        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(gallery_intent,"SELECT IMAGE"),GALLERY_PICK);
//    }

    private void loadMessages() {
        rootRef.child("messages").child(my_uid).child(user_uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                adapter.notifyDataSetChanged();
                messages_list.smoothScrollToPosition(messagesList.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(){
        String message=message_text.getText().toString();

        if(!TextUtils.isEmpty(message)){
            String current_user_ref="messages/"+my_uid+"/"+user_uid;
            String chat_user_ref="messages/"+user_uid+"/"+my_uid;

            DatabaseReference user_message_push=rootRef.child("messages").child(my_uid).child(user_uid).push();
            String push_id=user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("group",false);
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",my_uid);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            message_text.setText("");

            HashMap<String,String> notificationData = new HashMap<>();
            notificationData.put("from",my_uid);
            notificationData.put("message",message);

            mNotificationRef.child(user_uid).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Notification","Sent");
                }
            });

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }
                }
            });

        }
    }
}


