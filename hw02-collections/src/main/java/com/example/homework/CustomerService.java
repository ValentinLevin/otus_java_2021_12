package com.example.homework;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    private final TreeMap<Customer, String> map = new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    private Map.Entry<Customer, String> cloneEntry(Map.Entry<Customer, String> entry) {
        return entry == null ? null : Map.entry(new Customer(entry.getKey()), entry.getValue());
    }

    public Map.Entry<Customer, String> getSmallest() {
        //Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        return cloneEntry(this.map.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return cloneEntry(this.map.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        this.map.put(new Customer(customer), data);
    }
}
