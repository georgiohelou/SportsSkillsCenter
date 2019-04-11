package com.example.skillssport;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
//import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 11/17/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> messagesList;
    private HashMap<String,String> uid_name_or_number;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> messagesList){
        View view;
        this.messagesList=messagesList;
        mAuth= FirebaseAuth.getInstance();
    }

    public MessageAdapter(List<Messages> messagesList, HashMap<String,String> uid_name_or_number){
        View view;
        this.messagesList=messagesList;
        mAuth= FirebaseAuth.getInstance();
        this.uid_name_or_number=uid_name_or_number;
    }



    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_layout,parent,false);
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        View view;
        public TextView messageText;
        RelativeLayout single_message_layout;
        ImageView message_image;
        public TextView message_time;
        TextView contact_name;

        public MessageViewHolder(View view){
            super(view);
            this.view=view;
            contact_name=(TextView) view.findViewById(R.id.group_contact_name);
            messageText= (TextView) view.findViewById(R.id.message_text_layout);
            single_message_layout= (RelativeLayout)view.findViewById(R.id.single_message_layout_2);
            message_image=(ImageView)view.findViewById(R.id.message_image);
            message_time=(TextView)view.findViewById(R.id.message_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = messagesList.get(position);

        String from_user = c.getFrom();
        String message_type=c.getType();
        boolean isGroup=c.isGroup();
        Log.d("ISGROUP",isGroup+"");

        if(from_user.equals(current_user_id)){
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            holder.single_message_layout.setLayoutParams(relativeLayoutParams);
            holder.single_message_layout.setBackground((Drawable) holder.view.getResources().getDrawable(R.drawable.message_background_me));

        } else {
            if(isGroup){
                RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeLayoutParams.setMargins(40,75,0,0);
                holder.contact_name.setText(uid_name_or_number.get(from_user));
                holder.messageText.setLayoutParams(relativeLayoutParams);
                Log.d("ISGROUP",uid_name_or_number.get(from_user));
            }
            holder.single_message_layout.setBackground((Drawable) holder.view.getResources().getDrawable(R.drawable.message_background));
        }

        if(message_type.equals("text")) {
            holder.messageText.setText(c.getMessage());
            holder.message_image.setVisibility(View.INVISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);
        }
//        else if(message_type.equals("image")){
//            String encryptedMessage=c.getMessage();
//            String decryptedMessage=encryptDecrypt.decrypt(encryptedMessage);
//
//            holder.messageText.setVisibility(View.INVISIBLE);
//            Picasso.get().load(decryptedMessage).into(holder.message_image);
//            holder.message_image.setVisibility(View.VISIBLE);
//
//            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            relativeLayoutParams.addRule(RelativeLayout.END_OF, R.id.message_image);
//            relativeLayoutParams.setMargins(0,60,20,0);
//            holder.message_time.setLayoutParams(relativeLayoutParams);
//        }

        long timeSinceEpoc=c.getTime();

        Date d = new Date(timeSinceEpoc);
        String minutes =""+d.getMinutes();
        if(d.getMinutes()<10){
            minutes="0"+d.getMinutes();
        }

        holder.message_time.setText(d.getHours()+":"+minutes);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
