package com.example.prif233.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prif233.R;
import com.example.prif233.model.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    public ChatMessageAdapter(@NonNull Context context, @NonNull List<ChatMessage> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.item_chat_message, parent, false);
        }

        ChatMessage msg = getItem(position);

        TextView tvMeta = row.findViewById(R.id.tvMeta);
        TextView tvText = row.findViewById(R.id.tvText);

        if (msg != null) {
            // Adjust these getters to your Android model fields:
            String sender = msg.getSenderLogin(); // OR msg.getMessageSender().getLogin()
            String date = msg.getDateCreated().toString(); // or format it

            tvMeta.setText(date + " • " + sender);
            tvText.setText(msg.getMessageText());
        }

        return row;
    }
}
