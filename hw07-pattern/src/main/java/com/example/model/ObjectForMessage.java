package com.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectForMessage implements Copyable {
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    private ObjectForMessage(List<String> data) {
        this.setData(data);
    }

    public ObjectForMessage() {

    }

    @Override
    public String toString() {
        return "ObjectForMessage: { " + (data == null ? "" : String.join(",", data)) + " }";
    }

    public ObjectForMessage copy() {
        if (data != null) {
            List<String> destList = new ArrayList<>(data);
            return new ObjectForMessage(destList);
        }
        return null;
    }
}
