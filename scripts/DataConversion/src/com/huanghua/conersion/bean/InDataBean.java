package com.huanghua.conersion.bean;

import java.util.List;

public class InDataBean {

    public String head;
    public int number;
    public List<String> other;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<String> getOther() {
        return other;
    }

    public void setOther(List<String> other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "head:" + head + " number:" + number + " other:" + other;
    }
}
