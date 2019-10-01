package com.once2go.androidto_accessorymode;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.once2go.androidto_accessorymode.projection.ProjectionDemoActivity;
import com.once2go.androidto_accessorymode.webusb.WebUsbChatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startChatButton = findViewById(R.id.start_web_usb_chat_button);
        Button startProjectionButton = findViewById(R.id.start_projection_button);
        startChatButton.setOnClickListener(this);
        startProjectionButton.setOnClickListener(this);
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbManager.getAccessoryList() == null) return;
        for (UsbAccessory accessory : usbManager.getAccessoryList()) {
            if (accessory.getModel().equals("ProjectionApp")) {
                startProjectionButton.setVisibility(View.VISIBLE);
            } else if (accessory.getModel().equals("WebUsbChat")) {
                startChatButton.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.start_web_usb_chat_button:
                intent = new Intent(this, WebUsbChatActivity.class);
                break;
            case R.id.start_projection_button:
                intent = new Intent(this, ProjectionDemoActivity.class);
                break;
        }
        if (intent != null) {
            UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            intent.putExtra(UsbManager.EXTRA_ACCESSORY, usbManager.getAccessoryList()[0]);
            startActivity(intent);
        }
    }
}
