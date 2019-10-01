package com.once2go.androidto_accessorymode.webusb;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.once2go.androidto_accessorymode.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private RecyclerView messagesRecView;
    private int itemMarginTopBotom, itemMarginLeftRight;

    public MessagesAdapter(RecyclerView messagesRecView) {
        this.messagesRecView = messagesRecView;
        itemMarginTopBotom = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, messagesRecView.getContext().getResources()
                        .getDisplayMetrics());
        itemMarginLeftRight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 64, messagesRecView.getContext().getResources()
                        .getDisplayMetrics());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    void addMessage(Message message) {
        messages.add(message);
        notifyDataSetChanged();
        messagesRecView.scrollToPosition(messages.size() - 1);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView messageTextTextView;
        private TextView messageDateTextView;
        private TextView messageAuthorTextView;
        private CardView messageCardView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.messageTextTextView = itemView.findViewById(R.id.message_text_text_view);
            this.messageDateTextView = itemView.findViewById(R.id.message_time_text_view);
            this.messageCardView = itemView.findViewById(R.id.message_card);
            this.messageAuthorTextView = itemView.findViewById(R.id.message_author_text_view);
        }

        void bind(Message data) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) messageCardView.getLayoutParams();
            int left = data.author == Message.Author.HOST ? itemMarginTopBotom : itemMarginLeftRight;
            int right = data.author == Message.Author.HOST ? itemMarginLeftRight : itemMarginTopBotom;
            params.setMargins(left, itemMarginTopBotom, right, itemMarginTopBotom);
            messageTextTextView.setText(data.text);
            messageDateTextView.setText(data.time);
            messageAuthorTextView.setText(String.format("~ %s", data.author.name().toLowerCase()));
        }
    }
}
