package com.labs.core.entity;

import java.util.Date;

public class Income {
    private int ID;
    private Date Date;
    private String Title;
    private double Value;
    private double ValueTaxed;
    private Exemption Exemption;
    private Tax Tax;
    private User user;

    public Income(){}

    public int getID() {
        return ID;
    }
    public void setID(int iD) {
        ID = iD;
    }

    public Date getDate() {
        return Date;
    }
    public void setDate(Date date) {
        Date = date;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    
    public double getValue() {
        return Value;
    }
    public void setValue(double value) {
        Value = value;
    }

    public double getValueTaxed() {
        return ValueTaxed;
    }
    public void setValueTaxed(double valueTaxed) {
        ValueTaxed = valueTaxed;
    }

    public Exemption getExemption() {
        return Exemption;
    }
    public void setExemption(Exemption exemption) {
        Exemption = exemption;
    }

    public Tax getTax() {
        return Tax;
    }

    public void setTax(Tax tax) {
        Tax = tax;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
