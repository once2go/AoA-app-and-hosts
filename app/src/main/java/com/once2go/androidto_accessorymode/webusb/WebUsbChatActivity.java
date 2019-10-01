package com.once2go.androidto_accessorymode.webusb;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

import com.once2go.androidto_accessorymode.ConnectedActivity;
import com.once2go.androidto_accessorymode.R;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;


public class WebUsbChatActivity extends ConnectedActivity implements View.OnClickListener {

    private EditText mUserInputEditText;
    private MessagesAdapter mMessagesAdapter;

    interface ReadResultListener {
        void onData(byte[] data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_usb_chat_layout);
        findViewById(R.id.message_send_button).setOnClickListener(this);
        mUserInputEditText = findViewById(R.id.message_input_edit_text);
        RecyclerView messagesRecView = findViewById(R.id.messages_recycle_view);
        mMessagesAdapter = new MessagesAdapter(messagesRecView);
        messagesRecView.setAdapter(mMessagesAdapter);
    }

    @Override
    protected void onConnected() {
        new ReadThread(inputStream, new WebUsbChatActivity.ReadResultListener() {
            @Override
            public void onData(byte[] data) {
                mMessagesAdapter.addMessage(new Message(new String(data), getTime(), Message.Author.HOST));
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_send_button:
                ByteBuffer buffer = ByteBuffer.allocate(64);
                byte[] data = mUserInputEditText.getText().toString().getBytes();
                if (data.length <= 64) {
                    buffer.put(data);
                    try {
                        outputStream.write(buffer.array());
                        outputStream.flush();
                        mMessagesAdapter.addMessage(new Message(mUserInputEditText.getText().toString(), getTime(), Message.Author.ANDROID));
                        mUserInputEditText.setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Out of buffer bounds", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return hourOfDay + ":" + String.format("%02d", min);
    }
}
