package com.once2go.androidto_accessorymode.webusb;

import android.os.Handler;
import android.os.Looper;

import java.io.FileInputStream;
import java.io.IOException;

class ReadThread extends Thread {

    private FileInputStream inputStream;
    private WebUsbChatActivity.ReadResultListener readResultListener;

    public ReadThread(FileInputStream inputStream,
                      WebUsbChatActivity.ReadResultListener readResultListener) {
        this.inputStream = inputStream;
        this.readResultListener = readResultListener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final byte[] buffer = new byte[64];
                final int dataSize = inputStream.read(buffer);
                if (readResultListener != null && dataSize <= 0) return;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        readResultListener.onData(buffer);
                    }
                }, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
