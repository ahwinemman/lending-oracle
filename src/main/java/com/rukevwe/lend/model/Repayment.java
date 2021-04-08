package com.rukevwe.lend.model;

import java.util.Date;

public class Repayment {
    private Date date;
    private double amount;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
