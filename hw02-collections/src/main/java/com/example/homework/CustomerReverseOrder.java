package com.example.homework;


import java.util.LinkedList;

public class CustomerReverseOrder {
    private final LinkedList<Customer> list = new LinkedList<>();

    //todo: 2. надо реализовать методы этого класса
    //надо подобрать подходящую структуру данных, тогда решение будет в "две строчки"

    public void add(Customer customer) {
        this.list.push(customer);
    }

    public Customer take() {
        return this.list.pop();
    }
}
