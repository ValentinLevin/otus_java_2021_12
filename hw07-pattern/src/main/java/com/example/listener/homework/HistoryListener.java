package com.example.listener.homework;

import com.example.listener.Listener;
import com.example.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {
    private final Map<Long, Message> messages = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        messages.put(msg.getId(), msg.copy());
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(this.messages.get(id));
    }
}
