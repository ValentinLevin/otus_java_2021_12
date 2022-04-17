package com.example.crm.model;

import com.example.core.annotations.Id;

import java.util.Objects;

public class Manager {
    @Id
    private Long no;
    private String label;
    private String param1;

    public Manager() {}

    public Manager(String label) {
        this.label = label;
    }

    public Manager(Long no, String label, String param1) {
        this.no = no;
        this.label = label;
        this.param1 = param1;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String toString() {
        return "Manager { "
                + "no = " + this.no
                + ", label = " + this.label
                + ", param1 = " + this.param1
                + " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Manager manager = (Manager) obj;

        return (Objects.equals(manager.getNo(), this.getNo()))
                && (Objects.equals(manager.getLabel(), this.getLabel()))
                && (Objects.equals(manager.getParam1(), this.getParam1()));
    }}
