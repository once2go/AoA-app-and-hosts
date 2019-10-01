package com.once2go.androidto_accessorymode.webusb;


public class Message {
    enum Author {
        ANDROID,
        HOST
    }

    String text;
    String time;
    Author author;

    public Message(String text, String time, Author author) {
        this.text = text;
        this.time = time;
        this.author = author;
    }
}
